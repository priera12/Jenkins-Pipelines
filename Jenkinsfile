pipeline {
    agent any
    
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
                sh 'git --version'
                sh 'printenv'
                checkout([$class: 'GitSCM', 
                    branches: [[name: 'main']], // O la rama que necesites
                    extensions: [[$class: 'GitOption', remotePolling: false, gitTool: 'Default']],
                    userRemoteConfigs: [[
                        url: 'git@github.com:priera12/alumnos_backend.git',
                        credentialsId: 'Jenkins-pipeline' // El ID de tu credencial
                    ]]
                ])
            }
        }
        
        stage('Build and Push Image') {
            steps {
                script {
                    // Ejemplo: Usar el token generado por la GitHub App
                    // para hacer un curl a la API de GitHub
                    container('kaniko') {
                    sh '''
                    /kaniko/executor --context=`pwd`
                    --dockerfile=Dockerfile
                    --destination=${IMAGE_NAME}:${TAG}
                    --no-push=false
                    '''
                    }
                }
            }
        }
    }
}