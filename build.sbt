lazy val root = (project in file(".")).
  enablePlugins(ScalaJSPlugin).
  settings(

    name := "DATA643",

    version := "1.0",

    scalaVersion := "2.11.8",


// http://mvnrepository.com/artifact/org.apache.spark/spark-mllib_2.11
    libraryDependencies += "org.apache.spark" %% "spark-core" % "1.6.1",
    libraryDependencies += "org.apache.spark" %% "spark-mllib" % "1.6.1",
    libraryDependencies += "org.apache.spark" %% "spark-sql" % "1.6.1",
    libraryDependencies += "org.jsoup" % "jsoup" % "1.9.2"
  )


