JAVA_HOME_USER=/home/$USER/java/jdk-17.0.5+8
JAVA_HOME_DEBIAN=/usr/lib/jvm/java-17-openjdk-amd64

if [ -d $JAVA_HOME_USER ]; then
    export JAVA_HOME=$JAVA_HOME_USER
elif [ -d $JAVA_HOME_DEBIAN ]; then
    export JAVA_HOME=$JAVA_HOME_DEBIAN
fi

mvn clean install package -Dbitwig.extension.directory=target
