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
                  - name: kubectl
                    image: bitnami/kubectl:latest
                    command: ['cat']
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
        stage('Build TAR') {
            steps {
                container ('kaniko') {
                    sh """
                    /kaniko/executor --context=`pwd` \
                    --dockerfile=Dockerfile \
                    --tar-path /tmp/${params.IMAGE_NAME}_${params.TAG}.tar \
                    --no-push \
                    --skip-tls-verify=true
                    """
                }
            }
        }
        stage('Copy Image to Node'){
            steps{
                    container('kubectl'){
                        script{
                        def podName = sh(script: 'hostname', returnStdout: true).trim()
                        echo "El nombre del pod es: ${podName}"
                        sh "kubectl cp ${podName}:/tmp/${params.IMAGE_NAME}_${params.TAG}.tar ./${params.IMAGE_NAME}_${params.TAG}.tar"
                        sh "minikube image load ${params.IMAGE_NAME}_${params.TAG}.tar -p multinode-demo"
                        }
                    }
            }
        }
    }
}