(ns expense-tracker.logic.utils)

(defn keywordize-keys [data]
  (cond
    (map? data) (into {} (for [[k v] data]
                           [(keyword k) (keywordize-keys v)]))
    (coll? data) (mapv keywordize-keys data)
    :else data))

(defn assoc-some
  ([m k v]
   (cond-> m (some? v) (assoc k v)))
  ([m k v & kvs]
   (let [ret (assoc-some m k v)]
     (if kvs
       (if (next kvs)
         (recur ret (first kvs) (second kvs) (nnext kvs))
         (throw (IllegalArgumentException.
                  "assoc-some expects even number of arguments after map/vector, found odd number")))
       ret))))

(defn tap
  [v]
  (println v)
  v)

(defn query-config
  []
  {:ranges         {:july      {:start "A" :end "E" :month-number 7}
                    :august    {:start "F" :end "J" :month-number 8}
                    :september {:start "K" :end "O" :month-number 9}
                    :october   {:start "P" :end "T" :month-number 10}}
   :lines          {:start-row 3
                    :end-row   52}
   :spreadsheet-id (System/getenv "SHEET_ID")
   :api-key        (System/getenv "API_KEY")
   :cards          #{"NU CAROL (28)" "VISA ROSA (06)" "NU ANDRE (16)" "INFINITY (11)" "DEBITO (30)"}})