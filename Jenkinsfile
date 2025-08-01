pipeline {
    agent any

    environment {
        DOCKER_IMAGE = 'vishnukrajan007/flaskapp'
        KUBE_NAMESPACE = 'default'
    }

    stages {
        stage('Checkout') {
            steps {
                git(
                    url: 'https://github.com/vishnukrajan007/KubeBridge.git',
                    credentialsId: 'git-creds',
                    branch: 'main'
                )
                script {
                    IMAGE_TAG = sh(script: 'git rev-parse --short HEAD', returnStdout: true).trim()
                    env.IMAGE_TAG = IMAGE_TAG
                }
            }
        }

        stage('Docker Build & Push') {
            steps {
                script {
                    sh "docker build -t ${DOCKER_IMAGE}:${IMAGE_TAG} ."
                    withCredentials([usernamePassword(
                        credentialsId: 'docker-hub-creds',
                        usernameVariable: 'DOCKER_HUB_CREDENTIALS_USR',
                        passwordVariable: 'DOCKER_HUB_CREDENTIALS_PSW'
                    )]) {
                        sh "echo $DOCKER_HUB_CREDENTIALS_PSW | docker login -u $DOCKER_HUB_CREDENTIALS_USR --password-stdin"
                        sh "docker push ${DOCKER_IMAGE}:${IMAGE_TAG}"
                    }
                }
            }
        }

        stage('Deploy to Kubernetes') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'aws-credentials',
                    usernameVariable: 'AWS_ACCESS_KEY_ID',
                    passwordVariable: 'AWS_SECRET_ACCESS_KEY'
                )]) {
                    sh '''
                        export AWS_ACCESS_KEY_ID=${AWS_ACCESS_KEY_ID}
                        export AWS_SECRET_ACCESS_KEY=${AWS_SECRET_ACCESS_KEY}
                        export AWS_DEFAULT_REGION=ap-south-1

                        aws eks update-kubeconfig --name vkr-cluster --region ap-south-1

                        kubectl set image deployment/flaskapp flaskapp=${DOCKER_IMAGE}:${IMAGE_TAG} -n ${KUBE_NAMESPACE}
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
