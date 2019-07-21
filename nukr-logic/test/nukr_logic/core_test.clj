(ns nukr-logic.core-test
  (:require [clojure.test :refer :all]
            [nukr-logic.core :refer :all]))

(deftest a-test
  (testing "FIXME, I fail."
    (is (= 1 1))))

(deftest add-profile-test
  (testing "add-profile"
    (let [new-profile-key "newkey"
          new-profile-data {:name "Foo" :visible true}
          result (add-profile {}
                              new-profile-key
                              new-profile-data)]
      (is (= new-profile-key
                 (-> result keys last))
              "profile key not match")
      (is (= (assoc new-profile-data :connections [])
                 (-> result vals last))
              "profile data not match"))))

(deftest add-profile-duplicated-key-error-test
  (testing "add-profile-duplicated-key-error-test"
    (is (=
         [nil 'duplicated-profile-key-error]
         (add-profile
          {"the-duplicated-key" 
           {:name "Foo" :visible true}}
          "the-duplicated-key" 
          {:name "Bar" :visible false}))
        "didnt get failed on duplicated profile key")))

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

(deftest add-to-profile-connection-id-vector-test-x
  (testing "testing add-to-profile-connection-id-vector"
    (println (add-to-profile-connection-id-vector 
              (get profile-map-example "abc") "xpt"))))

(deftest add-to-profile-connection-id-vector-test
  (testing "testing add-to-profile-connection-id-vector"
    (println (add-to-profile-connection-id-vector
              (get profile-map-example "abc") "xpt"))))
