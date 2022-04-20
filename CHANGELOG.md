# Changelog

## 1.5.0 - Unreleased

### Added
* Support for GriefDefender 2.0.0
* Added possibility to create auctions
    * Auctions last a given duration
    * Players can bid a specific amount or raise their bid by a `bidStep` defined by the auctioneer
    * Format for new auctions is:
        ```
        [auction]
        <starting price>
        <bid step>
        <duration>
        ```
    * An auction with no bid won't change owner and will expire

### Removed
* Remove confusing *rent period* behavior
* Remove logs whenever a sign is interacted with

### Fixed
* Fixed typo in config

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