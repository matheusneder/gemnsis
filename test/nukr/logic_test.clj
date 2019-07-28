(ns nukr.logic-test
  (:require 
   [clojure.test :refer :all]
   [nukr.logic :as logic]
   [clojure.tools.logging :as log]))

(deftest connect-single-test
  (testing "logic/connect-single"
    (let [target-profile-model (logic/new-profile {:name "foo" :email "foo@nubank.com"})
          profile-source-id "321"
          result (logic/connect-single target-profile-model profile-source-id)]
      (is (= profile-source-id (-> result :connections first))
          "FIXME"))))

(deftest connect-test
  (testing "logic/connect"
    (let [profile1-model (logic/new-profile {:name "foo" :email "foo@nubank.com"})
          profile2-model (logic/new-profile {:name "bar" :email "bar@nubank.com"})
          profile1-id (:id profile1-model)
          profile2-id (:id profile2-model)
          network {:profiles {profile1-id profile1-model
                              profile2-id profile2-model}}
          result (logic/connect network profile1-id profile2-id)]
      (is (= profile1-id (-> result :profiles (get profile2-id) :connections first))
          "FIXME")
      (is (= profile2-id (-> result :profiles (get profile1-id) :connections first))
          "FIXME"))))

(def network-mock {:profiles
                   {"1" {:id "1"
                         :name "foo"
                         :suggestible true
                         :connections '("2")}
                    "2" {:id "2"
                         :name "bar"
                         :suggestible true
                         :connections '("9" "6" "1")}
                    "3" {:id "3"
                         :name "foobar"
                         :suggestible true
                         :connections '("9")}
                    "4" {:id "4"
                         :name "barfoo"
                         :suggestible true
                         :connections '("9")}
                    "5" {:id "5"
                         :name "foo-2"
                         :suggestible true
                         :connections '()}
                    "6" {:id "6"
                         :name "bar-2"
                         :suggestible true
                         :connections '("9" "2")}
                    "7" {:id "7"
                         :name "foobar-2"
                         :suggestible true
                         :connections '("9")}
                    "8" {:id "8"
                         :name "barfoo-2"
                         :suggestible true
                         :connections '("9")}
                    "9" {:id "9"
                         :name "foo"
                         :suggestible false
                         :connections '("2" "3" "4" "6" "7" "8")}}})

(deftest get-degree-sequence-test
  (testing "logic/get-degree-sequence"
    (let [result (logic/get-degree-sequence (:profiles network-mock))]
      (log/info "get-degree-sequence-test" result)
      (is (= "9" (-> result first :id))
          "FIXME")
      (is (= "5" (-> result last :id))
          "FIXME"))))

(deftest get-suggestions-test
  (testing "logic/get-suggestions"
    (let [result (logic/get-suggestions network-mock "3")]
      (log/info "get-suggestions-test" result)
      (is (= "2" (-> result first :id))
          "FIXME")
      (is (= "5" (-> result last :id))
          "FIXME"))))

(deftest get-profile-connections-test
  (testing "logic/get-profile-connections"
    (let [result (logic/get-profile-connections network-mock "9")]
      (log/info "get-profile-connections-test" result)
      (is (=
           (-> network-mock :profiles (get "9") :connections)
           (map #(:id %) result))
          "FIXME"))))