pipeline {
    agent any

    environment {
        DOCKER_HUB_CREDENTIALS = credentials('docker-hub-creds')  // Jenkins creds ID for Docker Hub
        AWS_CREDENTIALS = credentials('aws-credentials')          // Jenkins creds ID for AWS (access key + secret)
        DOCKER_IMAGE = "vishnukrajan007/kubebridge"
        IMAGE_TAG = "${env.BUILD_NUMBER}"
        KUBE_NAMESPACE = "default"
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/VishnuKRajan007/KubeBridge.git'
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Docker Build & Push') {
            steps {
                script {
                    sh "docker build -t ${DOCKER_IMAGE}:${IMAGE_TAG} ."
                    sh "echo $DOCKER_HUB_CREDENTIALS_PSW | docker login -u $DOCKER_HUB_CREDENTIALS_USR --password-stdin"
                    sh "docker push ${DOCKER_IMAGE}:${IMAGE_TAG}"
                }
            }
        }

        stage('Deploy to Kubernetes') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'aws-credentials', usernameVariable: 'AWS_ACCESS_KEY_ID', passwordVariable: 'AWS_SECRET_ACCESS_KEY')]) {
                    sh '''
                    export AWS_ACCESS_KEY_ID=${AWS_ACCESS_KEY_ID}
                    export AWS_SECRET_ACCESS_KEY=${AWS_SECRET_ACCESS_KEY}
                    export AWS_DEFAULT_REGION=ap-south-1

                    # Update kubeconfig for EKS
                    aws eks update-kubeconfig --name vkr-cluster --region ap-south-1

                    # Deploy by setting new image
                    kubectl set image deployment/kubebridge kubebridge=${DOCKER_IMAGE}:${IMAGE_TAG} -n ${KUBE_NAMESPACE}
                    '''
                }
            }
        }
    }

    post {
        failure {
            echo 'Build or deployment failed!'
        }
    }
}

