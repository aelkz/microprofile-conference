#!/usr/bin/env bash
# couchdb image
# utilizar alguma imagem que extenda o padrao openshift.
# ver: https://hub.docker.com/u/openshift/
#oc new-app openshift/base-centos7~https://hub.docker.com/_/couchdb --context-dir=health-review-healthy --name=couchdb
oc get is
oc get svc
oc expose svc couchdb
oc get routes
