(ns make-svg.core
  (:require [clojure.data.xml :as xml])
  (:require [clojure.math :as math]))

(defn generate-svg-xml [hinge-path]
  (xml/sexp-as-element
   [:svg
    {:version "1.1"
     :width "100"
     :height "100"
     :viewBox "0 0 100 100"
     :xmlns "http://www.w3.org/2000/svg"}
    [:g
     {}
     [:path
      {:d hinge-path
       :stroke-width "0.4"
       :transform-origin "center"
       :transform "scale(1, -1)"
       :stroke "black"
       :fill "transparent"}]]]))

(def angles [0 15 30 45 60 75 90])
(def inner-radius 20)
(def outer-radius 50)
(defn coords-at-angle [r angle] 
    {:x (format "%.2f" (* r (math/cos (math/to-radians angle)))) 
     :y (format "%.2f" (* r (math/sin (math/to-radians angle)))) }
)
(defn coords-str-at-angle [r angle] (let [coords (coords-at-angle r angle)] (str (get coords :x) " " (get coords :y))))
(println (apply str (map (fn [angle] (str "M " (coords-str-at-angle inner-radius angle) " L " (coords-str-at-angle outer-radius angle))) angles)))
(def hinge-path (apply str (map (fn [angle] (str "M " (coords-str-at-angle inner-radius angle) " L " (coords-str-at-angle outer-radius angle) " z ")) angles)))

(defn write-rss! [xml]
  (with-open [out-file (java.io.FileWriter. "out.svg")]
    (xml/emit xml out-file)))

(-> (generate-svg-xml hinge-path)
    write-rss!)
