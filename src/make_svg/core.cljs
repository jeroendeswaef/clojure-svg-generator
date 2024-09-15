(ns make-svg.core
  (:require [clojure.string :refer [join]])
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

(def step-cnt 7)
(def inner-radius 20)
(def outer-radius 50)
(defn coords-at-angle [r angle] 
    {:x (format "%.2f" (* r (math/cos (math/to-radians angle)))) 
     :y (format "%.2f" (* r (math/sin (math/to-radians angle)))) }
)
(defn coords-str-at-angle [r angle] (let [coords (coords-at-angle r angle)] (str (get coords :x) " " (get coords :y))))
;;(def steps (map (fn [step-i] [{ :angle (/ (* step-i 90) (- step-cnt 1)), :kind :cross}] ) (range 0 step-cnt)))
(def steps (reduce (fn [state input] (
  let [angle (/ (* input 90) (- step-cnt 1))] 
  (((remove nil?) conj) (conj state { :kind :cross, :angle angle }) ( if (< (/ (count state) 2) (- step-cnt 1)) { :kind :bind })) 
)) [] (range 0 step-cnt)))
(println "steps:" steps)
(defn combined-path [steps]
  (apply str (map (fn[step] (case (get step :kind)
    :cross (join " " [(coords-str-at-angle inner-radius (get step :angle)) "L" (coords-str-at-angle outer-radius (get step :angle))])
    :bind "-"
  )) steps))
)
;;(def combined-path (str "M 0 0 " (apply str (map (fn [step] (str "L " (coords-str-at-angle inner-radius (get step :angle)) " L " (coords-str-at-angle outer-radius (get step :angle)) " ")) steps)) " z"))

(println (combined-path steps))
;;(def hinge-path combined-path)

;;(defn write-rss! [xml]
;;   (with-open [out-file (java.io.FileWriter. "out.svg")]
;;     (xml/emit xml out-file)))

;; (-> (generate-svg-xml hinge-path)
;;     write-rss!)
