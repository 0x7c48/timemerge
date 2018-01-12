(ns tm.files
  (:require [clojure.string    :as str]
            [clojure.java.io   :as io]
            [clojure.java.jdbc :as jdbc]))

(def db-path
  (str (io/file (str (System/getProperty "java.io.tmpdir") "/" "apptopia.sqlite3"))))


(def db
  {:dbname      "apptopia"
   :classname   "org.sqlite.JDBC"
   :subprotocol "sqlite"
   :subname     db-path})


(defn make-conn []
  (let [db-conn   (jdbc/get-connection db)
        connected (jdbc/add-connection db db-conn)]
    connected))


(defn split' [line]
  (str/split line #":" 2))


(defn parse-int [s]
  (cond
    (number? s) s
    (string? s)
    (try (Integer/parseInt s)
         (catch Exception e nil))
    :else       nil))


(defn mk-vector [segment]
  (vector
    (->> segment first)
    (->> segment second parse-int)))


(defn parse-datetime-value
  "Parse datetime from line like YYYY-MM-DD:X to [YYYY-MM-DD X]"
  [line]
  (some-> line
          split'
          mk-vector))


(defn time-series-q
  [batch]
  (map (fn [row]
         (if (and (first row) (second row))
           [(first row) (second row)]))
    batch))


(defn upsert-time-series!
  [conn batch]
  (try
    (some->> batch
             time-series-q
             (filter some?)
             (jdbc/insert-multi! conn
               :timeseries
               [:datetime :value]))
    (catch Exception e (str (.getMessage e)))))


(def time-series-agg-q
  ["SELECT datetime, sum(value) value
    FROM timeseries
    GROUP BY date(datetime)
    ORDER BY date(datetime)"])


(defn row-format [row]
  (str (-> row :datetime) ":" (-> row :value)))


(defn setup-db []
  (if-not (.exists (io/as-file db-path))
    (jdbc/db-do-commands db
      (jdbc/create-table-ddl :timeseries
        [[:datetime :text   "NOT NULL"]
         [:value    :bigint "NOT NULL"]]))))


(defn clean-db []
  (if (.exists (io/as-file db-path))
    (io/delete-file db-path)))


(defn setup []
  (println "tm make setup ..")
  (setup-db))


(defn clean []
  (println "tm make clean")
  (clean-db))


(defn make-dest-file [dst-file]
  (println "tm make file" dst-file " ..")
  (let [conn (make-conn)]
    (with-open [writer (io/writer (-> dst-file str))]
      (jdbc/query conn time-series-agg-q
        {:row-fn (fn [row]
                   (.write writer (row-format row))
                   (.newLine writer))})))
  (println "tm make file done"))


(defn proc-line [input conn batch-size]
  (when (seq input)
    (->> (take batch-size input)
         (map parse-datetime-value)
         (upsert-time-series! conn))
    #(proc-line (drop batch-size input) conn batch-size)))


(defn read-input-file [file batch-size dst-file]
  (println "tm read" file " ..")
  (let [conn (make-conn)]
    (with-open [reader (io/reader file)]
      (trampoline proc-line (line-seq reader) conn batch-size))))


(defn merge-files [files batch-size dst-file]
  (doseq [file files]
    (read-input-file file batch-size dst-file))
  (make-dest-file dst-file))
