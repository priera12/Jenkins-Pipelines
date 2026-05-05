def call (args [:]){

    def appName       = args.appName
    def version       = args.version
    def branch        = args.branch
    def deployEnabled = args.deployEnabled ?: false // Valor por defecto

    if (deployEnabled){

        echo "Depliegue Habilitado"

        build job: "Despliegue/Deploy_${IMAGE_NAME}/main", 
            parameters: [
                string(name: 'APP_NAME', value: appName),
                string(name: 'VERSION', value: version),
                string(name: 'BRANCH', value: branch)
            ], 
            wait: true

    }else {
        echo "Despliegue Omitido por el Usuario"
    }

}