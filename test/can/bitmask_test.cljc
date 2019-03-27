(ns can.bitmask-test
  (:require #?(:clj  [clojure.test :refer [deftest is testing]]
               :cljs [cljs.test :refer-macros [deftest is testing]])
            [can.bitmask :as bitmask]))

(deftest decode-test
  (let [all-permissions {:admin [:create :read :update :delete]
                         :support [:create-tix :read-tix :edit-tix :delete-tix]
                         :printers [:print]}
        bitmasks {:admin 5 :support 4 :printers 0}]
    (is (= {:admin #{:create :update}
            :support #{:edit-tix}}
           (bitmask/decode all-permissions bitmasks)))

    (is (= {:admin #{:*}}
           (bitmask/decode all-permissions {:admin 15})))))


(deftest encode-test
  (let [all-permissions {:admin [:create :read :update :delete]
                         :support [:create-tix :read-tix :edit-tx :delete-tix]}
        user-permissions {:admin #{:create :delete}
                          :support #{:*}
                          :printer #{:print}}]
    (is (= {:admin 9 :support 15}
           (bitmask/encode all-permissions user-permissions)))

    (is (= {:admin 1 :support 0}
           (bitmask/encode all-permissions {:admin #{:create}})))))
