(ns practice.core
  (:use midje.sweet))

(defn note-expected-failure [] (println "^^^^ The previous failure was expected ^^^^"))

;;; simple
(fact (* 2 2) => 4)

; simple failing
(fact (* 2 2) => 5) (note-expected-failure)

; multiple facts
(facts "about doubling"
  (* 2 2) => 4
  (* 2 3) => 6)

;;; at this point, run from lein midje as well

;;; function on the rhs as well

(fact (* 2 3) => even?)


;;; Intoducing Prerequisites
;;; so usually we need to state facts based prerequisites.  These
;;; are sort of like mocking functions
;;; write the fact first, get exception, write function, add prerequisite
;;; get the unable to resolve symbol exception, then add unfinished

(unfinished triple)

(defn triple-all [number-seq]
  (map triple number-seq))

(fact
  (triple-all [1 2 3]) => [3 6 9]
  (provided (triple 1) => 3
            (triple 2) => 6
            (triple 3) => 9))


;;; you can use => on strings to match regular expressions
(fact "This is a string" => #"is a \w+$")

;;; you can do negative assertions as well

(fact (rest [1 2 3]) =not=> [1 2])

;;; checkers--matching functions on the rhs

(fact [1 2 3] => (contains [2 3]))

;;; in functional languages, sometimes the result is a function, how to you check to see if the
;;; right function was returned?

(fact (first [even? odd?]) => (exactly even?))

;;; I really like roughly. This would have saved me a lot of grief in the past
;;; trying to enter exact numbers or implement tolerances
(fact (/ 4 3) => (roughly 1.3333))

;;; checkers on collections
;;; this succeeds
(fact [1 2 3 4] => (contains [2 3]))

;;; but this one fails
(fact [1 2 3 4] => (contains [3 2]))  (note-expected-failure)

;;; however, we can add optional flags to the collection checkers
(fact [1 2 3 4] => (contains [3 2] :in-any-order))

;;; what about this?
(fact [1 2 3 4] => (contains [1 3]))  (note-expected-failure)

;;; we can fix it with 
(fact [1 2 3 4] => (contains [1 3] :gaps-ok))

;;; same goes for maps
(fact {:a 1 :b 2 :c 3} => (contains {:b 2}))

;;; can be more restrictive
(fact {:a 1 :b 2 :c 3} => (just {:b 2}))   (note-expected-failure)

;;; and can be specific about contents but relaxed about order
(fact {:a 1 :b 2 :c 3} => (just {:b 2 :a 1 :c 3} :in-any-order))


;;; can arrange facts in tables
(tabular
 (fact (even? ?int) => ?expected)
 :where
 | ?int  | ?expected
 | 1     | falsey 
 | 2     | truthy
 | 33    | falsey 
 | 154   | truthy)


;;; Metaconstants
;;; For me, enhance the declarative nature of Midje
;;; a way to state constraints on the arguments to facts or prerequisites (i.e. the mocking function)
;;;

(unfinished split-at-commas trim-last-digit)

(defn split-and-trim-last [input-string]
  (map trim-last-digit (split-at-commas input-string)))

(let [expected-result ["a-word" "another-word"]]
  (fact
    (split-and-trim-last ...input-string...) => expected-result
    (provided
      (split-at-commas ...input-string...) => [...result1... ...result2...]
      (trim-last-digit ...result1...) => "a-word"
      (trim-last-digit ...result2...) => "another-word")))
              
;;; Setup, teardown, etc.

(let [mycount (agent 0)]
  (send mycount #(+ % 1))
  (await mycount)
  (fact @mycount => 1))

;;;  The above is the same as 

(against-background [(around :facts (let [mycount (agent 0)] ?form))
                     (before :checks (do (send mycount #(+ 1 %)) (await mycount)))]
  (fact @mycount => 1))

  


