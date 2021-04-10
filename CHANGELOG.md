# Changelog

## Unreleased
### New features

* `can.can/allow?` supports domain-only checks; this simplifies code that
  checks whether a domain is (generally) permissible, e.g., to determine
  whether to display the admin UI (of admin domain). While this could be
  done with `(contains? permissions domain-key)`, the intent is clearer
  with `can.can/allow? permissions domain-key)`.
