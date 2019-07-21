(ns nukr-logic.core-test
  (:require [clojure.test :refer :all]
            [nukr-logic.core :refer :all]))

(deftest a-test
  (testing "FIXME, I fail."
    (is (= 1 1))))

(deftest add-profile-test-x
  (testing "Nukr profile"
    (println (add-profile {"abc" {:name "Matheus"
                                :visible true
                                :connections []}}
                          {:name "Leticia"
                           :visible true}))))


(deftest add-profile-test
  (testing "Nukr profile"
    (assert (= "Fulano" 
               (get 
                (last 
                 (vals 
                  (add-profile 
                   {} 
                   {:name "Fulano"
                    :visible true}))) :name)) 
            "Add profile failed")))

(def profile-map-example
  {"abc" {:name "Matheus"
        :visible true
        :connections []}
   "xpt" {:name "Leticia"
        :visible true
        :connections []}})

(deftest connect-test 
  (testing "Nukr connect"
    (println (connect profile-map-example "abc" "xpt")
             )
    ))

(deftest add-to-profile-connection-id-vector-test
  (testing "testing add-to-profile-connection-id-vector"
    (println (add-to-profile-connection-id-vector 
              (get profile-map-example "abc") "xpt"))))

