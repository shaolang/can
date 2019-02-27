(ns can.can
  (:require [clojure.string :as str]))


(defn allow? [permissions action]
  (let [[domain action] (->> (str/split action #":")
                             (map keyword))
        actions         (get permissions domain (:* permissions))]
    (boolean (when-not (or (= action :*) (= domain :*))
               (or (some #{action} actions)
                   (some #{:*} actions))))))


(defn- bitmask->granted-actions [bitmask actions]
  (let [bitmask-actions (zipmap (iterate #(* 2 %) 1) actions)
        granted-actions (for [[action-bit action] bitmask-actions]
                          (when (pos? (bit-and bitmask action-bit))
                            action))
        granted-actions (filter (complement nil?) granted-actions)]
    (when-not (empty? granted-actions)
      (into #{} (filter (complement nil?) granted-actions)))))


(defn bitmask-actions->permissions [all-permissions domain-bitmasks]
  (let [bitmasks (into [] (comp (map #(str/split % #":"))
                                (map (fn [[k v]] [(keyword k)
                                                  (Long/parseLong v)])))
                       domain-bitmasks)
        permissions (for [[domain bitmask] bitmasks]
                      (let [actions         (get all-permissions domain)
                            granted-actions (bitmask->granted-actions bitmask
                                                                      actions)]
                        (when-not (nil? granted-actions)
                          (hash-map domain granted-actions))))]
    (apply merge {} permissions)))
