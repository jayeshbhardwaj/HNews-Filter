(ns clojure-brave-and-true.core
  (:gen-class)
  (:require [clj-http.client :as client])
  (:require [clojure.data.json :as json])
  (:require [clojure.string :as string])
  (:require [clojure.core.reducers :as r]))

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


(defn isFunNews?
  [title]
  (let [words ["haskell" "clojure" "scala" "lisp" "scheme" "elixir" "erlang" "F#" "google"]]
    ;;(time (not= 0 (count (filter #(.contains (string/lower-case title) %) words))))
    (reduce (fn [res w]
              (or res (or (.contains (string/lower-case title) w))))
            false
            words)))

(defn getAttrs
  [url]
  (let [resp (client/get url)]
    (json/read-str (:body resp))))

(defn match-criteria
  [criteria url]
  (let [attrs (getAttrs url)]
    (if (criteria (get attrs "title"))
      {:url (get attrs "url") :title (get attrs "title")}
      {})))

(defn -main
  "Filter Hacker News articles for FP"
  [& args]
  (let [count (Integer/parseInt (first args))
        res (map  (fn [url] (future (match-criteria isFunNews? url))) (fetchTopUrls count))]

    ;; (loop [start count urls (fetchTopUrls count)]
    ;;   (conj res {(keyword start) (future (match-criteria isFunNews? (first urls)))})
    ;;   (if (> start 0)
    ;;     (recur (dec start) (rest urls))))

    (time (while (= false (reduce (fn [r f] (and r (realized? f))) [] res))
            (do (Thread/sleep 100))))
    (map #(println @%) (filter #(not (empty? @%)) res))
    ))
