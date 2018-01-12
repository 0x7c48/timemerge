(ns tm.core
  (:require [clojure.string :as str]
            [clojure.tools.cli :as cli]
            [tm.files :as fl])
  (:gen-class))

(defn usage [options-summary]
  (->> ["Program for Time Series Merge Problem"
        ""
        "Usage: java -jar tm filename1 filename2 filenamen3 --dst-file filename"
        "filename1 - input file"
        "filename2 - input file"
        ""
        "Options:"
        options-summary]
       (str/join \newline)))


(defn error-msg [errors]
  (str "The following errors occurred while parsing your command:\n\n"
    (str/join \newline errors)))


(def cli-options
  [["-h" "--help" "Show help"]
   ["-g" "--debug" "Show log"]
   ["-o" "--dst-file filename" "Destination file name, default out.txt" :default "out.txt"]
   ["-b" "--batch" "Batch read rows number, default 100000 rows" :default 100000]
   ["-c" "--no-clean" "Delete program artifacts, default true"]])


(defn validate-args [args]
  (let [{:keys [options arguments errors summary]} (cli/parse-opts args cli-options)]
    (cond
      (:help options)
      {:exit-message (usage summary) :ok? true}

      errors
      {:exit-message (error-msg errors)}

      (>= (count arguments) 1)
      {:arguments arguments :options options}

      :else
      {:exit-message (usage summary)})))


(defn exit [status msg]
  (println msg)
  (System/exit status))


(defn -main [& args]
  (let [{:keys [arguments options exit-message ok?]} (validate-args args)]
    (if exit-message
      (exit (if ok? 0 1) exit-message)
      (do (fl/setup)
          (fl/merge-files arguments (:batch options) (:dst-file options))
          (when (:debug options) (println "db " fl/db-path))
          (if-not (:no-clean options)
            (fl/clean))))))
