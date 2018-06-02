#!/bin/bash
mvn clean package -Pwildfly wildfly-swarm:run -Dswarm.http.port=6060 -Dswarm.management.http.disable=true #-Dswarm.debug.port=60509
