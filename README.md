# Jenkins-Pipelines
Jenkins-Pipelines


kubectl create secret docker-registry docker-hub-creds \
  --docker-server=https://index.docker.io/v1/ \
  --docker-username=pabloxr12 \
  --docker-password=pabloxr12 \
  --docker-email=rierapablo12@gmail.com


apiVersion: v1
kind: Pod
metadata:
  labels:
    label: kaniko-builder
    name: kaniko-builder
spec:
  containers:
  - name: jnlp
    image: jenkins/inbound-agent:latest
    # El JNLP necesita el workspace para reportar el estado al master
    volumeMounts:
    - name: workspace-volume
      mountPath: /home/jenkins/workspace 
  - name: kaniko
    image: gcr.io/kaniko-project/executor:debug
    command: ["sleep"]
    args: ["9999"]
    volumeMounts:
    - name: workspace-volume
      mountPath: /home/jenkins/workspace
    - name: docker-config
      mountPath: /kaniko/.docker
  volumes:
  - name: workspace-volume
    emptyDir: {}
  - name: docker-config
    secret:
      secretName: docker-hub-creds
      items:
      - key: .dockerconfigjson
        path: config.json