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
                    publishHTML([ 
                        allowMissing: true,
                        alwaysLinkToLastBuild: true,
                        keepAll: true,
                        reportDir: 'target/site/jacoco',
                        reportFiles: 'index.html',
                        reportName: 'Code Coverage Report'
                    ])
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
                echo "Scanning dependencies for vulnerabilities (offline mode)..."
                sh '''
                    mkdir -p dependency-check-data
                    mvn org.owasp:dependency-check-maven:9.0.9:check \
                        -Danalyzer.nvd.api.enabled=false \
                        -Dnvd.api.enabled=false \
                        -Dnvd.url.modified= \
                        -Dnvd.url.base= \
                        -Dnvd.offline=true \
                        -DdataDirectory=dependency-check-data \
                        -DupdateOnly=false \
                        -Dformat=ALL \
                        -DfailBuildOnCVSS=10 \
                        -DoutputDirectory=target || true
                '''
            }
            post {
                always {
                    script {
                        def reportExists = fileExists 'target/dependency-check-report.html'
                        if (reportExists) {
                            archiveArtifacts artifacts: 'target/dependency-check-report.*', fingerprint: true
                            publishHTML([
                                allowMissing: true,
                                alwaysLinkToLastBuild: true,
                                keepAll: true,
                                reportDir: 'target',
                                reportFiles: 'dependency-check-report.html',
                                reportName: 'Dependency-Check Report'
                            ])
                        } else {
                            echo "Dependency check report not found. Skipping archive and publish."
                        }
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

        stage('Docker Build & Scan - Trivy') {
            steps {
                echo "Building Docker image and scanning with Trivy..."
                script {
                    def imageName = "java-sample-app"
                    def imageTag = "1.0"
                    // Build Docker image
                    sh "docker build -t ${imageName}:${imageTag} ."

                    // Run Trivy scan
                    sh """
                        docker run --rm \
                            -v /var/run/docker.sock:/var/run/docker.sock \
                            -v ${WORKSPACE}:/mnt/wrkspace \
                            aquasec/trivy image \
                            --exit-code 1 \
                            --severity HIGH,CRITICAL \
                            --format json \
                            --output /mnt/wrkspace/trivy-report.json \
                            ${imageName}:${imageTag} || true
                    """
                }
            }
            post {
                always {
                    archiveArtifacts artifacts: 'trivy-report.json', fingerprint: true
                }
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
            echo "Build, tests, scans, and packaging completed successfully."
        }
        failure {
            echo "Pipeline failed. Check logs and reports."
        }
        always {
            echo "Pipeline execution finished."
        }
    }
}
