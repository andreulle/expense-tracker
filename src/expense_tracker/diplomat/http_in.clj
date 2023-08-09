(ns expense-tracker.diplomat.http-in
  (:require [clj-http.client :as http]
            [cheshire.core :as json]
            [expense-tracker.logic.utils :as logic.utils]
            [schema.core :as s]))

(s/defn get-google-sheets-data [api-key :- s/Str
                                spreadsheet-id :- s/Str
                                range :- s/Str] :- [[s/Str]]
  (let [request-url (str "https://sheets.googleapis.com/v4/spreadsheets/"
                         spreadsheet-id
                         "/values/"
                         range
                         "?key=" api-key)]
    (let [response (http/get request-url)]
      (if (>= (:status response) 200)
        (-> response
            :body
            (json/parse-string)
            logic.utils/keywordize-keys
            :values)
        (throw (Exception. (str "Erro ao obter dados do Googles Sheets: " (:status response))))))))

(s/defn -main []
  (let [api-key "AIzaSyDFa2_cW287rgFC5LOZ22gumIk1LLBy_I0"
        spreadsheet-id "1b5Ac_J1aqLS46lGBZCtQrajYzirqbO4feWZqg8XKvUU"
        range "VISA ROSA (6)!A1:E63" ; TODO: Organizar os ranges em um MAP :janeiro !A1:E63 :fevereiro F1:J63...
        data (get-google-sheets-data api-key spreadsheet-id range)]
    (println "Dados obtidos do Google Sheets:")
    (doseq [row data]
      (println row))
    (print (first (first data)))))