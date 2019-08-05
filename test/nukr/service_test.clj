(ns nukr.service-test
  (:require
   [clojure.test :refer :all]
   [io.pedestal.test :refer :all]
   [io.pedestal.http :as bootstrap]
   [clojure.data.json :as json]
   [nukr.service :as service]
   [clojure.tools.logging :as log]
   [nukr.controller :as controller]
   [nukr.database :as database]
   [nukr.logic :as logic]))

(def service
  (::bootstrap/service-fn (bootstrap/create-servlet service/service)))

;; Helpers

(defn convert-reponse-body
  [ring-response]
  (merge
   ring-response
   {:body (json/read-str
           (:body ring-response)
           :key-fn keyword)}))

(defn response
  ([verb path body]
   (let [request-body-json (json/write-str body)
         ring-response (response-for
                        service
                        (keyword verb) path
                        :headers {"Content-Type" "application/json"}
                        :body request-body-json)]
     (convert-reponse-body ring-response)))
  ([verb path]
   (let [ring-response (response-for
                        service
                        (keyword verb) path)]
     (convert-reponse-body ring-response))))

;; Tests

(deftest post-profiles-test
  (testing "POST /v1/profiles"
    (database/reset)
    (let [result (response 
                  :post 
                  "/v1/profiles"
                  {:name "Foo" :email "foo@bar.com"})]
      (log/info "post-profiles-test" result)
      (is (= 201 (:status result))
          "FIXME")
      (is (= "foo@bar.com" (-> result :body :email))
          "FIXME"))))

(deftest post-profiles-invalid-model-test
  (testing "POST /v1/profiles"
    (database/reset)
    (let [result (response
                  :post
                  "/v1/profiles"
                  {:name "   " :email "foobar.com"})]
      (log/info "post-profiles-invalid-model-test" result)
      (is (= 400 (:status result))
          "FIXME")
      (is (some 
           #(= (:profile-name-required logic/core-error ) %) 
           (-> result :body :errors))
          "FIXME")
      (is (some
           #(= (:profile-invalid-email logic/core-error ) %)
           (-> result :body :errors))
          "FIXME"))))

(deftest get-profiles-test
  (testing "GET /v1/profiles"
    (post-profiles-test)
    (let [result (response
                  :get
                  "/v1/profiles")]
      (log/info "get-profiles-test" result)
      (is (= 200 (:status result))
          "FIXME")
      (is (= 1 (-> result :body :total))
          "FIXME")
      (is (= "Foo" (-> result :body :items first :name))
          "FIXME"))))

(deftest put-profiles-id-test
  (testing "PUT /v1/profiles/:id"
    (database/reset)
    (let [profile-id 
          (:id (controller/add-profile! {:name "Foo" :email "foo@bar.com"}))
          result (response
                  :put
                  (format "/v1/profiles/%s" profile-id)
                  {:name "Bar" :email "bar@foo.com"})]
      (log/info "put-profiles-id-test" result)
      (is (= 200 (:status result))
          "FIXME")
      (is (= "bar@foo.com" (-> result :body :email))
          "FIXME"))))

(deftest put-profiles-id-invalid-model-test
  (testing "PUT /v1/profiles/:id"
    (database/reset)
    (let [profile-id
          (:id (controller/add-profile! {:name "Foo" :email "foo@bar.com"}))
          result (response
                  :put
                  (format "/v1/profiles/%s" profile-id)
                  {:name "" :email "barfoo.com"})]
      (log/info "put-profiles-id-test" result)
      (is (= 400 (:status result))
          "FIXME")
      (is (some
           #(= (:profile-name-required logic/core-error) %)
           (-> result :body :errors))
          "FIXME")
      (is (some
           #(= (:profile-invalid-email logic/core-error) %)
           (-> result :body :errors))
          "FIXME"))))

(deftest get-profiles-id-test
  (testing "GET /v1/profiles/:id"
    (database/reset)
    (let [profile-id
          (:id (controller/add-profile! {:name "Foo" :email "foo@bar.com"}))
          result (response
                  :get
                  (format "/v1/profiles/%s" profile-id))]
      (log/info "get-profiles-id-test" result)
      (is (= 200 (:status result))
          "FIXME")
      (is (= "foo@bar.com" (-> result :body :email))
          "FIXME"))))

(deftest get-profiles-id-not-foundtest
  (testing "GET /v1/profiles/:id"
    (database/reset)
    (let [profile-id (logic/uuid)
          result (response
                  :get
                  (format "/v1/profiles/%s" profile-id))]
      (log/info "get-profiles-id-test" result)
      (is (= 404 (:status result))
          "FIXME")
      (is (= 
           (:profile-not-found logic/core-error) 
           (-> result :body :errors first))
          "FIXME"))))

(deftest post-profiles-id-connections-test
  (testing "POST /v1/profiles/:id/connections"
    (database/reset)
    (let [profile1-id
          (:id (controller/add-profile! {:name "Foo" :email "foo@bar.com"}))
          profile2-id
          (:id (controller/add-profile! {:name "Bar" :email "bar@foo.com"}))
          result (response
                  :post
                  (format "/v1/profiles/%s/connections" profile1-id)
                  {:id profile2-id})]
      (log/info "post-profiles-id-connections-test" result)
      (is (= 200 (:status result))
          "FIXME")
      (is (= "bar@foo.com" (-> result :body :email))
          "FIXME"))))

(deftest get-profiles-id-suggestions-test
  (testing "GET /v1/profiles/:id/suggestions"
    (database/reset)
    (let [profile1-id
          (:id (controller/add-profile! {:name "Foo" :email "foo@bar.com"}))
          profile2-id
          (:id (controller/add-profile! {:name "Bar" :email "bar@foo.com"}))
          result (response
                  :get
                  (format "/v1/profiles/%s/suggestions" profile1-id))]
      (log/info "get-profiles-id-suggestions-test" result)    
      (is (= 200 (:status result))
          "FIXME")
      (is (= 1 (-> result :body :total))
          "FIXME")
      (is (= "Bar" (-> result :body :items first :name))
          "FIXME"))))

(deftest get-profiles-id-connections-test
  (testing "GET /v1/profiles/:id/connections"
    (database/reset)
    (let [profile1-id
          (:id (controller/add-profile! {:name "Foo" :email "foo@bar.com"}))
          profile2-id
          (:id (controller/add-profile! {:name "Bar" :email "bar@foo.com"}))
          connection (controller/connect-profiles! profile1-id profile2-id)
          result (response
                  :get
                  (format "/v1/profiles/%s/connections" profile1-id))]
      (log/info "get-profiles-id-connections-test" result)
      (is (= 200 (:status result))
          "FIXME")
      (is (= 1 (-> result :body :total))
          "FIXME")
      (is (= "Bar" (-> result :body :items first :name))
          "FIXME"))))