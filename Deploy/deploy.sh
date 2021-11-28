#!/bin/bash

clear
echo 'Starting deploy...'
echo 'Compiling application...'

cd ../Sources/PersonApi

mvn clean package -X -DskipTests

echo 'Copying generated .jar...'

cd ../../Deploy

cp ../Sources/PersonApi/target/*.jar ./app.jar

echo 'Starting deploy with docker-comopose in background mode...'

docker-compose up -d --build

echo 'Removing .jar...'

rm -f ./app.jar

sleep 1s

echo 'Deploy successful!'

sleep 1s

docker ps