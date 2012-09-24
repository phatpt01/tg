(declare-const x Int)
(assert (not (= (mod x 2) 0)))
(check-sat)
(model)