(ns clojure-brave-and-true.queens
  (require [clojure.string :as str]))

(defn printQueen
  [p n]
  (let [pos (reduce (fn [s p] (str s p)) "" (take n (repeat ".")))]
    (str (subs pos 0 (dec p)) "Q" (subs pos p))))

(defn printSoln
  [board]
  (doseq [row board] (println (printQueen (inc (:y row)) (count board))) board))

(defn isDiag?
  [p q]
  (= (Math/abs (- (:x p) (:x q))) (Math/abs (- (:y p) (:y q)))))


(defn isSafe?
  [p prevPos]
  (do
    (reduce (fn [s prev] (and s (not (= (:y p) (:y prev)))
                             (not (isDiag? p prev))))
            true
            prevPos)))


(defn backtrack-nqueens
  [board row n]
  (if (= row n) [true board]
      (let [ppos board]
        (loop [col 0]
          (if (= col n) [false board]
              (let [next (lazy-seq (backtrack-nqueens (conj ppos {:x row :y col}) (inc row) n))]
                (if (and (isSafe? {:x row :y col} ppos)
                         (= (first next) true))
                  [true (second next)]
                  (recur (inc col)))))))))


(defn -main
  [& args]
  (let [n (if (= (count args) 0) 8 (Integer/parseInt (first args)))]
    (printSoln (second (backtrack-nqueens [] 0 n)))))
