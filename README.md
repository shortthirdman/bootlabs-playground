# bootlabs-playground

A modular playground of Spring Boot projects and experiments — crafted for learning, rapid prototyping, and exploration.

---

## 📁 Repository Structure

This mono-repo contains a variety of standalone Spring Boot applications and modules organized for easy discovery and experimentation. Each folder under `projects/` or `modules/` is a self-contained example, demo, or prototype.

---

## 🧪 What's Inside

- ✅ Real-world Spring Boot use cases
- 🧩 Modular examples with common patterns (REST APIs, security, data, messaging, etc.)
- ⚙️ Integration with databases, cloud services, and Spring ecosystem components
- 🧰 Experimental code to test new ideas, libraries, or techniques

---

## 🎯 Goals

- Serve as a reference for Spring Boot features and integrations
- Provide a safe space to experiment with new ideas
- Promote clean architecture, modular design, and best practices

---

## 🚀 Getting Started

Each project contains its own README and can be run independently.

---

### 🛠 Tech Stack

This repository is built around the Spring Boot ecosystem and includes a curated set of tools and technologies to support modular, modern Java development.

| Category              | Tech / Tool                                                                                                      | Description                                                               |
| --------------------- | ---------------------------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------- |
| **Language**          | [Java 17+](https://openjdk.org/)                                                                                 | Modern LTS version for Spring Boot 3.x support.                           |
| **Framework**         | [Spring Boot](https://spring.io/projects/spring-boot)                                                            | Foundation for building standalone, production-grade Spring applications. |
| **Build Tool**        | [Maven](https://maven.apache.org/)                                                                               | Dependency management and build automation.                               |
| **Project Structure** | Modular mono-repo                                                                                                | Each module is a standalone Spring Boot project under a common structure. |
| **IDE**               | [IntelliJ IDEA](https://www.jetbrains.com/idea/) / [VS Code](https://code.visualstudio.com/)                     | Recommended IDEs with Spring support.                                     |
| **Dependency Mgmt**   | [Spring Dependency BOM](https://docs.spring.io/spring-boot/docs/current/reference/html/dependency-versions.html) | Keeps dependencies in sync with Spring ecosystem.                         |
| **Testing**           | [JUnit 5](https://junit.org/junit5/) + [Mockito](https://site.mockito.org/)                                      | For unit and integration testing.                                         |
| **Packaging**         | Executable JARs                                                                                                  | Each module builds into an independent Spring Boot runnable JAR.          |
| **Version Control**   | [Git](https://git-scm.com/) + [GitHub](https://github.com/)                                                      | Standard version control and hosting platform.                            |
| **Java Formatter**    | [Spotless](https://github.com/diffplug/spotless) *(optional)*                                                    | Code formatting and consistency (if configured).                          |
| **CI/CD**             | GitHub Actions *(optional)*                                                                                      | Used for running build/test pipelines (if configured).                    |

> ℹ️ *Some tools like Spotless or GitHub Actions may be used optionally across modules. Check each module's README or `pom.xml` for specific usage.*

---

### 🧑‍💻 Recommended Dev Setup

* ✅ **JDK:** Java 17+ (e.g., OpenJDK 17 or Amazon Corretto)
* ✅ **IDE:** IntelliJ IDEA with Spring and Maven plugins
* ✅ **Terminal:** Bash, Zsh, or PowerShell
* ✅ **Build:** Run `./mvnw clean install` or `./mvnw spring-boot:run` from any module root
* ✅ **Java Format Plugin:** Enable code formatting (Spotless or IDE-native) for consistency

---

### Reference Documentation
For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/3.4.4/maven-plugin)
* [Create an OCI image](https://docs.spring.io/spring-boot/3.4.4/maven-plugin/build-image.html)
* [Spring Boot Testcontainers support](https://docs.spring.io/spring-boot/3.4.10/reference/testing/testcontainers.html#testing.testcontainers)
* [Testcontainers Postgres Module Reference Guide](https://java.testcontainers.org/modules/databases/postgres/)
* [Spring Boot DevTools](https://docs.spring.io/spring-boot/3.4.4/reference/using/devtools.html)
* [Spring Web](https://docs.spring.io/spring-boot/3.4.4/reference/web/servlet.html)
* [Spring Security](https://docs.spring.io/spring-boot/3.4.10/reference/web/spring-security.html)
* [Spring Data JPA](https://docs.spring.io/spring-boot/3.4.10/reference/data/sql.html#data.sql.jpa-and-spring-data)
* [Validation](https://docs.spring.io/spring-boot/3.4.10/reference/io/validation.html)
* [JOOQ Access Layer](https://docs.spring.io/spring-boot/3.4.4/reference/data/sql.html#data.sql.jooq)
* [Liquibase Migration](https://docs.spring.io/spring-boot/3.4.4/how-to/data-initialization.html#howto.data-initialization.migration-tool.liquibase)
* [Spring for Apache Kafka](https://docs.spring.io/spring-boot/3.4.4/reference/messaging/kafka.html)
* [Spring Boot Actuator](https://docs.spring.io/spring-boot/3.4.4/reference/actuator/index.html)
* [Testcontainers](https://java.testcontainers.org/)

---

### Guides
The following guides illustrate how to use some features concretely:

* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
* [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)
* [Building REST services with Spring](https://spring.io/guides/tutorials/rest/)
* [Securing a Web Application](https://spring.io/guides/gs/securing-web/)
* [Spring Boot and OAuth2](https://spring.io/guides/tutorials/spring-boot-oauth2/)
* [Authenticating a User with LDAP](https://spring.io/guides/gs/authenticating-ldap/)
* [Accessing Data with JPA](https://spring.io/guides/gs/accessing-data-jpa/)
* [Validation](https://spring.io/guides/gs/validating-form-input/)
* [Building a RESTful Web Service with Spring Boot Actuator](https://spring.io/guides/gs/actuator-service/)

---

### Testcontainers support

This project uses [Testcontainers at development time](https://docs.spring.io/spring-boot/3.4.10/reference/features/dev-services.html#features.dev-services.testcontainers).

Testcontainers has been configured to use the following Docker images:

* [`postgres:latest`](https://hub.docker.com/_/postgres)

Please review the tags of the used images and set them to the same as you're running in production.

---

### Maven Parent overrides

Due to Maven's design, elements are inherited from the parent POM to the project POM.
While most of the inheritance is fine, it also inherits unwanted elements like `<license>` and `<developers>` from the parent.
To prevent this, the project POM contains empty overrides for these elements.
If you manually switch to a different parent and actually want the inheritance, you need to remove those overrides.

---

## Author

👤 **Swetank Mohanty**

* Website: https://shortthirdman.medium.com/
* Twitter: [@ShortThirdMan93](https://twitter.com/ShortThirdMan93)
* Github: [@shortthirdman](https://github.com/shortthirdman)
* LinkedIn: [@shortthirdman](https://linkedin.com/in/shortthirdman)

---

## 🤝 Contributing

Contributions, issues and feature requests are welcome!<br />Feel free to check [issues page](https://github.com/shortthirdman-org/Google-Colab-Notebooks/issues). 

---

## Show your support

Give a ⭐️ if this project helped you!

<a href="https://www.patreon.com/shortthirdman">
  <img src="https://c5.patreon.com/external/logo/become_a_patron_button@2x.png" width="160">
</a>

---

## 📝 License

Copyright © 2025 [Swetank Mohanty](https://github.com/shortthirdman).<br />
This project is [MIT](LICENSE) licensed.

***
_This README was generated with ❤️ by [readme-md-generator](https://github.com/kefranabg/readme-md-generator)_