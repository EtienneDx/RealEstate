# Changelog

# Version 1.4.5 (2026-08-24)

## Bug Fixes

- **Sign destruction ignored config:** `DestroySigns.Rent` and `DestroySigns.Lease` in `config.yml` stopped being honored after the 1.4.2 claim API refactor — signs were always destroyed on rent/lease regardless of the setting. Both are now respected again. (#84, #87)
- **`/re info` could crash on an orphaned transaction:** if the claim tied to a sign's transaction could no longer be found (e.g. the claim was resized, abandoned, or deleted while a transaction was still open), previewing that transaction threw an unhandled `NullPointerException` instead of showing an error. Sell, rent, lease, and auction previews now report the problem to the player and log a warning instead of crashing. (#87)
- **Parent claim purchases always reported an error:** buying a parent (non-sub) claim always sent the buyer an "unexpected error" message even though the purchase and ownership transfer had already succeeded. The stale post-transfer verification causing this has been removed.
- **The project failed to build entirely:** the `GriefDefenderAPI` dependency was pinned to a JitPack build of a commit that no longer exists upstream after a history rewrite, so it could never be resolved. Repointed to the real `com.griefdefender:api` coordinate GriefDefender actually publishes, with its already-shaded transitive dependencies excluded.

## New Features

- **PlaceholderAPI support:** exposes `%realestate_claim_rent_amount%`, `%realestate_claim_sell_amount%`, and `%realestate_claim_lease_amount%`, showing the price of the transaction sign at a player's current location.
- **Automatic claim snapshot/restore for rent and lease:** when a tenant moves into a rented or leased claim (right before they're granted access), its blocks are now snapshotted automatically; once that tenant's rental period or lease ends (expiry without renewal, failed payment, or admin-forced cancellation), the claim is restored back to that snapshot, undoing anything the tenant built or destroyed. The snapshot is taken at move-in time rather than when the sign is first listed, so any changes the owner makes to their own claim after listing it but before a tenant moves in are preserved rather than wiped. This does not apply when a lease completes and ownership transfers to the buyer — the new owner's changes are kept. Controlled by the new `RealEstate.Rules.ClaimSnapshots` config option (default: enabled). Currently only implemented for GriefDefender (via its `ClaimSnapshot` API); GriefPrevention, WorldGuard, and Towny claims are unaffected and behave exactly as before.
- **Per-player sell/rent/lease limits:** admins can now cap, per player, how many claims someone can simultaneously have listed for sale/rent/lease (`RealEstate.Default.Limit.Sell.Owner`, `.Rent.Owner`, `.Lease.Owner`), how many they can simultaneously be renting/leasing (`.Rent.Buyer`, `.Lease.Buyer`), and how many they may ever purchase outright (`.Sell.Buyer`, a lifetime total). All six default to `-1` (unlimited). The five concurrency-based limits are derived directly from the live transaction store and work identically on every supported claim provider (GriefPrevention, GriefDefender, WorldGuard, Towny); the lifetime purchase count is tracked in RealEstate's own transaction data store (file or database, matching whichever backend is already configured) rather than on the claim itself, so it too is provider-agnostic. An auction is treated as a sale for both of these: listing an auction counts against `.Sell.Owner` and winning one counts against `.Sell.Buyer`, exactly like a sell sign. None of the owner-side limits (`.Sell.Owner`, `.Rent.Owner`, `.Lease.Owner`) apply to admin-claim listings, since those are never attributed to the listing admin's personal count. Ported and adapted from `bloodmc/realestate@4e34dff`; that fork's per-rank LuckPerms meta overrides were intentionally left out, since this codebase has no existing LuckPerms integration and adding one is a separate dependency decision — limits here are global, config-only.
- **Map-integration data hook for GriefDefender:** added `IClaimAPI#getTransaction(UUID)`, letting a claim be looked up by its provider-specific unique ID and returning the RealEstate transaction (price, buyer, period) currently associated with it, if any. This is purely a data-access hook — RealEstate does not add or render any map UI itself; it exists so that GriefDefender's own built-in BlueMap/Dynmap claim-popup integration can pull RealEstate's transaction info when a server has it enabled. Implemented for GriefDefender, which can resolve a claim from its UUID directly; GriefPrevention, WorldGuard, and Towny have no existing by-ID claim lookup in this codebase (only by-location), so they return `null`.

## Improvements

- **Dependency alignment:** bumped the `spigot-api` dependency from 1.21.1 to 1.21.4 to match `paper-api` and the plugin's declared `api-version`.
- **CI:** the build workflow's `actions/upload-artifact@v3` step was hard-deprecated by GitHub and failed before checkout even ran; bumped to v4.
- **JDK toolchain raised from 16 to 25.** Paper's own build (as of Minecraft's new 26.x calendar-based version scheme) compiles against a JDK 25 toolchain, and WorldGuard/WorldEdit/Towny's latest releases publish JDK 25 (class file 69) bytecode — unreadable by the previous JDK 16 toolchain. JDK 25 reads everything from old JDK 16 class files up through the newest dependencies, so a single toolchain covers every supported target below with no per-target JDK matrix needed. `maven-compiler-plugin` was also bumped (`3.8.0` → `3.13.0`) for reliable JDK 25 support, and both GitHub Actions workflows' `java-version` were updated to match.
- **Multi-target build support: RealEstate now builds against four Minecraft/Paper/Spigot targets — 1.21.1, 1.21.4, 1.21.11, and the current 26.x release (26.1.2 and newer).** `pom.xml` gained `minecraft.api.version`, `spigot.api.version`, and `paper.api.version` properties (parameterizing the `api-version:` field in `plugin.yml`/`paper-plugin.yml` and the `spigot-api`/`paper-api` dependency versions via the same resource-filtering mechanism already used for `version: ${project.version}`), so any one target can be built with `mvn package -D<property>=<value>...` without editing source. `.github/workflows/build.yml` (runs on every push) now builds all four targets in a parallel matrix and uploads each as a separately-named artifact (`RealEstate-<target>-<plugin-version>.jar`); `.github/workflows/build.yaml` (the PR sanity check) stays a single build on the pom's defaults (the latest/26.x target).
  - **Spigot has not published a `spigot-api` under the new 26.x version scheme** (confirmed via Paper's own docs: `docs.papermc.io`'s project-setup guide notes the version-string format changed at `26.1`/`1.21.11`, and Paper's `paper-api` build declares an outgoing Gradle capability conflict against `org.spigotmc:spigot-api` at the same version specifically to say the two are alternatives, not a signal that Spigot itself ships one at that version). 1.21.11 is, as of this writing, the newest Minecraft version with a real `spigot-api` release. The 26.x build target is therefore Paper-only: the new `skip.spigot.api` property (default `true`) omits the `spigot-api` dependency entirely for that target via a Maven profile, while the three older targets keep both `spigot-api` and `paper-api` together as before.
  - **Two latent classpath conflicts surfaced while wiring this up, both now fixed:** EssentialsX transitively pulls `org.bukkit:bukkit:1.12.2-R0.1-SNAPSHOT` and WorldGuard transitively pulls `org.spigotmc:spigot-api:1.16.2-R0.1-SNAPSHOT` — both pre-date the 1.20 sign-side API (`Sign#getSide`/`SignSide`) this codebase uses. These were always present but only started actually losing Maven's classpath-ordering race once dependency resolution shifted under the JDK 25 / multi-target changes; both are now explicitly excluded via `<exclusions>` so only this project's own explicitly-declared, correctly-versioned `spigot-api`/`paper-api` can ever provide those classes. Separately, this project's own pinned `net.kyori:adventure-api:4.18.0` dependency was removed entirely: it isn't imported by this project's own source (GriefDefender integration code uses GriefDefender's shaded `com.griefdefender.lib.kyori.adventure.*` copy instead), and pinning it directly shadowed paper-api's own transitively-supplied Adventure version via Maven's "nearest wins" mediation, which broke compilation against the 26.x target specifically (paper-api's `Player` interface referenced newer Adventure classes the old pinned jar didn't have). Each target now resolves whatever Adventure version paper-api (and spigot-api, where present) actually supplies.

## Tooling

- **Local test server script:** `scripts/run-server.sh <target>` (targets: `1.21.1`, `1.21.4`, `1.21.11`, `26.1.2`/`latest`, matching the multi-target build's matrix above) downloads the matching Paper server jar via PaperMC's Fill downloads API (`fill.papermc.io`, the successor to the old `api.papermc.io`), builds the plugin for that target using the same `mvn package -D...` invocation as `build.yml`, drops the freshly-built jar into `servers/<target>/plugins/`, accepts the EULA, and launches the server — so changes can be tested in-game instead of only compile-checked in CI. Paper server jars are used for every target, including the three pre-26.x ones, since Paper is a strict superset that runs Spigot plugins fine and sourcing separate Spigot/CraftBukkit jars via BuildTools would be far slower for a local dev loop. `servers/` is gitignored.

## Dependency Updates

- **GriefPrevention: `16.18.4` → `17.0.0`** (skipped `18.0.0`). The API surface this codebase actually calls (`Claim#getID/getArea/isAdminClaim/getLesserBoundaryCorner/setPermission/dropPermission/clearPermissions/setSubclaimRestrictions`, the `children`/`parent`/`managers`/`ownerID` fields, `ClaimPermission`, `DataStore#getClaimAt/saveClaim/getPlayerData/changeClaimOwner`, `PlayerData`'s claim-block getters/setters, and the `ClaimDeletedEvent`/`ClaimPermissionCheckEvent` classes) is byte-for-byte unchanged between `16.18.4` and `17.0.0` — confirmed by diffing the real source at both tags in `GriefPrevention/GriefPrevention` on GitHub. `18.0.0` was deliberately not taken: its `pom.xml` sets `maven.compiler.release=21` (up from `16` in `17.0.0`, matching this project's own `16` target and the CI JDK at the time), a hard requirement change that risked the JDK 16 CI toolchain being unable to compile against it, and it also reorders the `ClaimPermission` enum so `Manage` grants `Build`/`Inventory`/`Access` (previously a separate track that granted nothing) — a real behavior change to permission semantics this codebase relies on via `GPClaim#addPlayerPermissions`. `17.0.0` gets a full major version closer to current with none of that risk. (Now that the JDK toolchain is 25, `18.0.0`'s JDK 21 requirement is no longer a blocker — but its `ClaimPermission` behavior change stands regardless of JDK, so it's still not taken here without a deliberate look at that semantics shift.)
- **WorldGuard `7.0.5` → `7.0.18`, WorldEdit `7.2.0` → `7.4.5`, Towny `0.101.1.0` → `0.103.2.0`.** Source-level API diffing (same method as above) had already shown no incompatible usage for any of the three; the only blocker was that their published artifacts are JDK 25-compiled class files the previous JDK 16 toolchain couldn't read at all (`class file has wrong version 69.0, should be 60.0`). Now that the toolchain is JDK 25 (see above), all three build and compile cleanly across every supported target in CI.

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