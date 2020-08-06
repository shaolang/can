(ns can.can-test
  (:require #?(:clj  [clojure.test :refer [deftest is testing]]
               :cljs [cljs.test :refer-macros [deftest is testing]])
            [can.can :as can]))

(deftest allow?-test
  (let [permissions {:admin   #{:create :read :update :delete}
                     :support #{:create-ticket :close-ticket}
                     :audit   #{:*}}]
    (doseq [action ["admin:create"      ;; specified domain and known action
                    "audit:print-log"]] ;; specified domain with wildcard action
      (testing (str "should allow " action)
        (is (true? (can/allow? permissions action)))))


    (doseq [action ["admin:approve" ;; specified domain but unknown action
                    "misc:print"    ;; unknown domain and unknown action
                    "admin:*"       ;; specified domain and wildcard action
                    "*:create"]]    ;; wildcard domain
      (testing (str "should not allow " action)
        (is (false? (can/allow? permissions action))))))


  (let [permissions {:* #{:*}}]   ;; [almost] anything allowed
    (testing "allows everything"
      (is (true? (can/allow? permissions "hello:world"))))

    (testing "user domain cannot be a wildcard"
      (is (false? (can/allow? permissions "*:delete"))))

    (testing "user action cannot be a wildcard"
      (is (false? (can/allow? permissions "misc:*"))))))


(deftest actions->permissions-test
  (testing "with no full domain/action map given"
    (is (= {:admin    #{:create :read}
            :support  #{:close-ticket}}
           (can/actions->permissions
             ["admin:create" "admin:read" "support:close-ticket"]))))

  (testing "identical domain/action names are handled properly"
    (is (= {:support #{:support}}
           (can/actions->permissions ["support:support"])))))
