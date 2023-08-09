(defn s [x]
  (fn [y]
    (fn [z]
      ((x z) (y z)))))

(defn k [x]
  (fn [y] x))

(defn u [n]
  (+ n 1))

(def z 0)

(defn fib [n]
  ((s
    (s (s k) (k k))
    (s (s k) (s (s k (s k)) k))
    (s (k (s k (s k (s k (s k (s (s k) (k k))))))) (s (k (s (s k) k))) (s (k (s k))))) (k (s (k (s k (s k (s k (s k (s (s k) (k k))))))) (k (s k))))) n)

(def ten (s (s (s k) k) (s (s (s k) k) (s (s (s k) k) (s (s (s k) k) (s (s (s k) k) (s (s (s k) k) (s (s (s k) k) (s k)))))))))

(def x (fib ten u z))

(println x) ;; retorna 55
