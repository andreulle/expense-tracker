(ns expense-tracker.controller.expense
  (:require [expense-tracker.diplomat.datomic :as diplomat.datomic]
            [expense-tracker.logic.expense :as logic.expense]
            [expense-tracker.logic.utils :as logic.utils]
            [expense-tracker.diplomat.http-in :as http-in]
            [expense-tracker.adapters.expense :as adapters.expense]))

(defn get-google-sheets-data+card
  [api-key
   spreadsheet-id
   {:keys [range card month-number]}]
  {:card card
   :month-number month-number
   :values (http-in/get-google-sheets-data api-key spreadsheet-id range)})

(defn get-google-sheets-by-month
  [cards
   month
   lines
   api-key
   spreadsheet-id]
  (->> cards
       (map (partial adapters.expense/ranges+lines->full-range+card month lines))
       (map (partial get-google-sheets-data+card api-key spreadsheet-id))
       (map adapters.expense/response->models)))

(defn get-expenses-from-sheets
  []
  (let [{:keys [lines spreadsheet-id api-key cards ranges]} (logic.utils/query-config)]
    (->> (flatten
          (concat (for [[_ v] ranges]
                    (get-google-sheets-by-month cards v lines api-key spreadsheet-id))))
        (map adapters.expense/model->datomic)
        seq
        (diplomat.datomic/save-expenses))))

(defn initialize []
  (println "\n Initializing DB...")
  (println (diplomat.datomic/setup-schema))
  (println "\n Getting data from Google Sheets...")
  (get-expenses-from-sheets))

(defn get-expenses-from-datomic
  [month]
  (let [expenses (diplomat.datomic/find-all-expenses)]
    (->> expenses
         (map :sub-category)
         distinct
         (map (partial logic.expense/group-sum expenses month))
         (filter #(> (:total-amount %) 0))
         (sort-by :total-amount #(compare %2 %1)))))
