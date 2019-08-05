(ns nukr.controller-test
  (:require 
   [clojure.test :refer :all]
   [nukr.controller :as controller]
   [clojure.tools.logging :as log]
   [nukr.database :as database]))

(deftest add-profile!-test
  (testing "controller/add-profile!"
    (database/clear!)
    (let [result
          (controller/add-profile! {:name "Foo"
                                    :email "foo@bar.com"})]
      (log/info "add-profile!-test" result)
      (is (= "foo@bar.com" (:email result))
          "FIXME"))))

(deftest update-profile!-test
  (testing "controller/update-profile!"
    (database/clear!)
    (let [result
          (controller/update-profile!
           (:id (controller/add-profile! {:name "Foo" :email "foo@bar.com"}))
           {:name "Bar" :email "bar@foo.com"})]
      (log/info "update-profile!-test" result)
      (is (= "bar@foo.com" (:email result))
          "FIXME"))))

(deftest connect-profiles!-test
  (testing "controller/connect-profiles!"
    (database/clear!)
    (let [result
          (controller/connect-profiles!
           (:id (controller/add-profile! {:name "Foo" :email "foo@bar.com"}))
           (:id (controller/add-profile! {:name "Foo" :email "bar@foo.com"})))]
      (log/info "connect-profiles!-test" result)
      (is (= "bar@foo.com" (:email result))
          "FIXME"))))

(deftest get-profiles-test
  (testing "controller/get-profiles"
    (add-profile!-test)
    (let [result (controller/get-profiles {})]
      (log/info "get-profiles-test" result)
      (is (= "Foo" (-> result :items first :name))
          "FIXME"))))

(deftest get-suggestions-test
  (testing "controller/get-suggestions"
    (database/clear!)
    (let [profile1-id
          (:id (controller/add-profile! {:name "Foo" :email "foo@bar.com"}))
          profile2-id
          (:id (controller/add-profile! {:name "Foo" :email "bar@foo.com"}))
          result (controller/get-suggestions profile1-id {})]
      (log/info "get-suggestions-test" result)
      (is (= profile2-id (-> result :items first :id))
          "FIXME"))))

(deftest get-profile-connections-test
  (testing "controller/get-profile-connections"
    (database/clear!)
    (let [profile1-id
          (:id (controller/add-profile! {:name "Foo" :email "foo@bar.com"}))
          profile2-id
          (:id (controller/add-profile! {:name "Foo" :email "bar@foo.com"}))
          connetion-result
          (controller/connect-profiles! profile1-id profile2-id)
          result (controller/get-profile-connections profile1-id {})]
      (log/info "get-profile-connections-test" result)
      (is (= profile2-id (-> result :items first :id))
          "FIXME"))))

(deftest get-profile-details-test
  (testing "controller/get-profile-details"
    (database/clear!)
    (let [result 
          (controller/get-profile-details 
           (:id (controller/add-profile! {:name "Foo" :email "foo@bar.com"})))]
      (log/info "get-profile-details-test" result)
      (is (= "foo@bar.com" (:email result))
          "FIXME"))))
