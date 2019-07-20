(ns nukr-logic.core-test
  (:require [clojure.test :refer :all]
            [nukr-logic.core :refer :all]))

(deftest a-test
  (testing "FIXME, I fail."
    (is (= 0 1))))

(deftest add-profile-test
  
  (testing "Nukr profile"
    
    
    (println (add-profile [{
                            :name "Matheus" 
                            :visible true 
                            :connections [] }] 
                          {
                           :name "Leticia" 
                           :visible true}))
    ))

(def profile-vector-base [{:name "Matheus"
                    :visible true
                    :connections []}
                   {:name "Leticia"
                    :visible true
                    :connections []}])

(deftest connect-test 
  (testing "Nukr connect"
    (println (connect profile-vector-base 0 1)
             )
    ))

(deftest add-to-profile-connection-id-vector-test
  (testing "testing add-to-profile-connection-id-vector"
    (println (add-to-profile-connection-id-vector 
              (first profile-vector-base) 1))))

(deftest nukr-learning-test
  (testing "Learning how to test"
    (println "teste")))
