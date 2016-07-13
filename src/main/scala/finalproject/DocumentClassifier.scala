package finalproject

import org.apache.spark.ml.feature.{HashingTF, IDF, Normalizer, PCA, StopWordsRemover, Tokenizer}
import org.apache.spark.mllib.linalg.SparseVector
import org.apache.spark.ml.Pipeline

import scala.scalajs.js
import scala.scalajs.js.annotation.JSExportAll


//  This is the docuemnt Classifier engine which contains the following attributes and methods:
// * documentClassifierTrainingData: It's a list of documentCategories and documentText
// * appendDocumentCategory: It appends to the trainingdata
// * clearDocumentCategories: Resets the training data
// * classifyDocument: Takes a documentText and returns the cosine similarity with the existing categories
@JSExportAll
object DocumentClassifier{
  var documentClassifierTrainingData: List[(String, String)] = List()

  def appendDocumentCategory(docType:String, docText: String) = {
    documentClassifierTrainingData = documentClassifierTrainingData ::: List(
      (docType,
        docText.toLowerCase.replaceAll("(?is)[^a-z]"," ").replaceAll("(?is) +"," ")
        )
    )
  }

  def clearDocumentCategories = {
    documentClassifierTrainingData = List()
  }


  def classifyDocument(docText: String) = {
    val predictData = List(
      ("CLASSIFYME",
        docText
        )
    )

    // get all training data and create a dataframe out of it
    val allData = SparkEngine.sqlContext.createDataFrame(documentClassifierTrainingData ::: predictData).toDF("docType", "docText")

    // Let's configure the pipeline stages: Tokenizer, Stop WordsRemover, HashingTf with 500 features, IDF normalization and PCA with
    val tokenizer = new Tokenizer()
      .setInputCol("docText")
      .setOutputCol("words")

    val remover = new StopWordsRemover()
      .setInputCol(tokenizer.getOutputCol)
      .setOutputCol("filtered")

    val hashingTF = new HashingTF()
      .setNumFeatures(500)
      .setInputCol(remover.getOutputCol)
      .setOutputCol("hashed")

    val idf = new IDF()
      .setInputCol(hashingTF.getOutputCol)
      .setOutputCol("idfFeatures")

    val pca = new PCA()
      .setInputCol(idf.getOutputCol)
      .setOutputCol("features")
      .setK(350) // parameter value selection comes from last project


    // LEt's now use the SparkML Pipeline in order to chain the transformations
    val pipeline = new Pipeline()
      .setStages(Array(tokenizer, remover, hashingTF, idf, pca))

    pipeline.write

    // Fit the pipeline to training documents.
    val model = pipeline.fit(allData)


    // and get the output data
    val hashedData = model
      .transform(allData)
      .select("docType", "features")

    // with the output features, let's now separate the unknown document from the training documents. The goal is to calculate
    // the cosine simmilarity of the new document against every other featurized document in the training dataset

    val unknownDocument = hashedData.filter("""docType ="CLASSIFYME" """).collect.map(r => (r.getAs[String]("docType"), r.getAs[SparseVector]("features").toDense.toArray))
    val trainingDocuments = hashedData.filter("""docType <> "CLASSIFYME" """).collect.map(r => (r.getAs[String]("docType"), r.getAs[SparseVector]("features").toDense.toArray))

    // The stuff below calculates the cosine similarities between the unknown document and each of the documents in the training dataset
    val unknownDocumentsWithLengths = unknownDocument map {
      td =>
        (td._1,
          td._2,
          Math.sqrt(td._2.foldLeft(0.0)((T, r) => T + r * r))
          )
    }
    val documentsWithLengths = trainingDocuments map {
      td =>
        (td._1,
          td._2,
          Math.sqrt(td._2.foldLeft(0.0)((T, r) => T + r * r))
          )
    }
    val distances = documentsWithLengths map {
      td =>
        (td._1,
          (0 to td._2.length - 1).map(i => unknownDocumentsWithLengths.head._2(i) * td._2(i))
            .reduce((T, v) => T+v)
            / (td._3 * unknownDocumentsWithLengths.head._3)
          )
    }
    distances.sortBy(-_._2)
  }

}


