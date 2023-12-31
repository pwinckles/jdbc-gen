# Changelog

## [Unreleased] - ReleaseDate

## [1.1.0] - 2023-08-07

### Breaking changes

- The mechanism for specifying the sort order has changed

### Added

- Select, count, and delete operations now support filtering
- Add support for records
- Add support for ordering by multiple fields
- Add basic enum support
- Add pagination support
- Add `patch()` function on db classes as a convenience for creating patch objects

## [1.0.1] - 2023-07-26

### Fixed

- Correctly support quoted column/table names

### Added

- `@Generated` annotation on all generated types

## [1.0.0] - 2023-06-28

Initial release

[Unreleased]: https://github.com/pwinckles/jdbc-gen/compare/v1.1.0...HEAD
[1.1.0]: https://github.com/pwinckles/jdbc-gen/compare/v1.0.1...v1.1.0
[1.0.1]: https://github.com/pwinckles/jdbc-gen/compare/v1.0.0...v1.0.1
[1.0.0]: https://github.com/pwinckles/jdbc-gen/releases/tag/v1.0.0