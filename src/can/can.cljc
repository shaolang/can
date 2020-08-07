(ns can.can
  (:require [clojure.set :as set]
            [clojure.string :as str]))

(defn- subset? [xs ys]
  (set/subset? (set xs) (set ys)))


(defn actions->permissions
  ([actions]
   (->> actions
        (map #(map keyword (str/split % #":")))
        (group-by first)
        (map (fn [[k vs]] [k (set (map second vs))]))
        (into {})))
  ([domain-actions actions]
   (let [permissions  (actions->permissions actions)
         domains      (set (keys domain-actions))]
     (if (and (subset? (keys permissions) domains)
              (every? (fn [[k vs]] (subset? vs (get domain-actions k)))
                      permissions))
       permissions
       :unknown-domain-action-found))))


(defn allow? [permissions action]
  (let [[domain action] (->> (str/split action #":")
                             (map keyword))
        actions         (get permissions domain (:* permissions))]
    (boolean (when-not (or (= action :*) (= domain :*))
               (or (some #{action} actions)
                   (some #{:*} actions))))))


(defn permissions->actions [full-permissions permissions]
  (->> permissions
       (map (fn [[k vs]]
              (let [kstr (name k)
                    vs    (if (some #{:*} vs) (get full-permissions k) vs)]
                [k (set (map #(format "%s%s" kstr %) vs))])))
       (into {})))
