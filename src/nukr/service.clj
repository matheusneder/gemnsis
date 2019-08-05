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

(defn about-page
  [request]
  (ring-resp/response (format "Clojure %s - served from %s"
                              (clojure-version)
                              (route/url-for ::about-page))))

(defn dump-database
  [request]
  (ring-resp/response (controller/dump-database)))

(defn reset-database
  [request]
  (ring-resp/response (controller/reset-database)))

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

(defn post-profiles
  "Add a new profile to network."
  [request]
  (log/debug
   :msg "post-profiles fired"
   :request request)
  (let [result (controller/add-profile! (:json-params request))]
    (or
     (inspect-core-errors result)
     (ring-resp/created
      (format "/v1/profiles/%s" (:id result)) result))))

(defn put-profiles
  [request]
  (log/info :msg request)
  (let [result (controller/update-profile! 
                (-> request :path-params :id)
                (:json-params request))]
    (if (:errors result)
      (ring-resp/bad-request result)
      (ring-resp/response result))))

(defn post-profile-connections
  [request]
  (log/info :msg request)
  (let [result (controller/connect-profiles!
                (-> request :path-params :id)
                (-> request :json-params :id))]
    (if (:errors result)
      (ring-resp/bad-request result)
      (ring-resp/response result))))

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
  (log/info :msg request)
  (let [result (controller/get-suggestions
                (-> request :path-params :id)
                (:query-params request))]
    (if (:errors result)
      (ring-resp/bad-request result)
      (ring-resp/response result))))

(defn get-profiles
  "Get profiles
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
  (let [result
        (controller/get-profiles (:query-params request))]
    (log/info :msg result)
    (ring-resp/response result)))

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
  (log/info :msg request)
  (let [result (controller/get-profile-connections
                (-> request :path-params :id)
                (:query-params request))]
    (if (:errors result)
      (ring-resp/bad-request result)
      (ring-resp/response result))))

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
  (log/info :msg request)
  (let [result (controller/get-profile-details
                (-> request :path-params :id))]
    (if (:errors result)
      (ring-resp/bad-request result)
      (ring-resp/response result))))

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

;; Defines "/" and "/about" routes with their associated :get handlers.
;; The interceptors defined after the verb map (e.g., {:get home-page}
;; apply to / and its children (/about).
(def common-interceptors [service-error-handler (body-params/body-params) http/json-body])

;; Tabular routes
(def routes #{["/v1"
               :get (conj common-interceptors `dump-database)]
              ["/v1"
               :delete (conj common-interceptors `reset-database)]
              ["/v1/profiles/"
               :get (conj common-interceptors `get-profiles)]         
              ["/v1/profiles/"
               :post (conj common-interceptors `post-profiles)]
              ["/v1/profiles/:id"
               :put (conj common-interceptors `put-profiles)]
              ["/v1/profiles/:id"
               :get (conj common-interceptors `get-profile-details)]
              ["/v1/profiles/:id/suggestions/"
               :get (conj common-interceptors `get-suggestions)]
              ["/v1/profiles/:id/connections/"
               :get (conj common-interceptors `get-profile-connections)]
              ["/v1/profiles/:id/connections/"
               :post (conj common-interceptors `post-profile-connections)]})


;; Map-based routes
;(def routes `{"/" {:interceptors [(body-params/body-params) http/html-body]
;                   :get home-page
;                   "/about" {:get about-page}}})

;; Terse/Vector-based routes
;(def routes
;  `[[["/" {:get home-page}
;      ^:interceptors [(body-params/body-params) http/html-body]
;      ["/about" {:get about-page}]]]])


;; Consumed by nukr.server/create-server
;; See http/default-interceptors for additional options you can configure
(def service {:env :prod
              ;; You can bring your own non-default interceptors. Make
              ;; sure you include routing and set it up right for
              ;; dev-mode. If you do, many other keys for configuring
              ;; default interceptors will be ignored.
              ;; ::http/interceptors []
              ::http/routes routes

              ;; Uncomment next line to enable CORS support, add
              ;; string(s) specifying scheme, host and port for
              ;; allowed source(s):
              ;;
              ;; "http://localhost:8080"
              ;;
              ;;::http/allowed-origins ["scheme://host:port"]

              ;; Tune the Secure Headers
              ;; and specifically the Content Security Policy appropriate to your service/application
              ;; For more information, see: https://content-security-policy.com/
              ;;   See also: https://github.com/pedestal/pedestal/issues/499
              ;;::http/secure-headers {:content-security-policy-settings {:object-src "'none'"
              ;;                                                          :script-src "'unsafe-inline' 'unsafe-eval' 'strict-dynamic' https: http:"
              ;;                                                          :frame-ancestors "'none'"}}

              ;; Root for resource interceptor that is available by default.
              ::http/resource-path "/public"

              ;; Either :jetty, :immutant or :tomcat (see comments in project.clj)
              ;;  This can also be your own chain provider/server-fn -- http://pedestal.io/reference/architecture-overview#_chain_provider
              ::http/type :jetty
              ::http/host "0.0.0.0"
              ::http/port 8080
              ;; Options to pass to the container (Jetty)
              ::http/container-options {:h2c? true
                                        :h2? false
                                        ;:keystore "test/hp/keystore.jks"
                                        ;:key-password "password"
                                        ;:ssl-port 8443
                                        :ssl? false
                                        ;; Alternatively, You can specify you're own Jetty HTTPConfiguration
                                        ;; via the `:io.pedestal.http.jetty/http-configuration` container option.
                                        ;:io.pedestal.http.jetty/http-configuration (org.eclipse.jetty.server.HttpConfiguration.)
                                        }})
