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
                  - name: jnlp
                    resources:
                      limits:
                        memory: "2Gi"  # Aumenta esto. Los builds de JS son hambrientos.
                        cpu: "1000m"
                        ephemeral-storage: "8Gi"
                      requests:
                        memory: "1Gi"
                        cpu: "500m"
                        ephemeral-storage: "8Gi"
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
                        url: "https://github.com/priera12/${IMAGE_NAME}.git",
                        credentialsId: 'Jenkins-pipeline' // El ID de tu credencial
                    ]]
                ])
            }
        }
        stage('Build & Load') {
            steps {
                container('kaniko'){
                    script {
                        // 1. Kaniko escribe el tar en la RAM (/dev/shm)
                        sh """
                        /kaniko/executor --context=`pwd` \
                            --dockerfile=Dockerfile \
                            --tar-path /dev/shm/image.tar \
                            --no-push \
                            --skip-tls-verify=true
                        """

                        // 2. Minikube carga el tar desde la RAM
                        sh "minikube image load /dev/shm/image.tar"

                        // 3. Limpieza inmediata (opcional, pero buena práctica)
                        sh "rm /dev/shm/image.tar"
                    }
                }
            }
        }

    }
}