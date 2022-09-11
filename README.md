# Spring Boot + Spring Cloud Microservices
## Project Setup

This source code is based on Maven multi module project and it contains primary services such as Configuration, Service Registry and Gateway Service which forms the core of Microservices in addition to it there are few additional auxiliary services which can be used to get complete microservices communication.


Like Spring Boot has their own parent POM, this project also utilizes same concept and has `gm-parent-pom` as parent of all projects



| Name | Description |
| ----------- | ----------- |
| configuration-svc | Service for hosting configuration data of services which are spring-boot-config-client |
| service-registry-svc | Service for holding all registered Eureka client or discovery client information |
| gateway-svc | Spring Cloud Gateway providing single point to communicate with all registered services |
|greet-svc | Greeting service exposing two test APIs |



*Above all services run on HTTPS using self-signed certificate available under* `resources/cert` *folder, kindly make sure you configure trust-store* *as JVM argument or* *as part of OS to avoid getting* `sun.security.provider.certpath.SunCertPathBuilderException` *or* `sun.security.validator.ValidatorException` *certificate errors as specified in Running Services section.*



## Running Services
### Core Services
---
### **Application Configuration Service / configuration-svc**
No special configuration is needed to run the application. You can run application as standalone java application or if you are using Spring Tools Suite you can run application as Spring Boot application.
### **Eureka Service Registry and Discovery Service / service-registry-svc**

For running service-registry-svc we need to specify trust-store and trust-store password as a VM arguments containing self-signed CA certificate or else we need to update OS to install our self-signed CA certificate so that it can communicate with the `configuration-svc` service to fetch configuration properties.


Kindly, update trust-store file path as absolute path where the repository is cloned and use trust-store available in any of the project.


`-Djavax.net.ssl.trustStore=./truststore.jks -Djavax.net.ssl.trustStorePassword=localhost`


After specifying above argument you can run application as standalone java application or Spring Boot application.
### **API Gateway Application / gateway-svc**
Please update trust-store for application as suggested in above `service-registry-svc` application and you should be able to run application either standalone java application or Spring Boot application.
### Auxiliary Services
---
#### **greet-svc**
Kindly, update trust-store file path as absolute path where the repository is cloned and use trust-store available in any of the project.


`-Djavax.net.ssl.trustStore=./truststore.jks -Djavax.net.ssl.trustStorePassword=localhost`


After specifying above argument you can run application as standalone java application or Spring Boot application.

## References
- [Maven Multi-Module Project](https://books.sonatype.com/mvnex-book/reference/multimodule.html)
- [Maven Multi-Module Enterprise Project](https://books.sonatype.com/mvnex-book/reference/multimodule-web-spring.html)
