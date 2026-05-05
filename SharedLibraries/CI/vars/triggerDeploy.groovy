def call (args = [:]){

    def appName       = args.appName
    def version       = args.version
    def branch        = args.branch
    def deployEnabled = args.deployEnabled ?: false // Valor por defecto

    if (deployEnabled){

        echo "Despliegue Habilitado"

        build job: "Despliegue/Deploy_${args.appName}/main", 
            parameters: [
                string(name: 'APP_NAME', value: "${args.appName}"),
                string(name: 'VERSION', value: "${args.version}"),
                string(name: 'BRANCH', value: "${args.branch}")
            ], 
            wait: true

    }else {
        echo "Despliegue Omitido por el Usuario"
    }

}