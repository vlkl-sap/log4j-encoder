#!/bin/bash

set -e

mvn -q compile exec:java -Dexec.mainClass="com.example.log4j.Main"
