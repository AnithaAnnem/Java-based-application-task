pipeline {
    agent any

    tools {
        jdk 'Java17'
        maven 'Maven3.6.3'
    }

    environment {
        SONARQUBE_ENV = 'sonar-server'
    }

    stages {
        stage('Checkout') {
            steps {
                echo "Checking out code from GitHub..."
                git url: 'https://github.com/AnithaAnnem/Java-based-application-task.git', branch: 'main'
            }
        }

        stage('Credential Scan - Gitleaks') {
            steps {
                echo "Running Gitleaks secret scanning..."
                sh '''
                    if ! command -v gitleaks &> /dev/null; then
                      echo "ERROR: Gitleaks not installed on this agent. Please install it manually."
                      exit 1
                    fi
                    # Run Gitleaks scan (exit code 0 = no leaks, 1 = leaks found)
                    gitleaks detect --source . --report-path gitleaks-report.json --no-banner || true
                '''
            }
            post {
                always {
                    archiveArtifacts artifacts: 'gitleaks-report.json', fingerprint: true
                }
            }
        }

        stage('Code Compilation') {
            steps {
                echo "Compiling Java code..."
                sh 'mvn clean compile'
            }
        }

        stage('Unit Testing') {
            steps {
                echo "Running unit tests..."
                sh 'mvn test'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }

        stage('Code Coverage - JaCoCo') {
            steps {
                echo "Generating code coverage report..."
                sh 'mvn test jacoco:report'
            }
            post {
                always {
                    archiveArtifacts artifacts: 'target/site/jacoco/index.html', fingerprint: true
                }
            }
        }

        stage('Static Code Analysis & Bug Analysis - SonarQube') {
            steps {
                echo "Running SonarQube analysis..."
                withSonarQubeEnv("${SONARQUBE_ENV}") {
                    script {
                        def scannerHome = tool 'SonarQube_Scanner'
                        sh """
                            ${scannerHome}/bin/sonar-scanner \
                              -Dsonar.projectKey=java-sample \
                              -Dsonar.sources=src \
                              -Dsonar.java.binaries=target \
                              -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml
                        """
                    }
                }
            }
        }

        stage('Dependency Scanning - OWASP Dependency Check') {
            steps {
                echo "Scanning dependencies for vulnerabilities..."
                sh 'mvn org.owasp:dependency-check-maven:check -Dformat=ALL'
            }
            post {
                always {
                    archiveArtifacts artifacts: 'target/dependency-check-report.*', fingerprint: true
                    publishHTML([
                        allowMissing: false,
                        alwaysLinkToLastBuild: true,
                        keepAll: true,
                        reportDir: 'target',
                        reportFiles: 'dependency-check-report.html',
                        reportName: 'Dependency-Check Report'
                    ])
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
            echo " Build, tests, scans, and packaging completed successfully."
        }
        failure {
            echo " Pipeline failed. Check logs and reports."
        }
        always {
            echo " Pipeline execution finished."
        }
    }
}
