# can
[![Clojars Project](https://img.shields.io/clojars/v/can.svg)](https://clojars.org/can)

A permissions library for Clojure(Script), inspired by
[agynamix/permissions][permissions].

__Library is still undergoing development, so expect frequent changes
to the API.__

## Basic Usage

The core API lives in `can.can` (not `can.core`) and is recommended
to be aliased as `can`, i.e., `(require '[can.can :as can])`.

Permissions is a map of sets, where the key is the domain, and the
values the rights applicable to that domain. `can.can/allow?` takes
the permissions map and an domain/action string to determine whether
the user is allowed to perform the said action:

```clojure
(def permissions {:support #{:create-ticket :update-ticket}
                  :printer #{:print}})


(print (can/allow? permissions "support:create-ticket"))    ;; outputs "true"
(print (can/allow? permissions "print:clear-spool"))        ;; outputs "false"
```

You could also use `:*` to grant permissions:

```clojure
(def super-support {:support #{:*}})

(print (can/allow? super-support "support:launch-nuclear"))   ;; outputs "true"
(print (can/allow? super-support "print:clear-spool"))        ;; outputs "false"
```

And even superuser:

```clojure
(def superuser {:* #{:*}})

(print (can/allow? superuser "hello:world"))      ;; outputs "true"
```

Unlike permissions, actions cannot use `*`, i.e., doing
`(can/allow? permissions "*:create-ticket")` or
`(can/allow? permissions "admin:*")` will always return `false`.

## Bitmasks permissions
__Breaking change in 0.3.0: bitmask-related functions reside in `can.bitmask`
now, and they are renamed as decode and encode.__

`can.bitmask/decode` takes the full permissions setup (i.e.,
all available domains and actions available in your application) and
converts a map of domain-bitmask pair into a permissions map.

```clojure
(def all-permissions {:admin   [:create :read :update :delete :approve :reject]
                      :support [:create-ticket :update-ticket :close-ticket]
                      :printer [:print :clear-spool]})


(def alice-permissions
  (can/decode all-permissions {:admin 7 :printer 1}))

(print alice-permissions)   ;; outputs {:admin #{:create :read :update}
                            ;;          :printer #{:print}}
```

Note that the order of the available actions in each domain matters, i.e.,
new actions should be appended to the end of the action list.

`can.bitmask/decode` makes it trivial to implement
access control list/matrix in your application, e.g., the relational
database could have a table that looks like the following:

<table>
  <thead>
    <tr>
      <th>userid</th>
      <th>admin</th>
      <th>support</th>
      <th>printer</th>
    </tr>
  </thead>
  <tbody>
    <tr><td>alice</td><td>7</td><td>0</td><td>1</td></tr>
    <tr><td>bob</td><td>0</td><td>7</td><td>2</td></tr>
  </tbody>
</table>

Note that when using bitmasks permissions, the total number of actions
per domain cannot exceed 63 on Clojure, and cannot exceed 52 on
ClojureScript.

`can.bitmask/encode` converts the permissions map into a map of
domain-bitmask pair (somewhat the inverse of `can.bitmask/decode`),
making it easy to persist changes back to the datastore:

```clojure
(print (encode all-permissions alice-permissions))  ;; outputs {:admin 7
                                                    ;;          :support 0
                                                    ;;          :printer 1}
```

## License

Copyright © 2019 Shaolang Ai

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

[permissions]: https://github.com/tuhlmann/permissions
