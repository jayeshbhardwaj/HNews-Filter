(ns clojure-brave-and-true.queens
  (require [clojure.string :as str]))

(defn printQueen
  [p n]
  (let [pos (reduce (fn [s p] (str s p)) "" (take n (repeat ".")))]
    (str (subs pos 0 (dec p)) "Q" (subs pos p))))

(defn backtrack-nqueens
  [n]
  (backtrack-nqueens 0 [])
  [n row prevRows]
  (if (= n row) prevRows
   ()))

(defn isSafe?
  [p prevPos]
  (reduce (fn [s prev] (and s (not (= (:y p) (:y prev)))
                           (not (isDiag? p prev))))
          true
          prevPos))

(defn isDiag?
  [p q]
  (= (- (:x p) (:x q)) (- (:y p) (:y q))))
