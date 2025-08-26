# Copyright (c) 2024-2025 SUSE LLC
# Licensed under the terms of the MIT license.

@sle15sp6_minion
Feature: Migrate a SLES 15 SP6 Salt minion to 15 SP7

  Scenario: Log in as admin user
    Given I am authorized for the "Admin" section

  Scenario: Prerequisite: update OS Salt to the latest version
    Given I am on the Systems overview page of this "sle15sp6_minion"
    When I follow "Software" in the content area
    And I follow "Packages"
    And I follow "Upgrade"
    And I enter "salt" as the filtered latest package
    And I click on the filter button
    And I click on "Select All"
    And I click on "Upgrade Packages"
    And I click on "Confirm"
    Then I should see a "package upgrades have been scheduled" text
    And I wait until event "Package Install/Upgrade scheduled" is completed

  Scenario: Prerequisite: update OS zypper to the latest version
    Given I am on the Systems overview page of this "sle15sp6_minion"
    When I follow "Software" in the content area
    And I follow "Packages"
    And I follow "Upgrade"
    And I enter "zypp" as the filtered latest package
    And I click on the filter button
    And I click on "Select All"
    And I click on "Upgrade Packages"
    And I click on "Confirm"
    Then I should see a "package upgrades have been scheduled" text
    And I wait until event "Package Install/Upgrade scheduled" is completed

  Scenario: Migrate this minion to SLE 15 SP7
    Given I am on the Systems overview page of this "sle15sp6_minion"
    When I follow "Software" in the content area
    And I follow "Product Migration" in the content area
    And I wait until I see "Target Products:" text, refreshing the page
    And I wait until I see "SUSE Linux Enterprise Server 15 SP7 x86_64" text
    And I click on "Select Channels"
    And I check "allowVendorChange"
    And I click on "Schedule Migration"
    Then I should see a "Product Migration - Confirm" text
    When I click on "Confirm"
    Then I should see a "This system is scheduled to be migrated to" text

  Scenario: Check the migration is successful for this minion
    Given I am on the Systems overview page of this "sle15sp6_minion"
    When I follow "Events"
    And I follow "History"
    And I wait at most 600 seconds until event "Product Migration" is completed
    And I wait until event "Package List Refresh" is completed
    And I follow "Details" in the content area
    Then I wait until I see "SUSE Linux Enterprise Server 15 SP7" text, refreshing the page
    And vendor change should be enabled for product migration on "sle15sp6_minion"

  Scenario: Detect latest Salt changes on the SLES minion
    When I query latest Salt changes on "sle15sp6_minion"

  Scenario: Check events history for failures on SLES minion
    Given I am on the Systems overview page of this "sle15sp6_minion"
    Then I check for failed events on history event page
