## Introduction

Small clojure program that creates a svg for a living hinge. The goal is to be able to import into OpenScad to evenually 3d-print in a flexible material (TPU).

To invoke:

    clj -M src/make_svg/hinge.cljs

This should generate an svg:

![](out.svg)