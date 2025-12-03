pipeline {
    agent any

    stages {

        stage('Check & Install Java, Maven, Tomcat (as ROOT)') {
            steps {
                sh '''
                    sudo -s << 'EOF'

                    echo "===== CHECKING JAVA ====="
                    if java -version >/dev/null 2>&1; then
                        echo "Java already installed"
                        java -version
                    else
                        echo "Installing Java 17..."
                        apt update -y
                        apt install -y openjdk-17-jdk
                    fi

                    echo "===== CHECKING MAVEN ====="
                    if mvn -version >/dev/null 2>&1; then
                        echo "Maven already installed"
                        mvn -version
                    else
                        echo "Installing Maven..."
                        apt install -y maven
                    fi

                    echo "===== CHECKING TOMCAT ====="
                    if [ -d "/opt/tomcat10" ]; then
                        echo "Tomcat already installed at /opt/tomcat10"
                    else
                        echo "Tomcat not found, installing Tomcat 10..."
                        cd /opt
                        wget https://archive.apache.org/dist/tomcat/tomcat-10/v10.1.30/bin/apache-tomcat-10.1.30.tar.gz
                        tar -xzf apache-tomcat-10.1.30.tar.gz
                        mv apache-tomcat-10.1.30 tomcat10
                        chmod +x /opt/tomcat10/bin/*.sh
                    fi

                    EOF
                '''
            }
        }

        stage('Build WAR File') {
            steps {
                sh '''
                    echo "Running Maven build..."
                    mvn clean package -DskipTests
                '''
            }
        }

        stage('Deploy WAR to Tomcat (as ROOT)') {
            steps {
                sh '''
                    sudo -s << 'EOF'

                    echo "Stopping Tomcat..."
                    /opt/tomcat10/bin/shutdown.sh || true

                    echo "Removing old deployment..."
                    rm -rf /opt/tomcat10/webapps/news-app
                    rm -f /opt/tomcat10/webapps/news-app.war

                    echo "Copying new WAR..."
                    cp target/news-app.war /opt/tomcat10/webapps/

                    echo "Starting Tomcat..."
                    /opt/tomcat10/bin/startup.sh

                    EOF
                '''
            }
        }
    }

    post {
        success {
            echo "Deployment completed successfully!"
        }
        failure {
            echo "Deployment failed!"
        }
    }
}
