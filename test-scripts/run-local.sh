#!/bin/bash
echo "Building Maven app..."
mvn clean package -DskipTests

echo "Running Spring Boot app..."
java -jar target/kubebridge-1.0.0.jar
