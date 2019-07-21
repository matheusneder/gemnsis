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
      (is (= 
           ;; expected
           new-profile-key
           ;; actual
           (-> result keys last))
          ;; assertion failure message
          "profile key not match")
      (is (= 
           ;; expected
           (assoc new-profile-data :connections [])
           ;; actual
           (-> result vals last))
          ;; assertion failure message
          "profile data not match"))))

(deftest add-profile-duplicated-key-error-test
  (testing "add-profile-duplicated-key-error-test"
    (is (=
         ;; expected
         {:errors '((-> profile-validation-errors :duplicated-key))}
         ;; actual
         (add-profile
          ;; current profile-map
          {"the-duplicated-key"
           {:name "Foo" :visible true}}
          ;; new item key
          "the-duplicated-key"
          ;; new item data
          {:name "Bar" :visible false}))
        ;; assertion failure message
        "didnt get failed on duplicated profile key")))

(deftest validate-profile-name-test
  (testing "validate-profile-name"
    (is (=
         ;; expected
         {:name "A Valid Name"}
         ;; actual
         (validate-profile-name {:name "A Valid Name"}))
        ;; assertion failure message
        "validation for 'A Valid Name' failed.")))

(deftest validate-profile-name-error-blank
  (testing "validate-profile-name-error-blank"
    (is (some 
         ;; expected (as an item of the errors list)
         #(= (-> profile-validation-errors :blank-name) %)
         ;; actual
         (-> (validate-profile-name {:name "  "}) :errors))
        ;; assertion failure message
        "Provided a blank name but blank-name error reason could
not be found.")
    ))

(deftest validate-profile-name-error-invalid
  (testing "validate-profile-name-error-invalid"
    (is (some
         ;; expected (as an item of the errors list)
         #(= (-> profile-validation-errors :invalid-name) %)
         ;; actual
         (-> (validate-profile-name {:name "Ab"}) :errors))
        ;; assertion failure message
        "Provided a invalid name but invalid-name error reason could
not be found.")))

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
