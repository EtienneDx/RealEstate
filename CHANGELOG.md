# Changelog

## 1.4.1 - Unreleased

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