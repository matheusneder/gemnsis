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

(deftest connecting-check-preconditions-profile-not-found-test
  (testing "logic/connecting-check-preconditions [profile-not-found]"
    (let [result (logic/connecting-check-preconditions 
                  ;; empty network
                  {:profiles {}}
                  ;; inexistent profile-id 
                  "1"
                  ;; another inexistent profile-id
                  "2")]
      (is (= (:profile-not-found logic/core-error) 
             (-> result
                 :errors
                 first))
          "FIXME"))))

(deftest connecting-check-preconditions-to-connect-profile-not-found
  (testing "logic/connecting-check-preconditions [to-connect-profile-not-found]"
    (let [result (logic/connecting-check-preconditions
                  ;; network with profile "1"
                  {:profiles {"1" {:id "1"}}}
                  ;; valid profile-id 
                  "1"
                  ;; inexistent profile-id
                  "2")]
      (is (= (:to-connect-profile-not-found logic/core-error)
             (-> result
                 :errors
                 first))
          "FIXME"))))

(deftest connecting-check-preconditions-profiles-already-connected
  (testing "logic/connecting-check-preconditions [profiles-already-connected]"
    (let [result (logic/connecting-check-preconditions
                  ;; network with profile "1" and "2"
                  {:profiles {"1" 
                              {:id "1" :connections '("2")}
                              "2"
                              {:id "2" :connections '("1")}}}
                  ;; valid profile-id (already connected)
                  "1"
                  ;; valid profile-id (already connected)
                  "2")]
      (is (= (:profiles-already-connected logic/core-error)
             (-> result
                 :errors
                 first))
          "FIXME"))))

(deftest connecting-check-preconditions-could-not-connect-itself
  (testing "logic/connecting-check-preconditions [could-not-connect-itself]"
    (let [result (logic/connecting-check-preconditions
                  ;; network with profile "1"
                  {:profiles {"1" {:id "1" }}}
                  ;; valid profile-id 
                  "1"
                  ;; valid profile-id (it self)
                  "1")]
      (is (= (:could-not-connect-itself logic/core-error)
             (-> result
                 :errors
                 first))
          "FIXME"))))

(deftest connect-test
  (testing "logic/connect"
    (let [profile1-model (logic/new-profile
                          {:name "foo" :email "foo@nubank.com"})
          profile2-model (logic/new-profile
                          {:name "bar" :email "bar@nubank.com"})
          profile1-id (:id profile1-model)
          profile2-id (:id profile2-model)
          network {:profiles {profile1-id profile1-model
                              profile2-id profile2-model}}
          result (logic/connect network profile1-id profile2-id)]
      (is (= profile1-id (-> result
                             :profiles
                             (get profile2-id)
                             :connections
                             first))
          "FIXME")
      (is (= profile2-id (-> result
                             :profiles
                             (get profile1-id)
                             :connections
                             first))
          "FIXME"))))

(def network-mock {:profiles
                   {"1" {:id "1"
                         :name "foo"
                         :visible true
                         :connections '("2")}
                    "2" {:id "2"
                         :name "bar"
                         :visible true
                         :connections '("9" "6" "1")}
                    "3" {:id "3"
                         :name "foobar"
                         :visible true
                         :connections '("9")}
                    "4" {:id "4"
                         :name "barfoo"
                         :visible true
                         :connections '("9")}
                    "5" {:id "5"
                         :name "foo-2"
                         :visible true
                         :connections '()}
                    "6" {:id "6"
                         :name "bar-2"
                         :visible true
                         :connections '("9" "2")}
                    "7" {:id "7"
                         :name "foobar-2"
                         :visible true
                         :connections '("9")}
                    "8" {:id "8"
                         :name "barfoo-2"
                         :visible true
                         :connections '("9")}
                    "9" {:id "9"
                         :name "foo"
                         :visible false
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

(deftest get-suggestions-profile-not-found-test
  (testing "logic/get-suggestions [profile-not-found]"
    (let [result (logic/get-suggestions network-mock "10")]
      (log/info "get-suggestions-profile-not-found-test" result)
      (is (= (:profile-not-found logic/core-error) 
             (-> result :errors first))
          "FIXME"))))

(deftest get-profile-connections-test
  (testing "logic/get-profile-connections"
    (let [result (logic/get-profile-connections network-mock "9")]
      (log/info "get-profile-connections-test" result)
      (is (=
           (-> network-mock :profiles (get "9") :connections)
           (map #(:id %) result))
          "FIXME"))))

(deftest get-profile-connections-profile-not-found-test
  (testing "logic/get-profile-connections [profile-not-found]"
    (let [result (logic/get-profile-connections network-mock "10")]
      (log/info "get-profile-connections-profile-not-found-test" result)
      (is (= (:profile-not-found logic/core-error) 
             (-> result :errors first))
          "FIXME"))))

(deftest validate-profile-name-required-test
  (testing "logic/validate-profile-name [profile-name-required]"
    (let [result (logic/validate-profile-name {:name "  "})]
      (log/info "validate-profile-name-required-test" result)
      (is (= (:profile-name-required logic/core-error)
             (-> result :errors first))
          "FIXME"))))

(deftest validate-profile-email-required-test
  (testing "logic/validate-profile-email [profile-email-required]"
    (let [result (logic/validate-profile-email {:email "  "})]
      (log/info "validate-profile-email-required-test" result)
      (is (= (:profile-email-required logic/core-error)
             (-> result :errors first))
          "FIXME"))))

(deftest validate-profile-email-invalid-test
  (testing "logic/validate-profile-email [profile-invalid-email]"
    (let [result (logic/validate-profile-email {:email "foo"})]
      (log/info "validate-profile-email-invalid-test" result)
      (is (= (:profile-invalid-email logic/core-error)
             (-> result :errors first))
          "FIXME"))))

(deftest validate-profile-email-uniqueness-test
  (testing "logic/validate-profile-email-uniqueness"
    (let [result 
          (logic/validate-profile-email-uniqueness 
           {:email "foo@bar"} {"1" {:email "foo@bar"}})]
      (log/info "validate-profile-email-uniqueness-test" result)
      (is (= (:profile-email-exists logic/core-error)
             (-> result :errors first))
          "FIXME"))))

(deftest profile-check-preconditions-invalid-model-test
  (testing "logic/profile-check-preconditions [invalid model scenario]"
    (let [result
          (logic/profile-check-preconditions
           {:name "" :email "foo@bar.com"}
           {:profiles {}})]
      (log/info "profile-check-preconditions-invalid-model-test" result)
      (is (= (:profile-name-required logic/core-error)
             (-> result :errors first))
          "FIXME"))))

(deftest profile-check-preconditions-duplicated-email-test
  (testing "logic/profile-check-preconditions [duplicated email scenario]"
    (let [result
          (logic/profile-check-preconditions
           {:name "Foo" :email "foo@bar.com"} 
           {"1" {:name "Bar" :email "foo@bar.com"}})]
      (log/info "profile-check-preconditions-duplicated-email-test" result)
      (is (= (:profile-email-exists logic/core-error)
             (-> result :errors first))
          "FIXME"))))

(deftest profile-check-preconditions-ok-test
  (testing "logic/profile-check-preconditions [duplicated email scenario]"
    (let [result
          (logic/profile-check-preconditions
           {:name "Foo" :email "foo@bar.com"}
           {})]
      (log/info "profile-check-preconditions-ok-test" result)
      (is (= "Foo" (:name result))
          "FIXME"))))

(deftest validate-network-capacity-ok-test
  (testing "logic/validate-network-capacity [ok scenario]"
    (let [result (logic/validate-network-capacity {} 1)]
      (is (= nil result)
          "FIXME"))))

(deftest validate-network-capacity-over-capacity-test
  (testing "logic/validate-network-capacity [over-capacity scenario]"
    (let [result (logic/validate-network-capacity {:name "Foo"} 1)]
      (is (= {:errors [(:network-over-capacity logic/core-error)]} result)
          "FIXME"))))

(deftest new-profile-test
  (testing "logic/new-profile"
    (let [result 
          (logic/new-profile {:name "Foo"
                              :email "foo@bar.com"})]
      (log/info "new-profile-test" result)
      (is (= "foo@bar.com" (:email result))
          "FIXME"))))

(deftest update-profile-test
  (testing "logic/update-profile"
    (let [result
          (logic/update-profile 
           {:name "Bar"
            :email "bar@foo.com"}
           {:name "Foo"
            :email "foo@bar.com"})]
      (log/info "update-profile-test" result)
      (is (= "foo@bar.com" (:email result))
          "FIXME"))))