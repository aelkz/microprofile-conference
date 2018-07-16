#!/bin/bash

deploy () {
    echo "Deploying a CouchDB database..."
    oc delete is couchdb &> /dev/null
    oc delete svc couchdb &> /dev/null
    oc delete dc couchdb &> /dev/null
    oc new-app --docker-image=registry.lab.example.com:5000/openshift3/couchdb --insecure-registry -eCOUCHDB_USER=admin -eCOUCHDB_PASSWORD=redhat123 &> /dev/null
}

check_deployment() {
    oc get pods | grep couch | grep Running | grep 1/1 | grep 0 &> /dev/null
    result=$?
    echo $result
    return $result
}

wait_deployment() {
    result=$(check_deployment)
    while [ "$result" != "0" ] ; do
        result=$(check_deployment)
        sleep 5
    done
    echo "Deployed"
}

deploy
wait_deployment
