pipeline {
    agent any

    tools {
        jdk 'Java17'          // JDK configured in Jenkins
        maven 'Maven3.6.3'    // Maven configured in Jenkins
    }

    environment {
        SONARQUBE_ENV = 'sonar-server' // Matches Jenkins SonarQube server name
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

        stage('Code Quality') {  // SonarQube analysis
            steps {
                echo "Running SonarQube code quality analysis..."
                withSonarQubeEnv("${SONARQUBE_ENV}") {
                    script {
                        def scannerHome = tool 'SonarQube_Scanner'
                        sh """
                            ${scannerHome}/bin/sonar-scanner \
                              -Dsonar.projectKey=java-sample \
                              -Dsonar.sources=src \
                              -Dsonar.java.binaries=target
                        """
                    }
                }
            }
        }

        stage('Quality Gate') {
            steps {
                timeout(time: 5, unit: 'MINUTES') {  // Increased timeout here
                    waitForQualityGate abortPipeline: true
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
            echo "✅ Build and code quality check completed successfully."
        }
        failure {
            echo "❌ Build or code quality check failed. Check logs for details."
        }
        always {
            echo "ℹ️ Pipeline execution finished."
        }
    }
}
