pipeline {
    agent { label 'kaniko-builder' }
    
    parameters {
        string(name: 'IMAGE_NAME', defaultValue: '', description: 'NOMBRE DEL MICROSERVICIO')
        string(name: 'TAG', defaultValue: '', description: 'VERSION')
        string(name: 'BRANCH', defaultValue: '', description: 'BRANCH')
    }

    environment {
        // El ID que pusiste en Jenkins al guardar tu credencial
        BRANCH_NAME = "${params.BRANCH}"
        GITHUB_CREDS = credentials('Jenkins-pipeline')
        IMAGE_NAME = "${params.IMAGE_NAME}"
        TAG = "${params.TAG}"
        IMAGE_DESTINATION = "docker.io/pabloxr12/pxr207-${IMAGE_NAME}:${TAG}"
        // IP del registry interno de Minikube (usualmente localhost:5000 dentro del cluster)
    }
    stages {
        stage('Checkout') {
            steps {
                checkout([$class: 'GitSCM', 
                    branches: [[name: "${BRANCH}"]], // O la rama que necesites
                    userRemoteConfigs: [[
                        url: "https://github.com/priera12/${IMAGE_NAME}.git",
                        credentialsId: 'Jenkins-pipeline' // El ID de tu credencial
                    ]]
                ])
            }
        }
        stage('Build TAR') {
            steps {
                container ('kaniko') {
                    sh """
                    echo "${IMAGE_DESTINATION}"

                    /kaniko/executor --context=`pwd` \
                    --dockerfile=Dockerfile \
                    --destination=${IMAGE_DESTINATION} \
                    --cache=true
                    """
                }
            }
        }
    }
}