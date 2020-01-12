(ns clojure-brave-and-true.keywords
  (:gen-class))

(def fp-words ["haskell" "clojure" "scala" "lisp" "scheme" "elixir" "erlang" "F#"])

(def compiler-words ["compiler" "llvm" "parser" "lexer"])

(def words-map {:compiler compiler-words :functional fp-words})
