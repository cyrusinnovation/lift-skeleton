# Lift Skeleton

This is a skeleton for developing Lift applications as multi-module Gradle or sbt projects; unit
tests using ScalaTest and specs2 are demonstrated, as well as static code analysis using the 
Scalastyle style checker. The skeleton also includes a module for running Selenium tests driven 
by Cucumber for Scala or by ScalaTest. For sbt, the scct code coverage tool, along with its
associated plugin, is also included.

The Lift project is based on the lift_blank template project included in the Lift distribution.

## Table of Contents
* [Configuring IntelliJ IDEA using Gradle](#gradleconfig)
* [Configuring IntelliJ IDEA using sbt](#sbtconfig)
* [Running the server and the tests in IDEA](#usingidea)
* [Working with Gradle](#usinggradle)
* [Working with sbt](#usingsbt)
* [Reference](#reference)

## Configuring IntelliJ IDEA using Gradle<a name="gradleconfig"/>

If you prefer to use sbt, see below. The Gradle IDEA plugin generates file-based project
information; i.e., .ipr, .iml, and .iws files.

### What you need:

1. IntelliJ IDEA with the Scala, Gradle, and Cucumber-Java plugins.
2. An installation of Gradle you can run from the command line (ideally 1.7 or later)
3. This git repository.

### How-to:

1. We are going to use Gradle to bootstrap this process. The following steps will make use of the `gradle`
command that comes with the installation, in this project's root directory (the same directory as this README file).

    NOTE: The Gradle build relies on the gradle-scalastyle-plugin for Scala 2.10, which is not yet
    in a public Maven repository. To get the jar, you will need to git clone
    [https://github.com/cyrusinnovation/gradle-scalastyle-plugin] and then run

        gradle install

    to install the jar file to your local Maven repository, from which the lift-skeleton build can pick it up.
2. Retrieve all the library dependencies needed for the projects by entering the command

		gradle build

    This will take awhile to download all the jar files it needs.
3. Now generate the IDEA configurations using the command

		gradle idea

	This will download a lot of source jar files before creating the configurations.
4. You should now be able to use IDEA to open the lift-skeleton.ipr file in the root directory of this project.
5. The Gradle idea plugin makes the project's build output go to an `out/` subdirectory of the root project.
If you want to use the same directory as is used by the command-line Gradle build, in the IDEA project
settings, change that directory to `build/`.
6. If you want to clean out the IDEA project and module files before starting over, use:

        gradle cleanIdea

    If you want to clean out the IDEA workspace file (the .iws file), use:

        cleanIdeaWorkspace

## Configuring IntelliJ IDEA using sbt<a name="sbtconfig"/>

This is an alternative to using Gradle to configure IDEA. It generates directory-based IDEA project information.

### What you need:

1. IntelliJ IDEA with the Scala, sbt, and Cucumber-Java plugins.
2. This git repository.
3. (Optional) Version 0.12.4 of sbt (http://www.scala-sbt.org/) installed somewhere IntelliJ IDEA can see it.

### How-to:

1. We are going to use sbt to bootstrap this process. You can either install your own sbt or use the jar that
comes with the sbt plugin for IDEA. (It will be wherever IDEA keeps its cache--e.g., on OS X, the jar is
located at: /Users/[USER]/Library/Caches/IntelliJIdea12/sbt/sbt-launch.jar)

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

2. You need to get the library dependencies for all the projects and create IntelliJ IDEA project and module
configuration files. In the root directory of this project (the same directory as this README file),
at the command line enter:

	    sbt gen-idea

	This should begin the download of a whole bunch of jar files. This will 
	take awhile. The last steps in the process will involve creating the .iml files.

3. You should now be able to open the root directory of this project as a project in IDEA.

## Running the server and the tests in IDEA<a name="usingidea"/>

1. To run the web application in IDEA, run the RunWebApp class in lift-webapp/src/test/scala -- you will
need to change the Working Directory setting in the Run Configuration to the lift-webapp directory
(it defaults to the project root). You should see a page at http://localhost:8080/index .

    You can specify the web browser for running the integration tests by adding system
    properties to the "VM Options" in the Run configurations. For example, for Chrome,
    you might add:

     -Dweb.driver.type="chrome" -Dchrome.driver.path="/usr/bin/chromedriver" -Dwebdriver.chrome.bin="/usr/bin/chrome/"

2. To run the ScalaTest Selenium tests in IDEA, run the HelloWorldIntegrationTests class in
selenium-tests/src/test/scala/code/test/selenium . The test uses the HtmlUnit driver, so you
will not see a browser window pop up.

3. To run the Cucumber Selenium tests in IDEA, right-click on HelloWorld.feature in
selenium-tests/src/test/resources/features and run it. To make it work, you will need
to change the Program Arguments setting in the Run Configuration to add:

	    --glue code.test.cucumber

## Working with Gradle<a name="usinggradle"/>

1. To build and run the tests, use:

        gradle test

    or

        gradle build

    Note that gradle builds artifacts to build/ subdirectories of the main project and of each module and not
    to target/ directories in the manner of Maven and sbt.

    These targets do not run Cucumber tests. They will, however, run the ScalaTest and specs2 unit tests and
    the ScalaTest integration tests.

    These targets also do not currently run the web server before the tests.

    The browser to be used with the integration tests can be specified using system properties set on the
    Gradle command line. For example:

        gradle -Dweb.driver.type="firefox" test

    Chrome requires more parameters:

        gradle -Dweb.driver.type="chrome" -Dchrome.driver.path="/usr/bin/chromedriver" -Dwebdriver.chrome.bin="/usr/bin/chrome/" test

    If no browser is specified, the HtmlUnit driver is used. For more details, see the comments at the top of
    `WebDriverFactory.scala`.

    Tests are by default run against http://localhost:8080/ -- if you need to change this in your tests, set the
    value of the default.base.url system property. See the comments at the top of DefaultBaseURLProvider.scala for
    details.

2. To run the web server from gradle, use:

        gradle jettyRun

    Note that running the web server from Gradle can lock Gradle cache files and interfere with running other
    Gradle processes.

3. To run just the Cucumber tests, again without running the web server, use:

        gradle cucumber

    You can specify the web browser to use with cucumber too:

        gradle -Dweb.driver.type="firefox" cucumber

4. To run Scalastyle, use:

        gradle scalaStyle

    Currently Scalastyle fails the build on errors but not on warnings. Output is sent to a file named scala_style_result.xml in
    the build/ subdirectory of each module.

5. To add more modules to the project, add their names to the `include` directive in settings.gradle.

## Working with sbt<a name="usingsbt"/>

1. To build and run the tests, use:

        sbt test

    The `sbt test` target is configured to run the ScalaTest and specs2 unit tests, the ScalaTest integration
    tests and the Cucumber tests; however, the configuration isn't currently working for the Cucumber tests
    for some reason I have been unable to identify.

    The `sbt test` target also does not currently run the web server before the tests, so you will need either
    to do that manually or implement the cucumberBefore and cucumberAfter settings in selenium-tests/build.sbt.

    Like Maven and unlike Gradle, sbt builds artifacts to target/ subdirectories of the main project
    and of each module.

2. To run just the Cucumber tests (this works), use the command:

        sbt cucumber

3. To run Scalastyle from sbt, use the command:

        sbt scalastyle

    A basic Scalastyle configuration has already been generated and is in scalastyle-config.xml. You can regenerate this
    file by entering the command:

        sbt scalastyle-generate-config

4. To run scct from sbt, clean first, then use the scct:test command:

        sbt clean scct:test

    This will generate coverage reports in each module's target/scala_<ver>/coverage-report/ directory. To merge all
    the child reports into one, use the command:

        sbt scct-merge-report

    The merged report will be created under the top-level target/scala_<ver>/coverage-report/ directory.

5. To add more modules to the project, edit the Build.scala file under the top-level project/ directory. Note that you
will need to add not only a new val specifying the module as a Project, but also to use that val in the call to the
aggregate method for the root project.

## Reference<a name="reference"/>

For more information on the how and why of this git repository, see:

http://www.scala-sbt.org/0.12.3/docs/Getting-Started/Multi-Project.html
http://www.playframework.com/documentation/2.0/SBTSubProjects
http://www.decodified.com/scala/2010/10/12/an-integrated-sbt-and-idea-scala-dev-setup.html
(This one is out-of-date, but a lot of the step-by-step was helpful in putting the above instructions together.)
