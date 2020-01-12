(ns clojure-brave-and-true.core
  (:gen-class)
  (:require [clj-http.client :as client]
            [clojure.data.json :as json]
            [clojure.string :as string]
            [clojure.core.reducers :as r]
            [clojure-brave-and-true.keywords :as word]))

;; Pending features
;; Topic based
;; Send notification
;; modularize namespace


(defn fetchTopUrls [n]
  (let [endpoint  "https://hacker-news.firebaseio.com/v0/"]
    (let [resp (client/get (str endpoint "topstories.json")
                           {:accept :json})]
      (take n (map #(str endpoint "item/" % ".json")
                    (into [] (re-seq #"[0-9]+" (:body resp))))))))

;;(def fp-words ["haskell" "clojure" "scala" "lisp" "scheme" "elixir" "erlang" "F#"])

(defn isNewsMatched?
  [title match-words]
  (let [words match-words]
    (reduce (fn [res w]
              (or res (or (.contains (string/lower-case title) w))))
            false
            words)))

(defn getAttrs
  "Parse url response as json"
  [url]
  (let [resp (client/get url)]
    (json/read-str (:body resp))))

(defn match-criteria
  "Matches criteria with news"
  [theme url]
  (let [attrs (getAttrs url)]
    (if (isNewsMatched? (get attrs "title")
                        ((keyword theme) word/words-map))
      {:url (get attrs "url") :title (get attrs "title")}
      {})))


(defn waitForFutures
  "Returns vector of realized futures similar to wait for threads"
  [fColl]
  (while (= false (reduce (fn [r f] (and r (realized? f))) [] fColl))
    (do (Thread/sleep 100)))
  (reduce (fn [r f] (conj r @f)) [] fColl))


(defn -main
  "Filter Hacker News articles for FP"
  [& args]
  (time (let [count (Integer/parseInt (first args))
        theme (if (empty? (rest args)) "functional"
                 (first (rest args)))
        res (map  (fn [url] (future (match-criteria theme url))) (fetchTopUrls count))]
    (filter #(not (empty? %)) (waitForFutures res)))))
