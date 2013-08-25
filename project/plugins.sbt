resolvers ++= Seq("snapshots"               at "http://oss.sonatype.org/content/repositories/snapshots",
                  "maven-central-repo"      at "http://repo1.maven.org/maven2",
                  "sbt-idea-repo"           at "http://mpeltonen.github.com/maven",
                  "scct-github-repository"  at "http://mtkopone.github.com/scct/maven-repo",
                  "Templemore Repository"   at "http://templemore.co.uk/repo")


// Plugin for web application support
addSbtPlugin("com.earldouglas" % "xsbt-web-plugin" % "0.3.0")

// Plugin for IntelliJ IDEA
addSbtPlugin("com.github.mpeltonen" % "sbt-idea" % "1.5.1")

// Plugin for eclipse
addSbtPlugin("com.typesafe.sbteclipse" % "sbteclipse-plugin" % "2.2.0")

// Plugin for scalastyle
addSbtPlugin("org.scalastyle" %% "scalastyle-sbt-plugin" % "0.3.2")

// Plugin for scct code coverage tool
addSbtPlugin("reaktor" % "sbt-scct" % "0.2-SNAPSHOT")

// Plugin for Cucumber
addSbtPlugin("templemore" % "sbt-cucumber-plugin" % "0.7.2")