package code.snippet

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FlatSpec
import org.scalatest.matchers.MustMatchers
import net.liftweb.http.{S, LiftSession}
import net.liftweb.util.Helpers._
import net.liftweb.common.Empty
import code.lib.DependencyFactory

@RunWith(classOf[JUnitRunner])
class HelloWorldScalaTest extends FlatSpec with MustMatchers {
  val session = new LiftSession("", randomString(20), Empty)
  val stableTime = now

  override def withFixture(test: NoArgTest) {
    S.initIfUninitted(session) {
      DependencyFactory.Time.doWith(stableTime) {
        super.withFixture(test) // execute t inside a http session
      }
    }
  }

  it should "Put the time in the node" in {
    val hello = new HelloWorld
    Thread.sleep(1000) // make sure the time changes

    val str = hello.howdy(<span>Welcome to your Lift app at <span id="time">Time goes here</span></span>).toString

    str.indexOf(stableTime.toString) must be >= 0
    str must startWith("<span>Welcome to")
  }

}