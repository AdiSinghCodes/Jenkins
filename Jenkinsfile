pipeline {
    agent any

    tools {
        // Maven tool name defined in Jenkins Global Tool Configuration
        maven 'Maven-3.9.6'
        jdk 'JDK-17'
    }

    environment {
        APP_NAME = 'employee-helpdesk-system'
        REGISTRY_USER = 'adisingh'
        IMAGE_NAME = "${REGISTRY_USER}/${APP_NAME}"
        IMAGE_TAG = "v1.0.${BUILD_NUMBER}"
        SERVER_PORT = '8082'
    }

    options {
        buildDiscarder(logRotator(numToKeepStr: '10'))
        timeout(time: 30, unit: 'MINUTES')
        disableConcurrentBuilds()
    }

    stages {
        stage('1. Checkout SCM') {
            steps {
                echo '=== Stage 1: Checking out source code from GitHub ==='
                checkout scm
                git status
            }
        }

        stage('2. Code Analysis & Compile') {
            steps {
                echo '=== Stage 2: Compiling Java Application via Maven ==='
                sh 'chmod +x mvnw'
                sh './mvnw clean compile'
            }
        }

        stage('3. Unit Tests') {
            steps {
                echo '=== Stage 3: Executing Unit Tests (Mockito) ==='
                sh './mvnw test -Dtest=*UnitTest*'
            }
        }

        stage('4. Selenium Quality Gate') {
            steps {
                echo '=== Stage 4: Executing Automated Selenium UI Journey Tests ==='
                sh './mvnw test -Dtest=EmployeeHelpdeskUserJourneysTest'
            }
            post {
                always {
                    // Publish Surefire HTML/XML Test Reports in Jenkins UI
                    junit testResults: '**/target/surefire-reports/*.xml', allowEmptyResults: true
                }
                failure {
                    echo 'QUALITY GATE FAILED: Selenium browser tests failed! Capturing screenshots...'
                    archiveArtifacts artifacts: '**/target/screenshots/*.png', allowEmptyArchive: true
                }
            }
        }

        stage('5. Package Application JAR') {
            steps {
                echo '=== Stage 5: Packaging Executable Spring Boot JAR ==='
                sh './mvnw package -DskipTests'
            }
        }

        stage('6. Archive Build Artifacts') {
            steps {
                echo '=== Stage 6: Archiving Executable JAR Artifact ==='
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            }
        }

        stage('7. Build Docker Image') {
            steps {
                echo "=== Stage 7: Building Docker Image ${IMAGE_NAME}:${IMAGE_TAG} ==="
                sh "docker build -t ${IMAGE_NAME}:${IMAGE_TAG} -t ${IMAGE_NAME}:latest ."
            }
        }

        stage('8. Deploy Application Container') {
            steps {
                echo "=== Stage 8: Deploying Container on Port ${SERVER_PORT} ==="
                sh "docker stop ${APP_NAME} || true"
                sh "docker rm ${APP_NAME} || true"
                sh "docker run -d --name ${APP_NAME} -p ${SERVER_PORT}:${SERVER_PORT} ${IMAGE_NAME}:${IMAGE_TAG}"
            }
        }

        stage('9. Post-Deployment Health Check') {
            steps {
                echo '=== Stage 9: Verifying Actuator Health Endpoint ==='
                sleep 5
                sh "curl -f http://localhost:${SERVER_PORT}/actuator/health || exit 1"
            }
        }
    }

    post {
        success {
            echo "SUCCESS: Pipeline completed successfully for ${APP_NAME} (Build #${BUILD_NUMBER})!"
        }
        failure {
            echo "FAILURE: Pipeline failed for ${APP_NAME} (Build #${BUILD_NUMBER}). Deployment stopped."
        }
    }
}
