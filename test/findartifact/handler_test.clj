(ns findartifact.handler-test
  (:require [clojure.test :refer :all]
            [ring.mock.request :as mock]
            [findartifact.handler :refer :all]))


(deftest test-app
  (testing "main route"
    (let [response (app (mock/request :get "/"))]
      (is (= (:status response) 200))
      (is (.contains (:body response) "Find Artifact &middot; Consice UI for a wide maven artifact search"))))

  (testing "not-found route"
    (let [response (app (mock/request :get "/invalid"))]
      (is (= (:status response) 404)))))
