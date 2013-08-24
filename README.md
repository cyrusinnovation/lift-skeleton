# Lift Skeleton

This is a skeleton for developing Lift applications as multi-module sbt projects. The skeleton also includes a module for running Selenium tests driven by Cucumber for Scala and ScalaTest. Scalastyle and its associated sbt plugin are also included.

## Importing into IntelliJ IDEA

How to repeatably create a multi-module Scala project in IntelliJ IDEA:

### What you need:

1. IntelliJ IDEA with the Scala, sbt, and Cucumber-Java plugins.
2. This git repository.
3. (Optional) Version 0.12.4 of sbt (http://www.scala-sbt.org/) installed somewhere IntelliJ IDEA can see it.

### How-to:

1. We are going to use sbt to bootstrap this process. You can either install your own sbt or use the jar that comes with the sbt plugin for IDEA. (It will be wherever IDEA keeps its cache--e.g., on OS X, the jar is located at: /Users/[USER]/Library/Caches/IntelliJIdea12/sbt/sbt-launch.jar .)

	The following shell script will run sbt on Unix-y systems:

		java -Xmx513M -XX:MaxPermSize=256M -jar [PATH_TO_SBT_JAR_DIR]/sbt-launch.jar "$@"

	At the time this is being written, the jar file that ships with the sbt
	plugin for IDEA was built with Scala 2.9; if you reference this jar, you
	will get a warning during the next step to the effect that "Binary version
	(2.9.2) for dependency org.scala-lang#scala-library;2.9.2... differs
	from Scala binary version in project (2.10)."

	To avoid this warning and make everything use Scala 2.10 consistently,
	install version 0.12.4 of sbt and point the shell script at its
	sbt-launch.jar instead. You may also want to change the configuration of
	the IDEA sbt plugin to use that jar as well; this setting may be found
	in the IDEA preferences under Project Settings > SBT.

2. You need to get the library dependencies for all the projects and create IntelliJ IDEA project and module configuration files. In the root directory of this project (the same directory as this README file), at the command line enter:

	    sbt gen-idea

	This should begin the download of a whole bunch of jar files. This will 
	take awhile. The last steps in the process will involve creating the .iml files.

3. You should now be able to open the root directory of this project as a project in IDEA. 

4. To run the web application, run the RunWebApp class in lift-webapp/src/test/scala -- you will need to change the Working Directory setting in the Run Configuration to the lift-webapp directory (it defaults to the project root). You should see a page at http://localhost:8080/index .

5. To run the Selenium tests using ScalaTest, run the HelloWorldIntegrationTests class in selenium-tests/src/test/scala/code/test/selenium . The test uses the HtmlUnit driver, so you will not see a browser window pop up.

6. To run the Selenium tests using Cucumber, right-click on HelloWorld.feature in selenium-tests/src/test/resources/features and run it. To make it work, you will need to change the Program Arguments setting in the Run Configuration to add:

	    --glue code.test.cucumber

7. To run Scalastyle from sbt, use the command:

        sbt scalastyle

    A basic Scalastyle configuration has already been generated and is in scalastyle-config.xml. You can regenerate this file by entering the command:

        sbt scalastyle-generate-config

### Reference

For more information on the how and why of this git repository, see:

http://www.scala-sbt.org/0.12.3/docs/Getting-Started/Multi-Project.html
http://www.playframework.com/documentation/2.0/SBTSubProjects
http://www.decodified.com/scala/2010/10/12/an-integrated-sbt-and-idea-scala-dev-setup.html
(This one is out-of-date, but a lot of the step-by-step was helpful in putting the above instructions together.)
