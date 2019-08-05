(ns nukr.service
  (:require
   [io.pedestal.http :as http]
   [io.pedestal.http.route :as route]
   [io.pedestal.http.body-params :as body-params]
   [ring.util.response :as ring-resp]
   [io.pedestal.log :as log]
   [io.pedestal.interceptor.error :as error-int]
   [clojure.data.json :as json]
   [nukr.controller :as controller]
   [nukr.logic :as logic]))

;; Helpers 

(defn inspect-core-errors
  "Look for :errors list on result and return ring-reponse:
   HTTP 404 when it contains :profile-not-found logic/core-error;
   HTTP 507 when it contains :network-over-capacity logic/core-error;
   HTTP 400 when it contains other kind of core-errors;
   nil      when :errors doesnt exists."
  [result]
  (if-let [errors (:errors result)]
    (cond
      (some #(= (:profile-not-found logic/core-error) %) errors)
      (ring-resp/not-found result)
      (some #(= (:network-over-capacity logic/core-error) %) errors)
      (ring-resp/status (ring-resp/response result) 507)
      :else (ring-resp/bad-request result))
    nil))

(defn http-response
  "Produce HTTP response according to inspect-core-errors rules
   or 200 status code with result on response body as json."
  [result]
  (or
   (inspect-core-errors result)
   (ring-resp/response result)))

;; Routed functions

;; POST /v1/profiles
(defn post-profiles
  "Add a new profile to network."
  [request]
  (log/debug :msg "post-profiles fired"
             :request request)
  (let [result (controller/add-profile! (:json-params request))]
    (or
     (inspect-core-errors result)
     (ring-resp/created
      (format "/v1/profiles/%s" (:id result)) result))))

;; PUT /v1/profiles/:id
(defn put-profiles
  "Edit an existing profile."
  [request]
  (log/debug :msg "put-profiles fired"
             :request request)
  (http-response (controller/update-profile!
                  (-> request :path-params :id)
                  (:json-params request))))

;; POST /v1/profiles/:id/connections
(defn post-profile-connections
  "Connect profiles."
  [request]
  (log/debug :msg "post-profile-connections fired"
             :request request)
  (http-response (controller/connect-profiles!
                  (-> request :path-params :id)
                  (-> request :json-params :id))))

;; GET /v1/profiles/:id/suggestions
(defn get-suggestions
  "Get connection suggestions for a given profile-id.
   Input  model is: {:id uuid} (from request path-params);
   Output model is:
     HTTP 200: {:total int (total items of collection)
                :showing int (number of items in current page)
                :page int (current page number, starting at 1)
                :pages int (number of pages for the given perpage value)
                :dropping int (number of dropping/skiping items)
                :items '({:id uuid :name string})}
     --or--
     HTTP 400: {:errors '({:key profile-not-found})}"
  [request]
  (log/debug :msg "get-suggestions fired"
             :request request)
  (http-response (controller/get-suggestions
                  (-> request :path-params :id)
                  (:query-params request))))

;; GET /v1/profiles
(defn get-profiles
  "List profiles
   Input  model is: {:perpage int (optional deafault 10, limited to 50)
                     :page int (optional default 1))} 
                     (both :perpage and :page from request query-params);
   Output model is: 
    HTTP 200: {:total int (total items of collection)
               :showing int (number of items in current page)
               :page int (current page number, starting at 1)
               :pages int (number of pages for the given perpage value)
               :dropping int (number of dropping/skiping items)
               :items '({:id uuid :name string})}"
  [request]
  (log/debug :msg "get-profiles fired"
             :request request)
  (http-response
   (controller/get-profiles (:query-params request))))

;; GET /v1/profiles/:id/connections
(defn get-profile-connections
  "Get connections for a given profile-id.
   Input  model is: {:id uuid} (from request path-params);
   Output model is:
     HTTP 200: {:total int (total items of collection)
                :showing int (number of items in current page)
                :page int (current page number, starting at 1)
                :pages int (number of pages for the given perpage value)
                :dropping int (number of dropping/skiping items)
                :items '({:id uuid :name string})}
     --or--
     HTTP 400: {:errors '({:key profile-not-found})}"
  [request]
  (log/debug :msg "get-profile-connections fired"
             :request request)
  (http-response (controller/get-profile-connections
                  (-> request :path-params :id)
                  (:query-params request))))

;; GET /v1/profiles/:id
(defn get-profile-details
  "Get profile details for a given profile-id.
   Input  model is: {:id uuid} (from request path-params);
   Output model is:
     HTTP 200: {:id uuid
                :name string
                :email string
                :visible bool
                :connections int}
     --or--
     HTTP 400: {:errors '({:key profile-not-found})}"
  [request]
  (log/debug :msg "get-profile-details fired"
             :request request)
  (http-response (controller/get-profile-details
                  (-> request :path-params :id))))

;; Error handling

(def internal-error-msg
   (str
    "Internal error has occurred. Error details was logged, "
    "in order to see what happens, use errorid value to find "
    "the details on logs (look for :error-id key)."))

(defn catch-all-error-handler
  "Generate an identifier, log the ex linked to it. Expose id on response 
   to be able to track the error for an specific request on logs."
  [ctx ex]
  (let [error-id (logic/uuid)]
    (log/error :msg "Internal error"
               :error-id error-id
               :ex ex)
    (assoc ctx
           :response
           {:status 500
            :headers {"Content-Type" "application/json"}
            :body
            (json/write-str
             {:errorid error-id
              :msg internal-error-msg})})))

(def service-error-handler
  "catch-all error handler"
  (error-int/error-dispatch
   [ctx ex]
   :else
   (catch-all-error-handler ctx ex)))

;; interceptor
(def common-interceptors 
  [service-error-handler (body-params/body-params) http/json-body])

;; routes
(def routes #{["/v1/profiles"
               :get (conj common-interceptors `get-profiles)]
              ["/v1/profiles"
               :post (conj common-interceptors `post-profiles)]
              ["/v1/profiles/:id"
               :put (conj common-interceptors `put-profiles)]
              ["/v1/profiles/:id"
               :get (conj common-interceptors `get-profile-details)]
              ["/v1/profiles/:id/suggestions"
               :get (conj common-interceptors `get-suggestions)]
              ["/v1/profiles/:id/connections"
               :get (conj common-interceptors `get-profile-connections)]
              ["/v1/profiles/:id/connections"
               :post (conj common-interceptors `post-profile-connections)]})

;; service configuration
(def service {:env :prod
              ::http/routes routes
              ::http/type :jetty
              ::http/host "0.0.0.0"
              ::http/port 8080
              ::http/container-options {:h2c? true
                                        :h2? false
                                        :ssl? false}})
