Feature: In order easily to set up new Lift projects
         I want to have a working skeleton project
         So that I can just get it from source control and start working.

  Scenario: Lift skeleton home page comes up
    Given I have browsed to the home page
    Then I should see the welcome text

  Scenario: Lift static content page is reachable
    Given I have browsed to the home page
    When I click the "Static Content" link
    Then I should see the Static Content page
    And I should see the static content description text