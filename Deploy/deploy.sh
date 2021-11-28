#!/bin/bash

clear

echo " -----------------------------------------------------------------------"
echo '| Project: Person serive                                                |'
echo '| Repository: https://github.com/ewerton255/person_service              |'
echo '|               © 2021 Edcor. All Rights Reserved.                      |'
echo " -----------------------------------------------------------------------"

sleep 1s

echo " -----------------------------------------------------------------------"
echo '|                         Starting deploy                               |'
echo " -----------------------------------------------------------------------"

sleep 1s

echo " -----------------------------------------------------------------------"
echo '|                  Starting compiling application...                    |'
echo " -----------------------------------------------------------------------"

cd ../Sources/PersonApi

sleep 2s

mvn clean package -X -DskipTests

sleep 1s

echo " -----------------------------------------------------------------------"
echo '|                        Copying generated .jar...                      |'
echo " -----------------------------------------------------------------------"

cd ../../Deploy

cp ../Sources/PersonApi/target/*.jar ./app.jar

sleep 1s

echo " -----------------------------------------------------------------------"
echo '|      Starting deploy with docker-comopose in background mode...       |'
echo " -----------------------------------------------------------------------"

docker-compose up -d --build

sleep 1s

echo " -----------------------------------------------------------------------"
echo '|                          Removing .jar...                             |'
echo " -----------------------------------------------------------------------"

rm -f ./app.jar

sleep 1s

echo " -----------------------------------------------------------------------"
echo '|                       Deploy successful!                              |'
echo " -----------------------------------------------------------------------"

sleep 1s

docker ps