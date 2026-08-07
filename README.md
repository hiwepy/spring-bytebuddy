# spring-bytebuddy

[English](./README.md) | [简体中文](./README.zh-CN.md)

[![Java](https://img.shields.io/badge/Java-8-orange)](https://github.com/easy-4-java/spring-bytebuddy) [![License](https://img.shields.io/badge/license-Apache%202.0-green)](./LICENSE)

spring-bytebuddy generates Spring MVC controllers at runtime with ByteBuddy

## Table of Contents

- [1. Project Overview](#1-project-overview)
- [2. Features & Status](#2-features--status)
- [3. Requirements & Compatibility](#3-requirements--compatibility)
- [4. Architecture & Modules](#4-architecture--modules)
- [5. Installation](#5-installation)
- [6. Quick Start](#6-quick-start)
- [7. Configuration](#7-configuration)
- [8. Core Usage / API](#8-core-usage--api)
- [9. Testing & Build](#9-testing--build)
- [10. Versioning & Branches](#10-versioning--branches)
- [11. Contributing & License](#11-contributing--license)

## 1. Project Overview

`spring-bytebuddy` generates Spring MVC controllers at runtime with ByteBuddy. Instead of writing a `@Controller` / `@RestController` class by hand, you describe the endpoint with a fluent builder (`EndpointApiBuilder`) — class annotations (`@Controller`, `@RestController`, Swagger `@Api`), request mappings, methods, parameters and invocation handlers — and the module produces a loadable class wired for Spring MVC (servlet) or Spring WebFlux (reactive).

It is for dynamic/plugin-style applications that must expose new HTTP endpoints without recompiling — it is not a code generator tool and not a replacement for writing regular Spring controllers.

Typical scenarios:

| Scenario | What this module contributes |
|:---|:---|
| Runtime-generated `@RestController` | `EndpointApiBuilder` (annotations, mappings, methods, parameters) |
| Reactive handler endpoints | `ReactiveHandlerBuilder` / `ReactiveHandler` |
| Swagger annotations on generated classes | `SwaggerAnnotationUtils` |
| Proxy or delegation implementations | `EndpointApiBuilder#proxy(...)` / `#delegate(...)` |
| Reusable mapping/param models | `MvcMapping`, `MvcMethod`, `MvcParam`, `MvcParamFrom`, `MvcBound` |

## 2. Features & Status

Project status: pre-release development line (`1.0.x.*` snapshots); public API is still stabilizing until the first tagged release.

| Capability | Status | Notes |
|:---|:---|:---|
| Fluent controller builder | Stable | `EndpointApiBuilder<T extends EndpointApi>` chains `api`, `controller`, `restController`, `requestMapping`, `autowired`, `newMethod` |
| Runtime annotation generation | Stable | `@Controller`, `@RestController`, `@RequestMapping` family, `@Autowired`, Swagger `@Api` / `@ApiIgnore` |
| Method + parameter generation | Stable | `newMethod(name, path, method, consumes, bound, params)` with `MvcParam` annotations (`@PathVariable`, `@RequestParam`, ... via `MvcParamFrom`) |
| Implementation strategies | Stable | `proxy(InvocationHandler)` and `delegate(Class)` for generated method bodies |
| ByteBuddy interop | Stable | `then()` exposes the underlying `DynamicType.Builder` for advanced customization |
| Reactive support | Stable | `ReactiveHandlerBuilder` / `ReactiveHandler` for WebFlux-style handlers |
| Annotation utilities | Stable | `EndpointApiAnnotationUtils`, `SwaggerAnnotationUtils` |

## 3. Requirements & Compatibility

| Requirement | Version |
|:---|:---|
| JDK | 8+ |
| Maven | 3.6+ |
| ByteBuddy | declared in the POM |
| Spring Framework | 5.3.x (spring-webmvc, spring-webflux, spring-context) |
| Swagger annotations | springfox-core + swagger-annotations |
| Servlet / validation APIs | jakarta.servlet-api, jakarta.validation-api (declared in the POM) |

Version lines:

| Branch | JDK | Version pattern | Notes |
|:---|:---|:---|:---|
| `feature/1.0.x` | 8 | `1.0.x.*` | Current line; Spring 5.x era |
| `feature/2.0.x` | 17 | `2.0.x.*` | Next line |
| `feature/3.0.x` | 21 | `3.0.x.*` | Future line |

## 4. Architecture & Modules

```
Endpoint description (fluent DSL)
        |
        v
EndpointApiBuilder (ByteBuddy subclass)
  controller / requestMapping / newMethod / proxy
        |
        v
DynamicType.Builder -> make() -> load(ClassLoader)
        |
        v
Generated Spring MVC Controller class
  (@Controller, @RequestMapping, Swagger @Api)
```

The project is a single jar module. Packages under `org.springframework.bytebuddy`:

| Package | Responsibility |
|:---|:---|
| `bytecode` | `EndpointApi`, `EndpointApiBuilder`, `ReactiveHandler`, `ReactiveHandlerBuilder` |
| `bytecode.definition` | `MvcBound`, `MvcMapping`, `MvcMethod`, `MvcParam`, `MvcParamFrom` |
| `utils` | `EndpointApiAnnotationUtils`, `SwaggerAnnotationUtils` |
| `annotation` | `WebBound` |

## 5. Installation

Artifacts are published to the easy4j private repository and GitHub Releases; the project is not yet on Maven Central.

Maven:

```xml
<dependency>
    <groupId>io.github.easy4j</groupId>
    <artifactId>spring-bytebuddy</artifactId>
    <version>1.0.x.20260630-SNAPSHOT</version>
</dependency>
```

Gradle:

```groovy
implementation 'io.github.easy4j:spring-bytebuddy:1.0.x.20260630-SNAPSHOT'
```

## 6. Quick Start

Generate a `@RestController` at runtime and load it:

```java
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import org.springframework.bytebuddy.bytecode.EndpointApi;
import org.springframework.bytebuddy.bytecode.EndpointApiBuilder;
import org.springframework.web.bind.annotation.RequestMethod;

Class<?> controllerClass = new EndpointApiBuilder<EndpointApi>("com.example.DemoApi")
        .restController()
        .api("Demo API", "demo")                 // Swagger @Api
        .requestMapping("/api/demo")             // class-level mapping
        .newMethod("sayHello", "/say", RequestMethod.GET,
                "application/json", null)        // method-level mapping
        .then()                                  // underlying ByteBuddy builder
        .make()
        .load(getClass().getClassLoader(), ClassLoadingStrategy.Default.INJECTION)
        .getLoaded();

System.out.println(controllerClass.getName());   // com.example.DemoApi
```

Expected result: a class named `com.example.DemoApi` is generated, annotated as a `@RestController` with the given mappings and Swagger metadata, and is loadable by the class loader — ready to be registered as a Spring bean / handler.

## 7. Configuration

Programmatic library — there are no configuration files or property prefixes. Everything is configured through the builder DSL at runtime.

## 8. Core Usage / API

Describe a method with typed parameters and bind data:

```java
import org.springframework.bytebuddy.bytecode.EndpointApiBuilder;
import org.springframework.bytebuddy.bytecode.definition.MvcBound;
import org.springframework.bytebuddy.bytecode.definition.MvcParam;
import org.springframework.bytebuddy.bytecode.definition.MvcParamFrom;
import org.springframework.web.bind.annotation.RequestMethod;

new EndpointApiBuilder<EndpointApi>("com.example.DemoApi2")
        .controller()
        .bind(new MvcBound("uid", "{\"name\":\"demo\"}"))
        .newMethod("hello", "/hello/{name}", RequestMethod.POST, "application/json",
                new MvcBound("uid"),
                new MvcParam<>(String.class, "name", MvcParamFrom.PATH_VARIABLE))
        .then()
        .make()
        .load(getClass().getClassLoader(), ClassLoadingStrategy.Default.INJECTION);
```

Generated methods are stubbed (`StubMethod`) unless an implementation is attached with `proxy(...)` or `delegate(...)`:

```java
EndpointApiBuilder<EndpointApi> builder = new EndpointApiBuilder<>("com.example.DemoApi3");
builder.restController().proxy(myInvocationHandler).then();
```

## 9. Testing & Build

Build and run tests:

```bash
./mvnw clean verify
```

- Test suite covers the builder (`Bytebuddy_EndpointApiBuilder_Test`), annotations (`ByteBuddy_Annotation_Test`), naming strategies (`ByteBuddy_NamingStrategy_Test`), controller generation (`ByteBuddy_Controller_Test`), reactive handlers (`ByteBuddy_ReactiveHandler_Test`) and generic subclassing (`ByteBuddy_GenericSubclass_Test`).
- The build is configured with the JaCoCo Maven plugin: a coverage report is generated at `target/site/jacoco/index.html` and a rule checks the bundle line coverage against a 90% minimum (`haltOnFailure=false`, so the check reports but does not fail the build).
- The `central` Maven profile (`./mvnw -Pcentral deploy`) attaches GPG signatures, sources and Javadoc jars for publishing.

## 10. Versioning & Branches

Three parallel version lines are maintained:

| Branch | JDK | Version pattern |
|:---|:---|:---|
| `feature/1.0.x` | 8 | `1.0.x.*` |
| `feature/2.0.x` | 17 | `2.0.x.*` |
| `feature/3.0.x` | 21 | `3.0.x.*` |

Maintenance policy: the `1.0.x` line is the actively developed line (current snapshot `1.0.x.20260630-SNAPSHOT`); `2.0.x` and `3.0.x` are forward porting lines targeting newer JDKs. Snapshots are built on demand; tagged releases are distributed via GitHub Releases.

## 11. Contributing & License

- Fork the repository and open a pull request; keep the `1.0.x` line compatible with JDK 8.
- Bug reports and feature requests are tracked via GitHub Issues.
- Licensed under the [Apache License, Version 2.0](https://www.apache.org/licenses/LICENSE-2.0).
