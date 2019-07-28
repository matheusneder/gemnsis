(ns nukr.service
  (:require 
   [io.pedestal.http :as http]
   [io.pedestal.http.route :as route]
   [io.pedestal.http.body-params :as body-params]
   [ring.util.response :as ring-resp]
   [io.pedestal.log :as log]
   [nukr.controller :as controller]))

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

(defn post-profiles
  [request]
  (log/info :msg request)
  (let [profile-model (controller/add-profile! (:json-params request))]
    (ring-resp/response profile-model)))

(defn post-profile-connections
  [request]
  (log/info :msg request)
  (ring-resp/response (controller/connect-profiles! 
                       (-> request :path-params :id) 
                       (-> request :json-params :id))))

(defn get-suggestions
  "Get connection suggestions for a given profile-id.
   Input  model is: {:id uuid} (from route path);
   Output model is:
     HTTP 200: {:id uuid :name string};
     HTTP 400: {:errors '({:key profile-not-found})}"  
  [request]
  (log/info :msg request)
  (let [result (controller/get-suggestions
                (-> request :path-params :id))]
    (if
     (:errors result)
      (ring-resp/bad-request result)
      (ring-resp/response result))))

;; Defines "/" and "/about" routes with their associated :get handlers.
;; The interceptors defined after the verb map (e.g., {:get home-page}
;; apply to / and its children (/about).
(def common-interceptors [(body-params/body-params) http/json-body])

;; Tabular routes
(def routes #{["/v1" :get (conj common-interceptors `dump-database)]
              ["/v1" :delete (conj common-interceptors `reset-database)]
              ["/v1/profiles" :post (conj common-interceptors `post-profiles)]
              ["/v1/profiles/:id/suggestions" :get (conj common-interceptors `get-suggestions)]
              ["/v1/profiles/:id/connections" :post (conj common-interceptors `post-profile-connections)]})


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
              ::http/port 9000
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