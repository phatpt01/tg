(declare-const n Int)
(assert (not (> n 0)))
(check-sat)
(model)
