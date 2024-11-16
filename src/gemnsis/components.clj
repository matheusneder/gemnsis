(ns gemnsis.components
  (:require 
   [clojure.math.numeric-tower :as math]))

;; Helpers for paginate function

(defn limit
  "If val greater than max, return max; 
   Returns val otherwise."
  [val max]
  (if (> val max)
    max
    val))

(defn try-parse-int
  [val default-val]
  "Try to parse val as integer then returns it;
   If val is nil or not a valid integer then returns default-val."
  (let [provided-val
        (if (nil? val)
          ;; keep nil if nil provided
          nil
          ;; non nil val was provided
          ;; so reading it
          (read-string val))]
    (if (integer? provided-val)
      provided-val
      default-val)))

(def paginate-items-per-page-max 50)
(def paginate-items-per-page-default 10)

;; the paginate function
(defn paginate
  "Paginate a collection result. 
   Input  model is: 
    - paginate-params {:page int (optional, default 1)
                       :perpage int (optional, default 
                        paginate-items-per-page-default, 
                        limited to paginate-items-per-page-max)};
    - coll The collection;
   Output model is: {:total int (total items of collection)
                     :showing int (number of items in current page)
                     :page int (current page number, starting at 1)
                     :pages int (number of pages for the given perpage value)
                     :dropping int (number of dropping/skiping items)
                     :items '(collection) (collection's slice 
                                           for the given page value)}"
  [paginate-params coll]
  (let [items-per-page (-> paginate-params
                           :perpage
                           (try-parse-int paginate-items-per-page-default)
                           (limit paginate-items-per-page-max))
        current-page (try-parse-int (:page paginate-params) 1)
        total-items (count coll)
        total-pages (math/ceil (/ total-items items-per-page))
        dropping (* (dec current-page) items-per-page)
        items (->> coll
                   (drop dropping)
                   (take items-per-page))]
    {:total total-items
     :showing (count items)
     :page current-page
     :pages total-pages
     :dropping dropping
     :items items}))