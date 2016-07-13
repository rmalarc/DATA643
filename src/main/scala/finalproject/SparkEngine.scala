package finalproject

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.SQLContext

import scala.scalajs.js
import scala.scalajs.js.annotation.{ JSExportAll}

/**
  * Created by malarconba001 on 7/10/2016.
  */

// let's define the spark engine as it's own object that will be extended to result into the document classifier
@JSExportAll
object SparkEngine{
  var sc: SparkContext = new SparkContext(
    new SparkConf()
      .setAppName("Datasiv")
      .setMaster("local[2]")
  )
  val sqlContext = new SQLContext(sc)
}
