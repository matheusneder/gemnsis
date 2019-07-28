(ns nukr.controller-test
  (:require [clojure.test :refer :all]
            [nukr.controller :as controller]))



(deftest add-profile!-test
  (testing "controller/add-profile!-test"
    (let [matheus (controller/add-profile!
                   {:name "matheus"
                    :email "matheus@email.com"
                    :suggestible true})]
      (println matheus))))


