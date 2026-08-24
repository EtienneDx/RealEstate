# Changelog

# Version 1.4.5 (2026-08-24)

## Bug Fixes

- **Sign destruction ignored config:** `DestroySigns.Rent` and `DestroySigns.Lease` in `config.yml` stopped being honored after the 1.4.2 claim API refactor — signs were always destroyed on rent/lease regardless of the setting. Both are now respected again. (#84, #87)
- **`/re info` could crash on an orphaned transaction:** if the claim tied to a sign's transaction could no longer be found (e.g. the claim was resized, abandoned, or deleted while a transaction was still open), previewing that transaction threw an unhandled `NullPointerException` instead of showing an error. Sell, rent, lease, and auction previews now report the problem to the player and log a warning instead of crashing. (#87)
- **Parent claim purchases always reported an error:** buying a parent (non-sub) claim always sent the buyer an "unexpected error" message even though the purchase and ownership transfer had already succeeded. The stale post-transfer verification causing this has been removed.
- **The project failed to build entirely:** the `GriefDefenderAPI` dependency was pinned to a JitPack build of a commit that no longer exists upstream after a history rewrite, so it could never be resolved. Repointed to the real `com.griefdefender:api` coordinate GriefDefender actually publishes, with its already-shaded transitive dependencies excluded.

## New Features

- **PlaceholderAPI support:** exposes `%realestate_claim_rent_amount%`, `%realestate_claim_sell_amount%`, and `%realestate_claim_lease_amount%`, showing the price of the transaction sign at a player's current location.
- **Automatic claim snapshot/restore for rent and lease:** when a claim is put up for rent or lease, its blocks are now snapshotted automatically; once a tenant's rental period or lease ends (expiry without renewal, failed payment, or admin-forced cancellation), the claim is restored back to that snapshot, undoing anything the tenant built or destroyed. This does not apply when a lease completes and ownership transfers to the buyer — the new owner's changes are kept. Controlled by the new `RealEstate.Rules.ClaimSnapshots` config option (default: enabled). Currently only implemented for GriefDefender (via its `ClaimSnapshot` API); GriefPrevention, WorldGuard, and Towny claims are unaffected and behave exactly as before.
- **Per-player sell/rent/lease limits:** admins can now cap, per player, how many claims someone can simultaneously have listed for sale/rent/lease (`RealEstate.Default.Limit.Sell.Owner`, `.Rent.Owner`, `.Lease.Owner`), how many they can simultaneously be renting/leasing (`.Rent.Buyer`, `.Lease.Buyer`), and how many they may ever purchase outright (`.Sell.Buyer`, a lifetime total). All six default to `-1` (unlimited). The five concurrency-based limits are derived directly from the live transaction store and work identically on every supported claim provider (GriefPrevention, GriefDefender, WorldGuard, Towny); the lifetime purchase count is tracked in RealEstate's own transaction data store (file or database, matching whichever backend is already configured) rather than on the claim itself, so it too is provider-agnostic. Ported and adapted from `bloodmc/realestate@4e34dff`; that fork's per-rank LuckPerms meta overrides were intentionally left out, since this codebase has no existing LuckPerms integration and adding one is a separate dependency decision — limits here are global, config-only.

## Improvements

- **Dependency alignment:** bumped the `spigot-api` dependency from 1.21.1 to 1.21.4 to match `paper-api` and the plugin's declared `api-version`.
- **CI:** the build workflow's `actions/upload-artifact@v3` step was hard-deprecated by GitHub and failed before checkout even ran; bumped to v4.

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