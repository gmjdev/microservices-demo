# Environment Setup
This documentation will help you to set up your local environment for running the application. As part of this document 
we will configure setting up below application/tools.

### Prerequisite Software/Tools

1. Docker Desktop / Rancher Desktop

   We will run our external services as part of docker container, so it is mandatory to have docker on running on 
local machine. For installation refer [Docker Desktop Install](https://docs.docker.com/desktop/)
/ [Rancher Desktop Install](https://docs.rancherdesktop.io/getting-started/installation)

2. Skaffold

   Refer [Installation](https://skaffold.dev/docs/install/)

3. Minikube/K3d
   
   Helps in setting up local Kubernetes cluster.

4. Private Local Docker Registry

   We are going to setup local private repository for uploading our docker images, you may use docker hub/GCR/ECR 
but for our demo purpose we will go with local repository. For running local private repository refer 
[Running local docker registry](#running-local-docker-registry)

5. Dive
   
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
