pipeline {
    agent any

    environment {
        REGISTRY = "${env.REGISTRY_HOST ?: 'ghcr.io'}"
        IMAGE_NAME = "${env.REGISTRY_IMAGE_NAME ?: 'muthukumaritaly/radiology-manager'}"
        IMAGE_TAG = "${env.BUILD_NUMBER}"
        
        DOCKER_REGISTRY_CREDS = "${env.DOCKER_REGISTRY_CREDS_ID ?: 'docker-registry-credentials'}"
        KUBECONFIG_CREDS_ID = "${env.KUBECONFIG_CREDS_ID_VAL ?: 'k3s-kubeconfig'}"
        PRODUCTION_SSH_CREDS = "${env.PRODUCTION_SSH_CREDS_ID ?: 'production-ssh-key'}"
        DATABASE_CREDS_ID = "${env.DATABASE_CREDS_ID_VAL ?: 'postgres-credentials'}"
        
        PRODUCTION_HOST = "${env.DEPLOY_PRODUCTION_HOST ?: 'production-target-ip'}"
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build & Test') {
            steps {
                dir('backend') {

                    sh 'chmod +x mvnw'
                    sh './mvnw clean verify'
                }
            }
        }

        stage('Docker Build & Push') {
            steps {
                script {
                    sh "docker build -t ${env.REGISTRY}/${env.IMAGE_NAME}:${env.IMAGE_TAG} -f Dockerfile ."
                    sh "docker tag ${env.REGISTRY}/${env.IMAGE_NAME}:${env.IMAGE_TAG} ${env.REGISTRY}/${env.IMAGE_NAME}:latest"
                    
                    withCredentials([usernamePassword(credentialsId: env.DOCKER_REGISTRY_CREDS, usernameVariable: 'REGISTRY_USER', passwordVariable: 'REGISTRY_PASS')]) {
                        sh "echo \$REGISTRY_PASS | docker login ${env.REGISTRY} -u \$REGISTRY_USER --password-stdin"
                        sh "docker push ${env.REGISTRY}/${env.IMAGE_NAME}:${env.IMAGE_TAG}"
                        sh "docker push ${env.REGISTRY}/${env.IMAGE_NAME}:latest"
                    }
                }
            }
        }

        stage('Deploy to Docker (Standalone/Local)') {
            when {
                expression { env.DEPLOY_TARGET == 'docker' }
            }
            steps {
                script {
                    def isLocal = env.PRODUCTION_HOST == 'localhost' || 
                                  env.PRODUCTION_HOST == '127.0.0.1' || 
                                  !env.PRODUCTION_HOST || 
                                  env.PRODUCTION_HOST == 'production-target-ip'

                    if (isLocal) {
                        echo "Deploying locally to the Jenkins runner Docker daemon..."
                        withCredentials([
                            usernamePassword(credentialsId: env.DATABASE_CREDS_ID, usernameVariable: 'SPRING_DATASOURCE_USERNAME', passwordVariable: 'SPRING_DATASOURCE_PASSWORD')
                        ]) {
                            sh """
                                # Create docker network if it doesn't exist
                                docker network create radiology-net || true
                                
                                # Terminate and remove the legacy container instance
                                docker stop radiology-manager || true
                                docker rm radiology-manager || true
                                
                                # Run the production container on 'radiology-net' network
                                docker run -d \
                                  --name radiology-manager \
                                  --network radiology-net \
                                  -p 8080:8080 \
                                  -e SPRING_PROFILES_ACTIVE=prod \
                                  -e SPRING_DATASOURCE_URL=jdbc:postgresql://radiology-db:5432/radiologydb \
                                  -e SPRING_DATASOURCE_USERNAME=${SPRING_DATASOURCE_USERNAME} \
                                  -e SPRING_DATASOURCE_PASSWORD=${SPRING_DATASOURCE_PASSWORD} \
                                  --restart unless-stopped \
                                  ${env.REGISTRY}/${env.IMAGE_NAME}:${env.IMAGE_TAG}
                            """
                        }
                    } else {
                        echo "Deploying remotely to host ${env.PRODUCTION_HOST} via SSH..."
                        withCredentials([
                            sshUserPrivateKey(credentialsId: env.PRODUCTION_SSH_CREDS, keyFileVariable: 'SSH_KEY', usernameVariable: 'SSH_USER'),
                            usernamePassword(credentialsId: env.DATABASE_CREDS_ID, usernameVariable: 'SPRING_DATASOURCE_USERNAME', passwordVariable: 'SPRING_DATASOURCE_PASSWORD'),
                            usernamePassword(credentialsId: env.DOCKER_REGISTRY_CREDS, usernameVariable: 'REGISTRY_USER', passwordVariable: 'REGISTRY_PASS')
                        ]) {
                            sh """
                                ssh -i ${SSH_KEY} -o StrictHostKeyChecking=no ${SSH_USER}@${env.PRODUCTION_HOST} "
                                    # Create docker network if it doesn't exist
                                    docker network create radiology-net || true

                                    # Registry authentication on the deployment server
                                    echo ${REGISTRY_PASS} | docker login ${env.REGISTRY} -u ${REGISTRY_USER} --password-stdin
                                    
                                    # Pull the newly built image
                                    docker pull ${env.REGISTRY}/${env.IMAGE_NAME}:${env.IMAGE_TAG}
                                    
                                    # Terminate and remove the legacy container instance
                                    docker stop radiology-manager || true
                                    docker rm radiology-manager || true
                                    
                                    # Run the production container on 'radiology-net' network
                                    docker run -d \
                                      --name radiology-manager \
                                      --network radiology-net \
                                      -p 8080:8080 \
                                      -e SPRING_PROFILES_ACTIVE=prod \
                                      -e SPRING_DATASOURCE_URL=jdbc:postgresql://radiology-db:5432/radiologydb \
                                      -e SPRING_DATASOURCE_USERNAME=${SPRING_DATASOURCE_USERNAME} \
                                      -e SPRING_DATASOURCE_PASSWORD=${SPRING_DATASOURCE_PASSWORD} \
                                      --restart unless-stopped \
                                      ${env.REGISTRY}/${env.IMAGE_NAME}:${env.IMAGE_TAG}
                                "
                            """
                        }
                    }
                }
            }
        }

        stage('Deploy to K3s') {
            when {
                expression { env.DEPLOY_TARGET == 'k3s' }
            }
            steps {
                script {
                    withKubeConfig([credentialsId: env.KUBECONFIG_CREDS_ID]) {
                        sh "sed -i 's|IMAGE_PLACEHOLDER|${env.REGISTRY}/${env.IMAGE_NAME}:${env.IMAGE_TAG}|g' k8s/app-deployment.yaml"
                        
                        withCredentials([
                            usernamePassword(credentialsId: env.DATABASE_CREDS_ID, usernameVariable: 'SPRING_DATASOURCE_USERNAME', passwordVariable: 'SPRING_DATASOURCE_PASSWORD')
                        ]) {
                            sh """
                                cp k8s/postgres-secret.yaml k8s/postgres-secret-temp.yaml
                                sed -i 's|\\\${SPRING_DATASOURCE_USERNAME:admin}|'${SPRING_DATASOURCE_USERNAME}'|g' k8s/postgres-secret-temp.yaml
                                sed -i 's|\\\${SPRING_DATASOURCE_PASSWORD:secretpassword}|'${SPRING_DATASOURCE_PASSWORD}'|g' k8s/postgres-secret-temp.yaml
                                sed -i 's|\\\${DB_NAME:radiologydb}|'${env.DB_NAME ?: 'radiologydb'}'|g' k8s/postgres-secret-temp.yaml
                                
                                kubectl apply -f k8s/postgres-secret-temp.yaml
                                rm k8s/postgres-secret-temp.yaml
                            """
                        }

                        sh "kubectl apply -f k8s/postgres-pvc.yaml"
                        sh "kubectl apply -f k8s/postgres-deployment.yaml"
                        sh "kubectl apply -f k8s/postgres-service.yaml"
                        
                        sh "kubectl apply -f k8s/app-configmap.yaml"
                        sh "kubectl apply -f k8s/app-deployment.yaml"
                        sh "kubectl apply -f k8s/app-service.yaml"
                        sh "kubectl apply -f k8s/app-ingress.yaml"
                        
                        sh "kubectl rollout status deployment/radiology-manager"
                    }
                }
            }
        }
    }

    post {
        always {
            sh "docker rmi ${env.REGISTRY}/${env.IMAGE_NAME}:${env.IMAGE_TAG} || true"
        }
    }
}
