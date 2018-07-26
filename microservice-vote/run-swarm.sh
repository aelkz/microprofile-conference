#!/bin/bash
mvn clean package -DskipTests -Pwildfly wildfly-swarm:run -Dswarm.http.port=7070 -Dswarm.management.http.disable=true #-Dswarm.debug.port=50509
