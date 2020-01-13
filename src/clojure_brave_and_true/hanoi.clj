(ns clojure-brave-and-true.hanoi
  (:gen-class))

(defn move
  [from to via n]
  (when (> n 0)
      (move from via to (dec n))
      (println (str "Move piece " n " from " from " to " to))
      (move via to from (dec n))))
