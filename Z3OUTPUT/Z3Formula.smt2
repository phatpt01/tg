(declare-const M Int)
(declare-const N Int)
(assert (and (> M 0) (> N 0)))
(assert (not (> M N)))
(check-sat)
(model)
