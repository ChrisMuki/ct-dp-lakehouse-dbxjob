#!/usr/bin/env zsh
set -e
# Why do we seperate cs download and cs setup?
# Use this for project specific cs installations and the coursier one for the general cs stuff?


# Java and certificates
su - sparkdev -c 'cs java --jvm temurin:1.17 --setup'
eval "$(cs java --jvm temurin:1.17 --env)"
CERT_SRC_DIR="$(dirname "$0")/certificates"
echo "in feature install 'java-17-setup' $CERT_SRC_DIR"
"$JAVA_HOME/bin/keytool"   -import -noprompt -trustcacerts -alias conti-ca-cert1 -file "$CERT_SRC_DIR"/conti-ca-cert1.crt -cacerts -storepass changeit
"$JAVA_HOME/bin/keytool"   -import -noprompt -trustcacerts -alias conti-ca-cert2 -file "$CERT_SRC_DIR"/conti-ca-cert2.crt -cacerts -storepass changeit
"$JAVA_HOME/bin/keytool"   -import -noprompt -trustcacerts -alias conti-ca-cert3 -file "$CERT_SRC_DIR"/conti-ca-cert3.crt -cacerts -storepass changeit