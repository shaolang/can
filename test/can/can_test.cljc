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


(deftest bitmask-actions->permissions-test
  (let [all-permissions {:admin [:create :read :update :delete]
                         :support [:create-tix :read-tix :edit-tix :delete-tix]
                         :printers [:print]}
        bitmasks ["admin:5" "support:4" "printers:0"]]
    (is (= {:admin #{:create :update}
            :support #{:edit-tix}}
           (can/bitmask-actions->permissions all-permissions bitmasks)))))
