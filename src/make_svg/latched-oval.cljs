(ns make-svg.core
  (:require [clojure.string :refer [join]])
  (:require [clojure.data.xml :as xml])
  (:require [clojure.math :as math]))

(def padding 7)
(def width 220)
(def height 160)
(def latch-width 10)
(def is-enable-latch true)

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
       :id "latched-oval"
       :stroke-width "0.4"
       :stroke "black"
       :fill "transparent"
       }]]))

(def rx (- (/ width 2) (/ latch-width 2)))
(def ry (/ height 2))
(def latched-oval-path (str 
    "M " padding " " (+ padding ry) " "
    "a " rx " " ry " 0 0 1 " rx " " (* ry -1) " "
    (cond is-enable-latch (str "m " latch-width " 0 ") :default (str "h " latch-width)) " "
    "a " rx " " ry " 0 0 1 " rx " " ry " "
    "a " rx " " ry " 0 0 1 " (* rx -1) " " ry " "
    (cond is-enable-latch (str "m " (* latch-width -1) " 0 ") :default (str "h " (* latch-width -1))) " "
    "a " rx " " ry " 0 0 1 " (* rx -1) " " (* ry -1)
))

(defn write-rss! [xml]
  (with-open [out-file (java.io.FileWriter. "latched-oval.svg")]
    (xml/emit xml out-file)))

(-> (generate-svg-xml latched-oval-path)
    write-rss!)
