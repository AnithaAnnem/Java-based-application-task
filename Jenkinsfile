pipeline {
    agent any

    tools {
        jdk 'Java17'        // Ensure JDK 17 is configured in Jenkins
        maven 'Maven3.6.3'  // Ensure Maven 3.6.3 is configured in Jenkins
    }

    environment {
        SONARQUBE = 'SonarQube_Scanner'   // Jenkins SonarQube scanner installation name
    }

    stages {
        stage('Checkout') {
            steps {
                echo "Checking out code from GitHub..."
                git url: 'https://github.com/AnithaAnnem/Java-based-application-task.git', branch: 'main'
            }
        }

        stage('Clean & Compile') {
            steps {
                echo "Cleaning workspace and compiling Java code..."
                sh 'mvn clean compile'
            }
        }

        stage('Run Unit Tests') {
            steps {
                echo "Running unit tests..."
                sh 'mvn test'
            }
            post {
                always {
                    echo "Publishing JUnit test results..."
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('SONAR-ANITHA') {  // Your SonarQube server config name in Jenkins
                    withCredentials([string(credentialsId: 'java-sample', variable: 'SONAR_TOKEN')]) {
                        sh """
                          ${tool SONARQUBE}/bin/sonar-scanner \
                          -Dsonar.projectKey=java-sample \
                          -Dsonar.projectName=java-sample \
                          -Dsonar.sources=src \
                          -Dsonar.java.binaries=target \
                          -Dsonar.host.url=http://54.197.1.182:9000 \
                          -Dsonar.login=$SONAR_TOKEN
                        """
                    }
                }
            }
        }

        stage('Package') {
            steps {
                echo "Packaging the application..."
                sh 'mvn package'
            }
        }

        stage('Archive Artifacts') {
            steps {
                echo "Archiving JAR artifact..."
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            }
        }
    }

    post {
        success {
            echo "Build completed successfully."
        }
        failure {
            echo "Build failed. Check logs for details."
        }
        always {
            echo "Pipeline execution finished."
        }
    }
}
