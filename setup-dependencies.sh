#!/bin/bash

currentPath=$(pwd)

echo "setting up dependencies in a local repo in " $currentPath

#setup dependencies
mvn install:install-file -Dfile=./lib/natives/libcplex1262.jnilib -DgroupId=cplex-so \
    -DartifactId=cplex-so -Dversion=1262 -Dpackaging=so -DlocalRepositoryPath=$currentPath/viepep-repo

mvn install:install-file -Dfile=./lib/jar/cplex1262.jar -DgroupId=cplex-so \
    -DartifactId=cplex-jar -Dversion=1262 -Dpackaging=jar -DlocalRepositoryPath=$currentPath/viepep-repo

mvn install:install-file -Dfile=./lib/jar/javailp-1.2a.jar -DgroupId=java-ilp-jar \
    -DartifactId=java-ilp-jar -Dversion=1.2a -Dpackaging=jar -DlocalRepositoryPath=$currentPath/viepep-repo

echo "set up dependencies in a local repo in " $currentPath " done"