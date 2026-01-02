(ns make-svg.core
  (:require [clojure.string :refer [join]])
  (:require [clojure.data.xml :as xml])
  (:require [clojure.math :as math])
  (:require [clojure.data.json :as json]))

(def thickness 2)
(defn generate-svg-xml [hinge-path]
  (xml/sexp-as-element
   [:svg
    {:version "1.1"
     :width "60mm"
     :height "60mm"
     :viewBox "0 0 60 60"
     :xmlns "http://www.w3.org/2000/svg"}
    [:g
     {}
     [:path
      {:d hinge-path
       :stroke-width thickness
       :transform-origin "center"
       :transform "scale(1, -1)"
       :stroke "black"
       :fill "transparent"}]]]))

(def step-cnt 8)
(def inner-radius 20)
(def outer-radius 50)
(def bend-radius 5)

(defn coords-at-angle [r angle] 
    {:x (format "%.2f" (+ (/ thickness 2) (* r (math/cos (math/to-radians angle))))) 
     :y (format "%.2f" (+ (/ thickness 2) (* r (math/sin (math/to-radians angle))))) }
)
(defn coords-str-at-angle [r angle] (let [coords (coords-at-angle r angle)] (str (get coords :x) " " (get coords :y))))
(def angle-steps 
  (reduce (fn [state input] (
    let [angle (/ (* input 90) (- step-cnt 1))] 
    (conj state { :angle angle, :direction (if (= 0 (mod (count state) 2)) :out :in)  })
  )) [] (range 0 step-cnt))
)
(println "angle-steps:" angle-steps )
(defn line-coords [is-inner step]
  (let [
      fn-coords-swap (if (= (get step :direction) :in) reverse identity)
      radius-delta (if (= is-inner true) bend-radius 0)
      ]
      (let [
        [start end] (fn-coords-swap 
          [(coords-at-angle ( + inner-radius radius-delta) (get step :angle))
          (coords-at-angle (- outer-radius radius-delta) (get step :angle))])
      ]
        { :start start :end end }
      )
  )
)
;; (println "line-coords:", (line-coords true (get angle-steps 0)))
(def hinge-path (apply str (map-indexed (fn[ index steps ]
  (let [[current next] steps]
    (let [
      fn-current-coords-swap (if (= (get current :direction) :in) reverse identity)
      fn-next-coords-swap (if (= (get next :direction) :in) reverse identity)
      ]
      (let [
        current-outer-end (get (line-coords false current) :end)
        next-start (get (line-coords true next) :start)
        next-outer-start (get (line-coords false next) :start)
      ]
        (str 
          (if (= index 0) (str "M" (get (get (line-coords false current) :start) :x) "," (get (get (line-coords false current) :start) :y) " ") "")
          "L" (get (get (line-coords true current) :end) :x) "," (get (get (line-coords (if (= (get next :direction) :none) false true) current) :end) :y) " "
          (if (= (get next :direction) :none) "" (str "C " 
            (get current-outer-end :x) "," (get current-outer-end :y) " " ;; x1 y1
            (get next-outer-start :x) "," (get next-outer-start :y) " " ;; x2 y2
            (get next-start :x) "," (get next-start :y) " ")) ;; x y
        )
      )
  )))
  (partition 2 1 (conj angle-steps { :angle 0, :direction :none }))
)))

(println hinge-path)
(defn write-rss! [xml]
  (with-open [out-file (java.io.FileWriter. "hinge.svg")]
    (xml/emit xml out-file)))

(-> (generate-svg-xml hinge-path)
    write-rss!)
