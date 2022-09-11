# Spring Boot + Spring Cloud Microservices
### Project Setup

This source code is based on Maven multi module project and it contains primary services such as Configuration, Service Registry and Gateway Service which forms the core of Microservices in addition to it there are few additional auxiliary services which can be used to get complete microservices communication.

Like Spring Boot has their own parent POM, this project also utilizes same concept and has `gm-parent-pom` as parent of all projects

| Name | Description |
| ----------- | ----------- |
| configuration-svc | Service for hosting configuration data of services which are spring-boot-config-client |
| service-registry-svc | Service for holding all registered Eureka client or discovery client information |
| gateway-svc | Spring Cloud Gateway providing single point to communicate with all registered services |
|greet-svc | Greeting service exposing two test APIs |
<br/>
### Running Services
#### configuration-svc
#### service-registry-svc
#### gateway-svc
#### greet-svc
<br/>
## References
- [Maven Multi-Module Project](https://books.sonatype.com/mvnex-book/reference/multimodule.html)
- [Maven Multi-Module Enterprise Project](https://books.sonatype.com/mvnex-book/reference/multimodule-web-spring.html)