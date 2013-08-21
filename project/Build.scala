import sbt._

object SkeletonBuild extends Build {
    lazy val root = Project(id = "rootProject",
                            base = file(".")) aggregate(lift_webapp, selenium_tests)

    lazy val lift_webapp = Project(id = "lift-webapp",
                                    base = file("lift-webapp"))

    lazy val selenium_tests = Project(id = "selenium-tests",
                                      base = file("selenium-tests"))
}
