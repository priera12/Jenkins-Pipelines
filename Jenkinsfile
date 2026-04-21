pipeline {
    agent {label 'docker-builder'}
    
    parameters {
        string(name: 'APP_NAME', defaultValue: '', description: 'NOMBRE DEL MICROSERVICIO')
        string(name: 'TAG', defaultValue: '', description: 'VERSION')
    }

    environment {
        // El ID que pusiste en Jenkins al guardar tu credencial
        GITHUB_CREDS = credentials('Jenkins-pipeline')
        APP_NAME = "${params.APP_NAME}"
        TAG = "${params.TAG}"
        REGISTRY = "registry.kube-system.svc.cluster.local:5000"
        IMAGE_NAME = "${REGISTRY}/${APP_NAME}:${TAG}"
        // IP del registry interno de Minikube (usualmente localhost:5000 dentro del cluster)
    }
    stages {
        stage('Checkout') {
            steps {
                checkout([$class: 'GitSCM', 
                    branches: [[name: 'main']], // O la rama que necesites
                    userRemoteConfigs: [[
                        url: "https://github.com/priera12/${APP_NAME}.git",
                        credentialsId: 'Jenkins-pipeline' // El ID de tu credencial
                    ]]
                ])
            }
        }
        stage('Build & Push') {
            steps {
                script {
                        echo "Construyendo imagen para el registro interno..."
                        sh "docker build -t ${IMAGE_NAME} ."
                        echo "Haciendo push al registro de Minikube..."
                        sh "docker push ${IMAGE_NAME}"
                }
            }
        }
    }
}