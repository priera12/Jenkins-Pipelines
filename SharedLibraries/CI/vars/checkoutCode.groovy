def call(Map args = [:]) {

    def repoName = args.repoName
    def branchName = args.branchName ?: 'main'
    def credencials = args.credencialsId ?: 'Jenkins-pipeline'
    def baseUrl = args.baseUrl ?: "https://github.com/priera12"

    echo "Iniciando Checkout del repositorio: ${repoName} (Rama: ${branchName})"

    checkout([$class: 'GitSCM', 
        branches: [[name: branchName]], 
        userRemoteConfigs: [[
            url: "${baseUrl}/${repoName}.git",
            credentialsId: credentials
        ]]
    ])    

}