(ns instagram.oauth)

(defrecord OauthCredentials
  [#^String client-id
   #^String client-secret
   #^String redirect-uri])


(defn make-oauth-creds
  "Create an OauthCredentials object with supplied params"
  [client-id client-secret redirect-uri]

  (OauthCredentials. client-id client-secret redirect-uri))
