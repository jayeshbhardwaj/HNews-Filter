(ns clojure-brave-and-true.core
  (:gen-class)
  (:require [clj-http.client :as client]
            [clojure.data.json :as json]
            [clojure.string :as string]
            [clojure.core.reducers :as r]))

;; Pending features
;; Topic based
;; Send notification
;; modularize namepsace


(defn fetchTopUrls [n]
  (let [endpoint  "https://hacker-news.firebaseio.com/v0/"]
    (let [resp (client/get (str endpoint "topstories.json")
                           {:accept :json})]
      (take n (map #(str endpoint "item/" % ".json")
                    (into [] (re-seq #"[0-9]+" (:body resp))))))))

(def fp-words ["haskell" "clojure" "scala" "lisp" "scheme" "elixir" "erlang" "F#"])

(defn isFunNews?
  [title]
  (let [words fp-words]
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
  [criteria url]
  (let [attrs (getAttrs url)]
    (if (criteria (get attrs "title"))
      {:url (get attrs "url") :title (get attrs "title")}
      {})))


(defn waitForFutures
  "Returns vector of realized futures"
  [fColl]
  (while (= false (reduce (fn [r f] (and r (realized? f))) [] fColl))
    (do (Thread/sleep 100)))
  (reduce (fn [r f] (conj r @f)) [] fColl))


(defn -main
  "Filter Hacker News articles for FP"
  [& args]
  (let [count (Integer/parseInt (first args))
        res (map  (fn [url] (future (match-criteria isFunNews? url))) (fetchTopUrls count))]
    (filter #(not (empty? %)) (waitForFutures res))))
