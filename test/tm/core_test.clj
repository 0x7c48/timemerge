(ns tm.core-test
  (:require [clojure.test :refer :all]
            [clojure.java.io     :as io]
            [clojure.string      :as str]
            [clj-time.core       :as time]
            [clj-time.periodic   :as time-period]
            [clj-time.coerce     :as tc]
            [clj-time.format     :as tf]
            [tm.core :refer :all]))

(def data1-txt
  (str (io/file (str (System/getProperty "java.io.tmpdir") "/" "data1.txt"))))


(defn time-lazy-range
  [start end step]
  (let [trange (time-period/periodic-seq start step)
        end?   (fn [t] (time/within? (time/interval start end) t))]
    (take-while end? trange)))


(def times
  (time-lazy-range (time/date-time 1414 01)
    (time/date-time 9999 01)
    (time/days 1)))


(defn make-out-file [dst-file]
  (with-open [w (io/writer (-> dst-file str))]
    (doseq [date times]
      (.write w (str (tf/unparse (tf/formatters :date) date) ":" (rand-int 100)))
      (.newLine w))
    (doseq [date times]
      (.write w (str (tf/unparse (tf/formatters :date) date) ":" (rand-int 100)))
      (.newLine w))
    (doseq [date times]
      (.write w (str (tf/unparse (tf/formatters :date) date) ":" (rand-int 100)))
      (.newLine w))
    (doseq [date times]
      (.write w (str (tf/unparse (tf/formatters :date) date) ":" (rand-int 100)))
      (.newLine w))
    (doseq [date times]
      (.write w (str (tf/unparse (tf/formatters :date) date) ":" (rand-int 100)))
      (.newLine w))
    (doseq [date times]
      (.write w (str (tf/unparse (tf/formatters :date) date) ":" (rand-int 100)))
      (.newLine w))
    (doseq [date times]
      (.write w (str (tf/unparse (tf/formatters :date) date) ":" (rand-int 100)))
      (.newLine w))))


(deftest data1-txt-test
  (testing "Make file"
    (println "start making file" data1-txt)
    (-> data1-txt
        make-out-file)
    (println "done")
    (is (= 0 0))))
