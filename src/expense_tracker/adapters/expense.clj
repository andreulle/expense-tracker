(ns expense-tracker.adapters.expense
  (:require
    [expense-tracker.logic.utils :as logic.utils]
    [schema.core :as s]))

(s/defn ranges+lines->full-range+card
  [{:keys [start end month-number]}
   {:keys [start-row end-row]}
   card]
  {:range (str card "!" start start-row ":" end end-row)
   :card card
   :month-number month-number})

(defn str->dec
  [value]
  (-> value
      (clojure.string/replace "," ".")
      bigdec))

(defn row->model
  [card
   month-number
   row]
  (when-not (empty? (get row 1))
    (logic.utils/assoc-some {:card card
                             :month-number       month-number
                             :description (get row 0)
                             :category    (get row 1)
                             :amount (str->dec (get row 3))}
                            :sub-category (get row 2)
                            :installment (get row 4))))

(defn model->datomic
  [{:keys [card month-number description category amount sub-category installment]}]
   (logic.utils/assoc-some {:expense/card         card
                           :expense/month-number month-number
                           :expense/description  description
                           :expense/category     category
                           :expense/amount       amount}
                          :expense/sub-category sub-category
                          :expense/installment installment))

(defn datomic->model
  [{:expense/keys [card month-number description category amount sub-category installment]}]
  (logic.utils/assoc-some {:card         card
                           :month-number month-number
                           :description  description
                           :category     category
                           :amount       amount}
                          :sub-category sub-category
                          :installment installment))

(defn response->models
  [{:keys [card month-number values]}]
  (->> values
       seq
       (map (partial row->model card month-number))
       (filter (complement empty?))))

