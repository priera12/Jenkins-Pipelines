pipeline {
    
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
                        url: "https://github.com/priera12/${IMAGE_NAME}.git",
                        credentialsId: 'Jenkins-pipeline' // El ID de tu credencial
                    ]]
                ])
            }
        }
        stage('Build Image') {
            steps {
                    sh """
                    docker image build -t ${params.IMAGE_NAME}:${params.TAG} .
                    minikube image load ${params.IMAGE_NAME}:${params.TAG} -p multinode-demo
                    """
            }
        }
        //stage('Copy Image to Node'){
        //    steps{
        //            container('kubectl'){
        //                script{
        //                def podName = sh(script: 'hostname', returnStdout: true).trim()
        //                echo "El nombre del pod es: ${podName}"
        //                sh "kubectl cp ${podName}:/tmp/${params.IMAGE_NAME}_${params.TAG}.tar ./${params.IMAGE_NAME}_${params.TAG}.tar"
        //                sh "minikube image load ${params.IMAGE_NAME}_${params.TAG}.tar -p multinode-demo"
        //                }
        //            }
        //    }
        //}
    }
}