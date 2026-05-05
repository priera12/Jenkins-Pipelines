def call (Map args [:]){

    container('kaniko') {
        sh """
        /kaniko/executor --context=`pwd` \
        --dockerfile=Dockerfile \
        --destination=${args.destination} \
        --cache=true
        """
    }
}