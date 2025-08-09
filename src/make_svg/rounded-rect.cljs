(ns make-svg.core
  (:require [clojure.string :refer [join]])
  (:require [clojure.data.xml :as xml])
  (:require [clojure.math :as math]))

(def border-radius 5)
(def padding 7)
(def width 40)
(def height 30)

(def document-width (+ width (* 2 padding)))
(def document-height (+ height (* 2 padding)))

(defn generate-svg-xml [svg-path]
  (xml/sexp-as-element
   [:svg
    {:version "1.1"
     :width (str document-width "mm")
     :height (str document-height "mm")
     :viewBox (str "0 0 " document-width " " document-height)
     :xmlns "http://www.w3.org/2000/svg"}
     [:path
      {:d svg-path
       :id "rounded-rectangle"
       :stroke-width "0.4"
       :stroke "black"
       :fill "transparent"
       }]]))

(defn rounded-curve [xfactor yfactor] (str " a" border-radius "," border-radius " 0 0 1 " (* xfactor border-radius) "," (* yfactor border-radius)))

(def rounded-rectangle-path (str "M" padding "," (+ padding border-radius) 
  (rounded-curve 1 -1)
" h" (- width (* 2 border-radius)) 
  (rounded-curve 1 1)
" v" (- height (* 2 border-radius))
  (rounded-curve -1 1)
" h-" (- width (* 2 border-radius) )
  (rounded-curve -1 -1)
" z"
))

(defn write-rss! [xml]
  (with-open [out-file (java.io.FileWriter. "rounded-rectangle.svg")]
    (xml/emit xml out-file)))

(-> (generate-svg-xml rounded-rectangle-path)
    write-rss!)
