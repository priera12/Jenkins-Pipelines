def call(Map config = [:]){

    pipeline{
        agent{
            label 'kaniko-builder'
        }

        parameters {
            string(name: 'IMAGE_NAME', defaultValue: '', description: 'NOMBRE DEL MICROSERVICIO')
            string(name: 'TAG', defaultValue: '', description: 'VERSION')
            string(name: 'BRANCH', defaultValue: 'main', description: 'BRANCH')
            booleanParam(name: 'DEPLOY_ENABLED', defaultValue: false, description: '¿Deseas realizar el despliegue al cluster?')
        }

        environment{
            IMAGE_DESTINATION = "docker.io/pabloxr12/pxr207-${IMAGE_NAME}:${TAG}"
        }

        stages {
            stage('Checkout'){
                steps{
                    checkoutCode(
                        deployEnabled: params.DEPLOY_ENABLED
                        appName: params.IMAGE_NAME
                        version: params.TAG
                        branch: params.BRANCH
                    )
                }
            }
            stage('Build Image'){
                steps{
                    
                    kanikoBuilder(
                        env.IMAGE_DESTINATION
                    )
                }
            }
            stage('Deploy to Minikube'){
                steps{
                    triggerDeploy(
                        appName = params.IMAGE_NAME
                        version = params.TAG
                        branch  = params.BRANCH
                        deployEnabled = params.DEPLOY_ENABLED
                    )
                }
            }
        }
    }
}