(ns make-svg.core
  (:require [clojure.string :refer [join]])
  (:require [clojure.data.xml :as xml])
  (:require [clojure.math :as math]))

(defn generate-svg-xml [svg-path]
  (xml/sexp-as-element
   [:svg
    {:version "1.1"
     :width "500mm"
     :height "500mm"
     :viewBox "0 0 500 500"
     :xmlns "http://www.w3.org/2000/svg"}
     [:path
      {:d svg-path
       :id "rounded-rectangle"
       :stroke-width "0.4"
       :stroke "black"
       :fill "transparent"
       }]]))

(def border-radius 5)
(def padding 7)
(def width 40)
(def height 30)

(def rounded-rectangle-path (str "M" padding "," (+ padding border-radius) 
" a" border-radius "," border-radius " 0 0 1 " border-radius ",-" border-radius 
" h" (- width (* 2 border-radius)) 
" a" border-radius "," border-radius " 0 0 1 " border-radius "," border-radius 
" v" (- height (* 2 border-radius))
" a" border-radius "," border-radius " 0 0 1 -" border-radius "," border-radius
" h-" (- width (* 2 border-radius) )
" a" border-radius "," border-radius " 0 0 1 -" border-radius ",-" border-radius
" z"
))

(defn write-rss! [xml]
  (with-open [out-file (java.io.FileWriter. "rounded-rectangle.svg")]
    (xml/emit xml out-file)))

(-> (generate-svg-xml rounded-rectangle-path)
    write-rss!)
