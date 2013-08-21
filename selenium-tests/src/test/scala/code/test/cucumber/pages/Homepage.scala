package code.test.cucumber.pages

object Homepage extends PageObject {
  protected val namedTexts = Map("welcome" -> "Welcome to your project!")

  def path = "/index"
}
