package week1

import org.apache.spark.{SparkConf, SparkContext}


/**
  * Created by malarconba001 on 6/11/2016.
  *
  * PiEstimator: Estimates Pi in Spark .. Adapted from: http://spark.apache.org/examples.html
  *
  * Usage:
  *     In your scala console type:
  *
  *         import week1.PiEstimator
  *         PiEstimator.run (1000)
  *
  */

object PiEstimator {
  def run(numSamples:Int) ={
    val conf = new SparkConf()
      .setAppName("week1-EstimatePi")
      .setMaster("local[2]") // Launch 3 local cores
    val sc = new SparkContext(conf)

    val count = sc.parallelize(1 to numSamples).map{i =>
      val x = Math.random()
      val y = Math.random()
      if (x*x + y*y < 1) 1 else 0
    }.reduce(_ + _)
    println("Pi is roughly " + 4.0 * count / numSamples)
    sc.stop()
  }
}
