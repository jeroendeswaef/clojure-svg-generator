(ns make-svg.core
  (:require [clojure.string :refer [join]])
  (:require [clojure.data.xml :as xml])
  (:require [clojure.math :as math]))

(def padding 7)

(defn generate-svg-xml [svg-path rectangle-width rectangle-height]
  (let [
    document-width (+ rectangle-width (* 2 padding))
    document-height (+ rectangle-height (* 2 padding))
    ]
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
       }]])))

(defn rounded-curve [xfactor yfactor rectangle-border-radius] (str " a" rectangle-border-radius "," rectangle-border-radius " 0 0 1 " (* xfactor rectangle-border-radius) "," (* yfactor rectangle-border-radius)))

(defn rounded-rectangle-path [rectangle-width rectangle-height rectangle-border-radius] (str "M" padding "," (+ padding rectangle-border-radius)
  (rounded-curve 1 -1 rectangle-border-radius)
" h" (- rectangle-width (* 2 rectangle-border-radius))
  (rounded-curve 1 1 rectangle-border-radius)
" v" (- rectangle-height (* 2 rectangle-border-radius))
  (rounded-curve -1 1 rectangle-border-radius)
" h-" (- rectangle-width (* 2 rectangle-border-radius) )
  (rounded-curve -1 -1 rectangle-border-radius)
" z"
))

(defn write-rss! [xml]
  (with-open [out-file (java.io.FileWriter. "rounded-rectangle.svg")]
    (xml/emit xml out-file)))

(->
  (let [
      rectangle-width (Integer/parseInt (nth *command-line-args* 0))
      rectangle-height (Integer/parseInt (nth *command-line-args* 1))
      rectangle-border-radius (Integer/parseInt (nth *command-line-args* 2))
    ]
  (generate-svg-xml (rounded-rectangle-path rectangle-width rectangle-height rectangle-border-radius) rectangle-width rectangle-height))
     write-rss!)
