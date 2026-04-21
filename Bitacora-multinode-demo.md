Apr 19 20:07:51 multinode-demo-m02 kubelet[1716]: I0419 20:07:51.962464    1716 container_manager_linux.go:275] "Creating Container Manager object based on Node Config" nodeConfig={"NodeName":"multinode-demo-m02","RuntimeCgroupsName":"","SystemCgroupsName":"","KubeletCgroupsName":"","KubeletOOMScoreAdj":-999,"ContainerRuntime":"","CgroupsPerQOS":true,"CgroupRoot":"/","CgroupDriver":"systemd","KubeletRootDir":"/var/lib/kubelet","ProtectKernelDefaults":false,"KubeReservedCgroupName":"","SystemReservedCgroupName":"","ReservedSystemCPUs":{},"EnforceNodeAllocatable":{"pods":{}},"KubeReserved":null,"SystemReserved":null,"HardEvictionThresholds":[],"QOSReserved":{},"CPUManagerPolicy":"none","CPUManagerPolicyOptions":null,"TopologyManagerScope":"container","CPUManagerReconcilePeriod":10000000000,"MemoryManagerPolicy":"None","MemoryManagerReservedMemory":null,"PodPidsLimit":-1,"EnforceCPULimits":true,"CPUCFSQuotaPeriod":100000000,"TopologyManagerPolicy":"none","TopologyManagerPolicyOptions":null,"CgroupVersion":2}



903a74205025a5230ea7e999dfbc64203063ce48ddcaff66e9000f6820528c7b



minikube start --nodes 3 --driver=docker \
  --cpus=4 --memory=8192 \
  --extra-config=kubelet.system-reserved=cpu=500m,memory=512Mi \
  --extra-config=kubelet.kube-reserved=cpu=500m,memory=512Mi \
  -p multinode-demo


Esta es la bitácora técnica del incidente de estabilidad del clúster Minikube. Puedes copiar y guardar esto en tu sistema de seguimiento de incidencias o documentación técnica (Post-mortem).

---

# Bitácora de Incidente: Inestabilidad de Nodo en Minikube
**Fecha:** 20 de Abril, 2026
**Componentes Afectados:** Nodo `multinode-demo` (Minikube), Agente Jenkins, Runtime de Kubernetes.

### 1. Descripción del Problema
Los builds de Jenkins en el clúster fallaban de forma intermitente con errores de sincronización y terminación de contenedores. El síntoma principal reportado por el `kubelet` era `"Housekeeping took longer than expected"`.

### 2. Síntomas Identificados
| Síntoma | Diagnóstico |
| :--- | :--- |
| `KubeletOOMScoreAdj: -999` | Falso positivo (inicio normal del kubelet). |
| `container not running` | Error en Jenkins al intentar limpiar un contenedor que el Kernel había matado o bloqueado. |
| `Housekeeping took longer...` | Bloqueo crítico del bucle de control del Kubelet debido a falta de recursos del sistema. |
| Procesos `<defunct>` (zombie) | Procesos hijos (cp/tar) bloqueados por falta de respuesta del Kernel (I/O Wait). |

### 3. RCA (Root Cause Analysis)
El análisis confirmó que no existía un error de lógica en el `Jenkinsfile` ni en las imágenes, sino una **saturación de I/O en el disco (Disk I/O Saturation)**.
* Durante la ejecución de pasos pesados (`tar`, `cp`, construcción de capas de Docker), el disco alcanzaba un **70% de utilización**.
* El Kernel de Linux, al estar ocupado gestionando estas operaciones de escritura/lectura masivas, dejaba en espera al proceso `kubelet`.
* Al exceder el tiempo de gracia de 1 segundo para el "Housekeeping", Kubernetes marcaba el error, reiniciaba el agente o lo consideraba como fallido.



### 4. Acciones Correctivas Aplicadas
Para resolver la contención de recursos, se procedió con una reconfiguración de nivel SRE:

* **Aislamiento de Recursos (Resource Reservation):** Se configuraron límites reservados para el sistema operativo, evitando que los Pods de usuario "ahoguen" al Kubelet.
    * Flag aplicado: `--extra-config=kubelet.system-reserved=cpu=500m,memory=512Mi`
* **Optimización de I/O (Workload Shifting):** Se migraron las operaciones de escritura pesadas (Workspaces de Jenkins) de disco físico a memoria RAM para eliminar la latencia de I/O.
    * Configuración: `emptyDir` con `medium: Memory` en el `PodTemplate` de Jenkins.
* **Ajuste de Infraestructura:** Ajuste de los recursos de la VM de Minikube (Driver Docker) para garantizar suficiente holgura de CPU/RAM.

### 5. Estado Final
El clúster ha sido re-provisionado con los flags de aislamiento y los pipelines de Jenkins han sido modificados para utilizar volúmenes en memoria. La saturación de I/O ha sido mitigada y el error de *Housekeeping* ha desaparecido, logrando la estabilidad del nodo bajo carga.