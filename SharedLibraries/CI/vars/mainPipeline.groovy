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
            TAG = "${params.TAG}${env.BUILD_NUMBER}"
            IMAGE_DESTINATION = "docker.io/pabloxr12/pxr207-${IMAGE_NAME}:${env.TAG}"
        }

        stages {
//            stage('Checkout'){
//                steps{
//                    checkoutCode(
//                        appName: params.IMAGE_NAME,
//                        branch: params.BRANCH
//                    )
//                }
//            }
            stage('Build Image'){
                steps{
                    step{
                        def destination = "${env.IMAGE_DESTINATION}"
                        kanikoBuilder(destination)
                    }
                }
            }
            stage('Deploy to Minikube'){
                steps{
                    triggerDeploy(
                        appName = params.IMAGE_NAME,
                        version = env.TAG,
                        branch  = params.BRANCH,
                        deployEnabled = params.DEPLOY_ENABLED
                    )
                }
            }
        }
    }
}