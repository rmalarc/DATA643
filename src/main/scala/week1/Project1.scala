package week1
/*
classpath.add( "org.apache.spark" %% "spark-core" % "1.6.1",
             "org.apache.spark" %% "spark-mllib" % "1.6.1",
              "org.apache.spark" %% "spark-sql" % "1.6.1")
*/
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql._
import org.apache.spark.sql.types.{StructType,StructField,StringType,IntegerType}
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.linalg.distributed.{MatrixEntry, RowMatrix}


object Project1 {
  def run = {
    val conf = new SparkConf()
      .setAppName("week1-EstimatePi")
      .setMaster("local")
    val sc = new SparkContext(conf)


    // Read the CSV file
    val csv = sc.textFile("src/main/scala/week1/MovieRatings - Clean.csv")
    csv.collect

    // split / clean data
    val separatedValues = csv.map(line => line.split(",").map(t => if (t.trim == "") 0.00 else t.trim.toDouble))

    separatedValues.collect

    // get header
    val movies = List("CaptainAmerica", "Deadpool", "Frozen", "JungleBook", "PitchPerfect2", "StarWarsForce") //separatedValues.first
    val users = List("Burton", "Charley", "Dan", "Dieudonne", "Matt", "Mauricio", "Max", "Nathan", "Param", "Parshu", "Prashanth", "Shipra", "Sreejaya", "Steve", "Vuthy", "Xingjia") //separatedValues.first

    // filter out header (eh. just check if the first val matches the first header name)
    //val data = separatedValues.mapPartitionsWithIndex { (idx, iter) => if (idx == 0) iter.drop(1) else iter }
    val data = separatedValues

    // remove the header
    data.collect

    //val sqlContext = new org.apache.spark.sql.SQLContext(sc)
    //import sqlContext._
    //import sqlContext.implicits._


    val userMoviesMatrix = new RowMatrix(data.map(line => Vectors.dense(line)))

    val moviesMoviesCosineDistance = userMoviesMatrix.columnSimilarities()

    val moviesMoviesSimilarities = moviesMoviesCosineDistance
      .entries
      .map {
        case MatrixEntry(i, j, u) => (i, j, u) }
      .map(r => Seq(movies(r._1.toInt), movies(r._2.toInt), r._3.toDouble))
      .collect
      .sortBy(-_(2).asInstanceOf[Double])

    val dataTransposed =  sc.parallelize(data.collect.toSeq.transpose)

    val moviesUserMatrix = new RowMatrix(dataTransposed.map(line => Vectors.dense(line.toArray)))

    val userUserCosineDistance = moviesUserMatrix.columnSimilarities()

    val userUserSimilarities = userUserCosineDistance
      .entries
      .map {
        case MatrixEntry(i, j, u) => (i, j, u) }
      .map(r => Seq(users(r._1.toInt), users(r._2.toInt), r._3.toDouble))
      .collect
      .sortBy(-_(2).asInstanceOf[Double])


    //querying the model:
    userUserSimilarities.filter(r=> r(0) == "Mauricio" || r(1)=="Mauricio").map(r=>(if (r(1)=="Mauricio") r(0) else r(1), r(2)))

    val user= "Mauricio"

    userUserSimilarities
      .filter(r=> r(0) == user || r(1)==user).map(r=>(if (r(1)==user) r(0) else r(1), r(2).asInstanceOf[Double]))
      .filter(_._2>0.7)



    /// ALS
    import org.apache.spark.mllib.recommendation.ALS
    import org.apache.spark.mllib.recommendation.MatrixFactorizationModel
    import org.apache.spark.mllib.recommendation.Rating

    // Load and parse the data
    val dataALS = sc.textFile("src/main/scala/week1/MovieRatings - Long.csv")
    val ratingsALS = dataALS.map(_.split(',') match { case Array(critic, movie, rate) =>
      Rating(critic.toInt, movie.toInt, rate.toDouble)
    })

    // Build the recommendation model using ALS
    val rank = 10
    val numIterations = 10
    val model = ALS.train(ratingsALS, rank, numIterations, 0.01)

    // Evaluate the model on rating data
    val criticsMovies = ratingsALS.map { case Rating(critic, movie, rate) =>
      (critic, movie)
    }
    val predictions =
      model.predict(criticsMovies).map { case Rating(critic, movie, rate) =>
        ((critic, movie), rate)
      }
    val ratesAndPredictions = ratingsALS.map { case Rating(critic, movie, rate) =>
      ((critic, movie), rate)
    }.join(predictions)
    val MSE = ratesAndPredictions.map { case ((critic, movie), (r1, r2)) =>
      val err = (r1 - r2)
      err * err
    }.mean()
    println("Mean Squared Error = " + MSE)

    val csv1 = sc.textFile("src/main/scala/week1/MovieRatings.csv")
    csv.collect
    sc.stop
  //userMoviesMatrix.

    ",,,,".replaceAll("$,","0,")

  }
}