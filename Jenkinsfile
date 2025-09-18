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
                echo 'Checking out source code...'
                checkout([$class: 'GitSCM',
                    branches: [[name: '*/main']],
                    userRemoteConfigs: [[url: 'https://github.com/AnithaAnnem/SecretSantaJava.git']]
                ])
            }
        }

        stage('Gitleaks Secret Scanning') {
            steps {
                echo 'Running Gitleaks secret scanning...'
                sh '''
                    if ! command -v gitleaks >/dev/null 2>&1; then
                        echo "ERROR: Gitleaks not installed on this agent."
                        exit 1
                    fi
                    gitleaks detect --report-path=gitleaks-report.json --report-format=json || true
                '''
                archiveArtifacts artifacts: 'gitleaks-report.json', allowEmptyArchive: true
            }
        }

        stage('Code Compilation') {
            steps {
                echo 'Compiling code...'
                sh 'mvn clean compile'
            }
        }

        stage('Unit Testing') {
            steps {
                echo 'Running unit tests...'
                sh 'mvn test'
            }
        }

        stage('Code Coverage - JaCoCo') {
            steps {
                echo 'Running code coverage with JaCoCo...'
                sh 'mvn jacoco:report'
            }
        }

        stage('Static Code Analysis & Bug Analysis - SonarQube') {
            steps {
                echo 'Running SonarQube analysis...'
                withSonarQubeEnv('sonar-server') {
                    sh 'mvn sonar:sonar'
                }
            }
        }

        stage('Dependency Scanning - OWASP Dependency Check') {
            steps {
                echo 'Running Dependency Scanning...'
                sh '''
                    mvn org.owasp:dependency-check-maven:9.0.9:check \
                        -Danalyzer.nvd.api.enabled=false \
                        -Dformat=HTML \
                        -DoutputDirectory=target || true
                '''
                publishHTML([
                    reportDir: 'target',
                    reportFiles: 'dependency-check-report.html',
                    reportName: 'Dependency-Check Report'
                ])
            }
        }

        stage('Package') {
            steps {
                echo 'Packaging application...'
                sh 'mvn package'
            }
        }

        stage('Archive Artifacts') {
            steps {
                echo 'Archiving artifacts...'
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            }
        }
    }

    post {
        always {
            echo "Pipeline execution finished."
        }
        success {
            echo "Pipeline succeeded."
        }
        failure {
            echo "Pipeline failed. Check logs and reports."
        }
    }
}
