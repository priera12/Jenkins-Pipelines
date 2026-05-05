def call(Map args = [:]) {

    def appName = args.IMAGE_NAME
    def branchName = args.branchName ?: 'main'
    def credencials = 'Jenkins-pipeline'
    def baseUrl = args.baseUrl ?: "https://github.com/priera12"

    echo "Iniciando Checkout del repositorio: ${baseUrl} (Rama: ${branchName})"

    checkout([$class: 'GitSCM', 
        branches: [[name: branchName]], 
        userRemoteConfigs: [[
            url: "${baseUrl}/${appName}.git",
            credentialsId: credentials
        ]]
    ])    

}