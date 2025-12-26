(ns make-svg.core
  (:require [clojure.string :refer [join]])
  (:require [clojure.data.xml :as xml])
  (:require [clojure.math :as math]))

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

(defn coords-at-angle [r angle] 
    {:x (format "%.2f" (+ (/ thickness 2) (* r (math/cos (math/to-radians angle))))) 
     :y (format "%.2f" (+ (/ thickness 2) (* r (math/sin (math/to-radians angle))))) }
)
(defn coords-str-at-angle [r angle] (let [coords (coords-at-angle r angle)] (str (get coords :x) " " (get coords :y))))
(def steps (drop-last (interleave 
  (reduce (fn [state input] (
    let [angle (/ (* input 90) (- step-cnt 1))] 
    (conj state { :kind :cross, :angle angle, :direction (if (= 0 (mod (count state) 2)) :out :in)  })
  )) [] (range 0 step-cnt))
  (repeat { :kind :bind })
)))
(println "steps:" steps)
(defn combined-path [steps]
  (str "M "
  (apply str (map (fn[step] (case (get step :kind)
    :cross (join " L " (let [f (if (= (get step :direction) :in) reverse identity)] (f [(coords-str-at-angle inner-radius (get step :angle)) (coords-str-at-angle outer-radius (get step :angle))])))
    :bind " L "
  )) steps))
  "  ")
)
(def hinge-path (combined-path steps))

(println hinge-path)
(defn write-rss! [xml]
  (with-open [out-file (java.io.FileWriter. "hinge.svg")]
    (xml/emit xml out-file)))

(-> (generate-svg-xml hinge-path)
    write-rss!)
