set JAVA_HOME=%JAVA_HOME17%
set SOURCE=C:/Privat/Programming/Libraries/USB/purejavahidapi/target
set DESTINATION=./maven-local-repository

mvn deploy:deploy-file -Dfile=%SOURCE%/purejavahidapi-0.0.18.jar -Dsources=%SOURCE%/purejavahidapi-0.0.18-sources.jar -Djavadoc=%SOURCE%/purejavahidapi-0.0.18-javadoc.jar -DgroupId=purejavahidapi -DartifactId=purejavahidapi -Dversion=0.0.18 -Dpackaging=jar -Durl=file:%DESTINATION% -DrepositoryId=maven-repository -DupdateReleaseInfo=true