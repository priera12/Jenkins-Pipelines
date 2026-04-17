pipeline {
    agent {
        kubernetes{
            yaml """
                apiVersion: v1
                kind: Pod
                metadata:
                  labels:
                    app: kaniko-build
                spec:
                  containers:
                  - name: kaniko
                    image: gcr.io/kaniko-project/executor:debug
                    imagePullPolicy: Always
                    command:
                    - /busybox/cat
                    tty: true
            """
        }
    }
    
    parameters {
        string(name: 'IMAGE_NAME', defaultValue: '', description: 'NOMBRE DEL MICROSERVICIO')
        string(name: 'TAG', defaultValue: '', description: 'VERSION')
    }

    environment {
        // El ID que pusiste en Jenkins al guardar tu credencial
        GITHUB_CREDS = credentials('Jenkins-pipeline')
        IMAGE_NAME = "${params.IMAGE_NAME}"
        TAG = "${params.TAG}"
        // IP del registry interno de Minikube (usualmente localhost:5000 dentro del cluster)
    }
    stages {
        stage('Checkout') {
            steps {
                checkout([$class: 'GitSCM', 
                    branches: [[name: 'main']], // O la rama que necesites
                    userRemoteConfigs: [[
                        url: 'https://github.com/priera12/alumnos_backend.git',
                        credentialsId: 'Jenkins-pipeline' // El ID de tu credencial
                    ]]
                ])
            }
        }
        stage('Build and Push Image') {
            steps {
                container ('kaniko') {
                    sh """
                    /kaniko/executor --context=`pwd` \
                    --dockerfile=Dockerfile \
                    --destination=${params.IMAGE_NAME}:${params.TAG} \
                    --skip-tls-verify=true
                    """
                }
            }
        }
    }
}