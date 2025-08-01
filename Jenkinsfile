pipeline {
    agent any

    environment {
        DOCKER_IMAGE = 'vishnukrajan007/flaskapp'
        IMAGE_TAG = 'latest'
        KUBE_NAMESPACE = 'default'
    }

    stages {
        stage('Checkout') {
            steps {
                git(
                    url: 'https://github.com/vishnukrajan007/KubeBridge.git',
                    credentialsId: 'git-creds',
                    branch: 'main' // or 'master' if that's your default branch
                )
            }
        }

        stage('Docker Build & Push') {
            steps {
                script {
                    sh "docker build -t ${DOCKER_IMAGE}:${IMAGE_TAG} ."
                    withCredentials([usernamePassword(credentialsId: 'docker-hub-creds', usernameVariable: 'DOCKER_HUB_USERNAME', passwordVariable: 'DOCKER_HUB_PASSWORD')]) {
                        sh "echo $DOCKER_HUB_PASSWORD | docker login -u $DOCKER_HUB_USERNAME --password-stdin"
                        sh "docker push ${DOCKER_IMAGE}:${IMAGE_TAG}"
                    }
                }
            }
        }

        stage('Deploy to Kubernetes') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'aws-credentials', usernameVariable: 'AWS_ACCESS_KEY_ID', passwordVariable: 'AWS_SECRET_ACCESS_KEY')]) {
                    script {
                        sh '''
                        export AWS_ACCESS_KEY_ID=${AWS_ACCESS_KEY_ID}
                        export AWS_SECRET_ACCESS_KEY=${AWS_SECRET_ACCESS_KEY}
                        export AWS_DEFAULT_REGION=ap-south-1

                        # Update kubeconfig
                        aws eks update-kubeconfig --name vkr-cluster --region ap-south-1

                        # Update deployment with new image
                        kubectl set image deployment/flaskapp flaskapp=${DOCKER_IMAGE}:${IMAGE_TAG} -n ${KUBE_NAMESPACE}
                        '''
                    }
                }
            }
        }
    }

    post {
        failure {
            echo '❌ Build or deployment failed!'
        }
        success {
            echo '✅ Build and deployment successful!'
        }
    }
}
