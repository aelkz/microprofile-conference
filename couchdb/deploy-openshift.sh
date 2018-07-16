#!/usr/bin/env bash
# couchdb image
oc create secret generic erlang --from-literal=erlang=$(LC_ALL=C tr -cd '[:alnum:]' < /dev/urandom | head -c 64)
oc create secret generic couchdb --from-literal=user=$(sed $(perl -e "print int rand(99999)")"q;d" /usr/share/dict/words) --from-literal=pass=$(LC_ALL=C tr -cd '[:alnum:]' < /dev/urandom | head -c 32)
oc create -f kubernetes/production
