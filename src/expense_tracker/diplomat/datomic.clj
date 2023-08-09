(ns expense-tracker.diplomat.datomic
  (:require [expense-tracker.adapters.expense :as adapters.expense]
            [datomic.api :as d]))

(def db-uri "datomic:mem://expense")

(d/create-database db-uri)

(def conn (d/connect db-uri))

@(d/transact conn [{:db/doc "expense"}])

(def expense-schema [{:db/ident       :expense/card
                      :db/valueType   :db.type/string
                      :db/cardinality :db.cardinality/one
                      :db/doc         "Card Name"}

                     {:db/ident       :expense/month-number
                      :db/valueType   :db.type/long
                      :db/cardinality :db.cardinality/one
                      :db/doc         "Month in number"}

                     {:db/ident       :expense/description
                      :db/valueType   :db.type/string
                      :db/cardinality :db.cardinality/one
                      :db/doc         "expense description"}

                     {:db/ident       :expense/category
                      :db/valueType   :db.type/string
                      :db/cardinality :db.cardinality/one
                      :db/doc         "expense category"}

                     {:db/ident       :expense/sub-category
                      :db/valueType   :db.type/string
                      :db/cardinality :db.cardinality/one
                      :db/doc         "expense sub-category"}

                     {:db/ident       :expense/amount
                      :db/valueType   :db.type/bigdec
                      :db/cardinality :db.cardinality/one
                      :db/doc         "expense amount"}

                     {:db/ident       :expense/installment
                      :db/valueType   :db.type/string
                      :db/cardinality :db.cardinality/one
                      :db/doc         "expense installment"}])

(defn setup-schema
  []
  @(d/transact conn expense-schema))

(defn save-expenses
  [expenses]
  @(d/transact conn expenses))

(def db (d/db conn))

(defn find-all-expenses
  []
  (->> (d/q '[:find (pull ?e [*])
              :where [?e :expense/card]]
            db)
       flatten
       (map adapters.expense/datomic->model)))

(defn find-by-category+month
  [category
   month]
  (->> (d/q '[:find (pull ?e [*])
              :in $ ?cat ?month
              :where [?e :expense/card]
              [?e :expense/category ?cat]
              [?e :expense/month-number ?month]]
            db category month)
       flatten
       (map adapters.expense/datomic->model)))

(defn find-by-sub-category+month
  [category
   month]
  (->> (d/q '[:find (pull ?e [*])
              :in $ ?sub-cat ?month
              :where [?e :expense/card]
              [?e :expense/sub-category ?sub-cat]
              [?e :expense/month-number ?month]]
            db category month)
       flatten
       (map adapters.expense/datomic->model)))