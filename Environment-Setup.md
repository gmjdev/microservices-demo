# Environment Setup
This documentation will guide you to set up your local environment for running the application using Docker and Kubernetes. 

### Prerequisite Software/Tools

#### Docker Desktop / Rancher Desktop
We will run our external services as part of docker container, so it is mandatory to have docker tool running on 
local machine. For installation you can refer [Docker Desktop Install](https://docs.docker.com/desktop/)
/ [Rancher Desktop Install](https://docs.rancherdesktop.io/getting-started/installation)

#### K3d
K3d will help us to setup our local Kubernetes Cluster, it is lightweight minimal kubernetes distribution to perform 
local development on Kubernetes Cluster. Refer [K3d](https://k3d.io/v5.6.0/) website for more information.

#### Skaffold
Refer [Installation](https://skaffold.dev/docs/install/)

#### Private Local Docker Registry
We are going to setup local private repository for uploading our docker images, you may use docker hub/GCR/ECR 
but to keep things simple we will go with local repository. For running local private repository refer 
[Running local docker registry](#running-local-docker-registry)

#### Dive
To verify contents of container image build by Jib plugin we can use dive tool you can read about it more 
at https://github.com/wagoodman/dive.

### Running local docker registry

1. Execute below command

    `docker run -d -p 5000:5000 --restart=always --name registry registry:2`
2. Verify the docker registry is running

    `docker ps`

3. You should see below output on successfully running of registry

    ![docker_ps_registry.png](readme-resources%2Fdocker_ps_registry.png)

4. Verify Container Images build by Jib

   `dive <docker-image-id>`

### Create Custom Docker network for communication

`docker network create -d bridge apps-bridge-netwrk`

### Create K8s local cluster

Execute below command from root folder of projects

`k3d cluster create -c k8s/k3d-cluster.yml `