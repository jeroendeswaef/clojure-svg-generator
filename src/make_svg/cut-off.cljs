(ns make-svg.core
  (:require [clojure.string :refer [join]])
  (:require [clojure.data.xml :as xml])
  (:require [clojure.math :as math]))

(def padding 7)
(def width 35)

(def document-width (+ width (* 2 padding)))
(def document-height (* 2 padding))

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
       :id "cut-line"
       :stroke-width "0.4"
       :stroke "black"
       :fill "transparent"
       }]]))

(def cut-off-path (str "M" padding "," padding " h" width))

(defn write-rss! [xml]
  (with-open [out-file (java.io.FileWriter. "cut-off.svg")]
    (xml/emit xml out-file)))

(-> (generate-svg-xml cut-off-path)
    write-rss!)
