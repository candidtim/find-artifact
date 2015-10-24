(ns findartifact.util)


(defn assoc-if [m k v]
  (if (nil? m)
    nil
    (assoc m k v)))
