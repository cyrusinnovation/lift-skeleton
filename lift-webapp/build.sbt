name := "Lift skeleton website"

version := "0.0.1"

scalaVersion := "2.10.0"

resolvers ++= Seq("maven-central-repo" at "http://repo1.maven.org/maven2",
                  "snapshots"          at "http://oss.sonatype.org/content/repositories/snapshots")

seq(webSettings :_*)

unmanagedResourceDirectories in Test <+= (baseDirectory) { _ / "src/main/webapp" }

scalacOptions ++= Seq("-deprecation", "-unchecked")

libraryDependencies ++= {
  val liftVersion = "2.5.1"
  Seq(
    "net.liftweb"               %% "lift-webkit"            % liftVersion           % "compile",
    "net.liftmodules"           %% "lift-jquery-module_2.5" % "2.4",
    "ch.qos.logback"             % "logback-classic"        % "1.0.6",
    "junit"                      % "junit"                  % "4.11"                % "test",
    "org.scalatest"              % "scalatest_2.10"         % "2.0.M5b"             % "test",
    "org.specs2"                %% "specs2"                 % "1.14"                % "test",
    "org.eclipse.jetty"          % "jetty-webapp"           % "8.1.7.v20120910"     % "container,test",
    "org.eclipse.jetty.orbit"    % "javax.servlet"          % "3.0.0.v201112011016" % "container,test" artifacts Artifact("javax.servlet", "jar", "jar")
  )
}

org.scalastyle.sbt.ScalastylePlugin.Settings