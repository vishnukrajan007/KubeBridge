pipeline {
    agent any

    environment {
        IMAGE_NAME = 'vishnukrajan007/flaskapp'
        REGISTRY_CREDENTIALS = credentials('docker-hub-creds') // DockerHub creds ID in Jenkins
        AWS_ACCESS_KEY_ID = credentials('aws-access-key')      // AWS access key
        AWS_SECRET_ACCESS_KEY = credentials('aws-secret-key')  // AWS secret key
        REGION = 'ap-south-1'
        CLUSTER_NAME = 'vkr-cluster'
        SONARQUBE_SERVER = 'sonarqube'                         // Jenkins Sonar server name
        SONAR_TOKEN = credentials('sonar-token')               // SonarQube token credential
    }

    stages {

        stage('Checkout') {
            steps {
                git credentialsId: 'git-creds',
                    url: 'https://github.com/vishnukrajan007/KubeBridge.git',
                    branch: 'main'

                script {
                    IMAGE_TAG = sh(script: "git rev-parse --short HEAD", returnStdout: true).trim()
                }
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv("${SONARQUBE_SERVER}") {
                    sh """
                        sonar-scanner \
                        -Dsonar.projectKey=KubeBridge \
                        -Dsonar.sources=. \
                        -Dsonar.host.url=http://13.235.61.11:9000 \
                        -Dsonar.login=${SONAR_TOKEN}
                    """
                }
            }
        }

        stage('Docker Build & Push') {
            steps {
                script {
                    sh "docker build -t ${IMAGE_NAME}:${IMAGE_TAG} ."

                    withCredentials([usernamePassword(credentialsId: 'docker-hub-creds', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                        sh """
                            echo \$DOCKER_PASS | docker login -u \$DOCKER_USER --password-stdin
                            docker push ${IMAGE_NAME}:${IMAGE_TAG}
                        """
                    }
                }
            }
        }

        stage('Deploy to Kubernetes') {
            steps {
                withCredentials([
                    string(credentialsId: 'aws-access-key', variable: 'AWS_ACCESS_KEY_ID'),
                    string(credentialsId: 'aws-secret-key', variable: 'AWS_SECRET_ACCESS_KEY')
                ]) {
                    script {
                        sh """
                            export AWS_DEFAULT_REGION=${REGION}
                            aws eks update-kubeconfig --name ${CLUSTER_NAME} --region ${REGION}
                            kubectl set image deployment/flaskapp flaskapp=${IMAGE_NAME}:${IMAGE_TAG} -n default
                        """
                    }
                }
            }
        }
    }
}
