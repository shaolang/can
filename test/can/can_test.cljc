(ns can.can-test
  (:require #?(:clj  [clojure.test :refer [are deftest is testing]]
               :cljs [cljs.test :refer-macros [are deftest is testing]])
            [can.can :as can]))

(deftest allow?-test
  (let [permissions {:admin   #{:create :read :update :delete}
                     :support #{:create-ticket :close-ticket}
                     :audit   #{:*}}]
    (are [action expected] (= (can/allow? permissions action) expected)

         ;; input             expected
         "admin:create"       true      ;; specified domain and known action
          "audit:print-log"   true      ;; specified domain with wildcard action
          "admin:approve"     false     ;; specified domain but unknown action
          "misc:print"        false     ;; unknown domain and unknown action
          "*:create"          false))   ;; wildcard domain

  (let [permissions {:* #{:*}}]   ;; [almost] anything allowed
    (are [action expected] (= (can/allow? permissions action) expected)

        ;;input               expected
        "hello:world"         true      ;; allows everything
        "*:delete"            false     ;; domain cannot be a wildcard
        "misc:*"              false)))  ;; user action cannot be a wildcard


(deftest actions->permissions-test
  (testing "with no full domain/action map given"
    (is (= {:admin    #{:create :read}
            :support  #{:close-ticket}}
           (can/actions->permissions
             ["admin:create" "admin:read" "support:close-ticket"]))))

  (testing "identical domain/action names are handled properly"
    (is (= {:support #{:support}}
           (can/actions->permissions ["support:support"]))))

  (testing "with full domain-action map given with no unknown action given"
    (is (= {:admin    #{:delete}
            :support  #{:edit-ticket}}
           (can/actions->permissions {:admin   #{:create :read :update :delete}
                                      :support #{:create-ticket :edit-ticket}}
                                     ["admin:delete" "support:edit-ticket"]))))

  (testing "with full domain-action map given and unknown action given"
    (is (= :unknown-domain-action-found
           (can/actions->permissions {:admin    #{:create :read}
                                      :support  #{:create-ticket}}
                                     ["admin:create" "support:edit-ticket"])))))


(deftest permissions->actions-test
  (is (= {:admin    #{"admin:create" "admin:read" "admin:update" "admin:delete"}
          :support  #{"support:create-ticket"}}
         (can/permissions->actions {:admin    #{:create :read :update :delete}
                                    :support  #{:create-ticket :edit-ticket}}
                                   {:admin #{:*}
                                    :support #{:create-ticket}}))))
