(ns expense-tracker.logic.expense
  (:require [schema.core :as s]
            [expense-tracker.logic.utils :as l]))

(s/defn sum-amount-expenses
  [expenses]
  (->> expenses
       (map :amount)
       seq
       (reduce +)))

(s/defn group-sum
  [expenses
   month
   group]
  {:group        group
   :total-amount (->> expenses
                      (filter #(= group (:sub-category %)))
                      (filter #(= month (:month-number %)))
                      sum-amount-expenses
                      )})