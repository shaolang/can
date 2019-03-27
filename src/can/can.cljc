(ns can.can
  (:require [clojure.string :as str]))


(defn allow? [permissions action]
  (let [[domain action] (->> (str/split action #":")
                             (map keyword))
        actions         (get permissions domain (:* permissions))]
    (boolean (when-not (or (= action :*) (= domain :*))
               (or (some #{action} actions)
                   (some #{:*} actions))))))
