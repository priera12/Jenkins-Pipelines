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
                    volumeMounts:
                      - name: shared-data 
                        mountPath: /shared
                  - name: kaniko
                    image: gcr.io/kaniko-project/executor:debug
                    imagePullPolicy: Always
                    command:
                    - /busybox/cat
                    tty: true
                    volumeMounts:
                      - name: shared-data
                        mountPath: /shared
                  - name: minikube-helper
                      image: alpine:latest
                      command: ['/bin/sh', '-c']
                      args: ['apk add --no-cache curl && curl -LO https://storage.googleapis.com/minikube/releases/latest/minikube-linux-amd64 && install minikube-linux-amd64 /usr/local/bin/minikube && tail -f /dev/null']
                      tty: true
                      volumeMounts:
                        - name: shared-data
                          mountPath: /shared
                        - name: docker-socket
                          mountPath: /var/run/docker.sock
                  volumes:
                  - name: shared-data
                    emptyDir:
                      medium: Memory # Usamos RAM para que sea ultra rápido
                      sizeLimit: "3Gi"
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
                    --tar-path /shared/${params.IMAGE_NAME}_${params.TAG}.tar \
                    --no-push \
                    --skip-tls-verify=true
                    """
                }
                container('minikube-helper') {
                    sh """
                    echo "Cargando imagen a Minikube..."
                    minikube image load /shared/${params.IMAGE_NAME}_${params.TAG}.tar
                    
                    echo "Limpiando..."
                    rm /shared/${params.IMAGE_NAME}_${params.TAG}.tar
                    """
                }
            }
        }
    }
}