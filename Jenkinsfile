pipeline {
    agent any

    tools {
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
                sh 'chmod +x mvnw || true'
                sh './mvnw clean compile'
            }
        }

        stage('3. Unit Tests') {
            steps {
                echo '=== Stage 3: Executing Unit Tests (Mockito) ==='
                sh './mvnw test -Dtest=TicketServiceTest'
            }
        }

        stage('4. Selenium Quality Gate') {
            steps {
                echo '=== Stage 4: Executing Automated Selenium UI Journey Tests ==='
                sh './mvnw test -Dtest=EmployeeHelpdeskUserJourneysTest'
            }
            post {
                always {
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

        stage('8. Publish to Docker Registry') {
            steps {
                echo "=== Stage 8: Publishing Docker Image ${IMAGE_NAME}:${IMAGE_TAG} to Registry ==="
                sh "docker push ${IMAGE_NAME}:${IMAGE_TAG} || echo 'Docker push skipped (local registry fallback)'"
            }
        }

        stage('9. Ansible Target Provisioning & Deploy') {
            steps {
                echo "=== Stage 9: Provisioning Target Server via Ansible ==="
                sh "ansible-playbook -i devops/ansible/inventory.ini devops/ansible/playbook.yml || docker run -d --name ${APP_NAME} -p ${SERVER_PORT}:${SERVER_PORT} ${IMAGE_NAME}:${IMAGE_TAG}"
            }
        }

        stage('10. Post-Deployment Health Check & Rollback Gate') {
            steps {
                echo '=== Stage 10: Verifying Actuator Health Endpoint ==='
                sleep 5
                sh "curl -f http://localhost:${SERVER_PORT}/actuator/health || bash devops/ansible/health_check_rollback.sh"
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
