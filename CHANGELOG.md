# Changelog

# Version 1.4.3 (2025-02-14)

## New Features

- **Admin Claim Detection:**
  - Improved the WorldGuard integration so that claims with no owner (i.e. an empty owner field) are now treated as admin claims.
  - In such cases, the claim’s owner is set to a constant `SERVER_UUID` (and displayed as "SERVER") for consistency in sign updates and transaction validations.

- **Instant Sign Update for Rent/Lease:**
  - Modified the ClaimRent and ClaimLease update logic to update signs instantly—bringing their behavior in line with ClaimSell—so that players see the correct information immediately after placement.

## Bug Fixes

- **Owner Verification Issue:**
  - Fixed an issue where players were incorrectly receiving the “You can only sell/rent/lease claim you own!” message on sign interaction.
  - The plugin now correctly determines claim ownership (including admin claims) using the updated WGClaim methods.

- **Database Migration for Admin Claims:**
  - Updated the SQL insert logic so that if a claim is detected as an admin claim, the owner field is set to the SERVER identifier.
  - This change ensures consistent behavior when loading data from the database.

- **WorldGuard Integration Problems:**
  - Resolved compatibility issues with WorldGuard and WorldEdit by updating our WGClaim implementation and adjusting our import statements.
  - The plugin now properly retrieves regions and their flags using the latest WorldGuard API.

## Improvements

- **Code Refactoring:**
  - Cleaned up various sections of the code for better readability and maintainability.
  - Improved error handling and logging to make troubleshooting easier.

- **Build & Dependency Updates:**
  - Updated the `pom.xml` to ensure proper integration with the latest versions of WorldGuard, WorldEdit, Vault, and other dependencies.

- **Documentation & Messaging:**
  - Revised in-game messages and log outputs to provide clearer feedback for both players and administrators.


# Version 1.4.2 (2025-02-13)
### Admin Claim Support Improvements:
* When processing ClaimRent transactions, if a claim is identified as an admin claim, its owner is now set to "SERVER" (using a fixed UUID or identifier) to ensure correct behavior.
* The INSERT statements for ClaimRent now conditionally set the owner to SERVER if the claim is an admin claim.

### Sign Update Consistency:
* Modified ClaimSell to update its sign immediately after creation.
* Updated ClaimRent to also perform an immediate sign update (by scheduling a one-tick delay) so that rental signs display correct information instantly.
* Updated ClaimLease to also perform an immediate sign update (by scheduling a one-tick delay) so that rental signs display correct information instantly.
* Updated ClaimAuction to also perform an immediate sign update (by scheduling a one-tick delay) so that rental signs display correct information instantly.

### Database Handling Enhancements:
* Fixed issues with UUID parsing when loading transactions from the database.
* Improved error checking for owner values during data load to prevent invalid UUID strings.

### Code Quality and Refactoring:
* Cleaned up repeated code between transaction types and centralized common behavior (e.g., sign updating and logging).
** Added missing getter methods for ClaimAuction, ClaimLease, ClaimRent, and ClaimSell to support proper database operations.
* Renamed the old transactions.data file to transactions.yml. (The plugin will automatically reformat if the old file is detected)
** This will be helpful for future upgrades.

### Dependency and Compatibility:
* Ensured compatibility with the latest versions of Vault, EssentialsX, GriefPrevention, and GriefDefender.
** Added paper-plugin.yml to ensure Paper servers load the plugins in the correct order.
** Updated the plugin's plugin.yml and paper-plugin.yml for API version 1.21.4.

### General Bug Fixes:
* Resolved a bug where the sign for [sell] transactions remained blank.
* Addressed potential null pointer exceptions during claim data loading.
* Improved logging to capture and record transaction events more clearly.

## 1.4.1
### Added
* Added support for multiple languages files within the jar
* Added `pt-br` as a language option

### Fixed
* Fixed #47 regarding `/re info` command formating
* Fixed #50 regarding error with `/re renewrent` on claims with no buyer
* Fixed #51 regarding a duplicate prefix on `/re info`

## 1.4.0
### Added
* Readme and changelog files
* Error messages to *messages.yml*
* List messages to *messages.yml*
* Info messages to *messages.yml*
* Support of [GriefPrevention v16.18](https://github.com/TechFortress/GriefPrevention/releases/tag/16.18) and up
* Disabled resizing of parent claims when subclaims are being rented

### Modified
* Changed java version to java 16
* Changed spigot version to 1.18.1 (should still support 1.17)
* Removed requirement for custom GP jar file 

### Fixed
* Sign header color formatting being lost on server restart
* Fixed issue preventing to buy claims due to currencies using $ character
* Fixed error with `/re list`
* Fixed error regarding renewrent