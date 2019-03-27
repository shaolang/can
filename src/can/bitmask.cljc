(ns can.bitmask
  (:require [clojure.string :as str]))


(defn- actions->bitmask-map [actions]
  (zipmap (iterate #(* 2 %) 1) actions))


(defn- on-all-bitmasks [actions]
  (dec (int (Math/pow 2 (count actions)))))


(defn- bitmask->granted-actions [bitmask actions]
  (if (= bitmask (on-all-bitmasks actions))
    #{:*}
    (let [bitmask-actions (actions->bitmask-map actions)
          granted-actions (for [[action-bit action] bitmask-actions]
                            (when (pos? (bit-and bitmask action-bit))
                              action))
          granted-actions (filter (complement nil?) granted-actions)]
      (when-not (empty? granted-actions)
        (into #{} (filter (complement nil?) granted-actions))))))

(defn- parse-long [s]
  #?(:clj  (Long/parseLong s)
     :cljs (js/parseInt s)))


(defn decode [all-permissions domain-bitmasks]
  (let [permissions (for [[domain bitmask] domain-bitmasks]
                      (let [actions         (get all-permissions domain)
                            granted-actions (bitmask->granted-actions bitmask
                                                                      actions)]
                        (when-not (nil? granted-actions)
                          (hash-map domain granted-actions))))]
    (apply merge {} permissions)))


(defn- granted-actions->bitmask [granted-actions all-domain-actions]
  (if (= granted-actions #{:*})
    (on-all-bitmasks all-domain-actions)
    (let [bitmask-actions (actions->bitmask-map all-domain-actions)]
      (transduce (comp (filter (fn [[_ action]] (some #{action} granted-actions)))
                       (map first))
                 +
                 bitmask-actions))))


(defn encode [all-permissions granted-permissions]
  (->> all-permissions
       (map (fn [[domain all-actions]]
              [domain (granted-actions->bitmask (get granted-permissions domain)
                                                all-actions)]))
       (into {})))
