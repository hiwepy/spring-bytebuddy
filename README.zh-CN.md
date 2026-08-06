# spring-bytebuddy

[English](./README.md) | [简体中文](./README.zh-CN.md)

## 目录

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

`spring-bytebuddy` 使用 ByteBuddy 在运行时生成 Spring MVC Controller。无需手写 `@Controller` / `@RestController` 类，只需用流式构建器（`EndpointApiBuilder`）描述端点——类注解（`@Controller`、`@RestController`、Swagger `@Api`）、请求映射、方法、参数与调用处理器——模块即可产出可加载的、可直接用于 Spring MVC（Servlet）或 Spring WebFlux（响应式）的类。

它面向需要在不重新编译的情况下动态暴露 HTTP 端点的插件化应用——不是代码生成工具，也不能替代手写常规 Spring Controller。

典型场景：

| 场景 | 本模块提供的组件 |
|:---|:---|
| 运行时生成 `@RestController` | `EndpointApiBuilder`（注解、映射、方法、参数） |
| 响应式处理器端点 | `ReactiveHandlerBuilder` / `ReactiveHandler` |
| 生成类上的 Swagger 注解 | `SwaggerAnnotationUtils` |
| 代理或委托实现 | `EndpointApiBuilder#proxy(...)` / `#delegate(...)` |
| 可复用的映射/参数模型 | `MvcMapping`、`MvcMethod`、`MvcParam`、`MvcParamFrom`、`MvcBound` |

## 2. Features & Status

项目状态：`1.0.x.*` 预发布开发线（快照版本）；在首个正式 Release 标签之前，公开 API 仍在稳定过程中。

| 能力 | 状态 | 说明 |
|:---|:---|:---|
| 流式 Controller 构建器 | 稳定 | `EndpointApiBuilder<T extends EndpointApi>` 串联 `api`、`controller`、`restController`、`requestMapping`、`autowired`、`newMethod` |
| 运行时注解生成 | 稳定 | `@Controller`、`@RestController`、`@RequestMapping` 系列、`@Autowired`、Swagger `@Api` / `@ApiIgnore` |
| 方法与参数生成 | 稳定 | `newMethod(name, path, method, consumes, bound, params)`，参数经 `MvcParam` 注解（`@PathVariable`、`@RequestParam` 等，通过 `MvcParamFrom`） |
| 实现策略 | 稳定 | `proxy(InvocationHandler)` 与 `delegate(Class)` 为生成方法提供实现 |
| ByteBuddy 互操作 | 稳定 | `then()` 暴露底层 `DynamicType.Builder`，便于高级定制 |
| 响应式支持 | 稳定 | `ReactiveHandlerBuilder` / `ReactiveHandler` 支持 WebFlux 风格处理器 |
| 注解工具 | 稳定 | `EndpointApiAnnotationUtils`、`SwaggerAnnotationUtils` |

## 3. Requirements & Compatibility

| 要求 | 版本 |
|:---|:---|
| JDK | 21+ |
| Maven | 3.6+ |
| ByteBuddy | 已在 POM 声明 |
| Spring Framework | 5.3.x（spring-webmvc、spring-webflux、spring-context） |
| Swagger 注解 | springfox-core + swagger-annotations |
| Servlet / 校验 API | jakarta.servlet-api、jakarta.validation-api（已在 POM 声明） |

版本线：

| 分支 | JDK | 版本模式 | 说明 |
|:---|:---|:---|:---|
| `feature/1.0.x` | 8 | `1.0.x.*` | 当前开发线；Spring 5.x 时代 |
| `feature/2.0.x` | 17 | `2.0.x.*` | 下一条版本线 |
| `feature/3.0.x` | 21 | `3.0.x.*` | 未来版本线 |

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

本工程为单 jar 模块，包位于 `org.springframework.bytebuddy`：

| 包 | 职责 |
|:---|:---|
| `bytecode` | `EndpointApi`、`EndpointApiBuilder`、`ReactiveHandler`、`ReactiveHandlerBuilder` |
| `bytecode.definition` | `MvcBound`、`MvcMapping`、`MvcMethod`、`MvcParam`、`MvcParamFrom` |
| `utils` | `EndpointApiAnnotationUtils`、`SwaggerAnnotationUtils` |
| `annotation` | `WebBound` |

## 5. Installation

制品发布到 easy4j 私有仓库与 GitHub Releases，暂未发布 Maven Central。

Maven：

```xml
<dependency>
    <groupId>io.github.easy4j</groupId>
    <artifactId>spring-bytebuddy</artifactId>
    <version>3.0.x.x.20260630-SNAPSHOT</version>
</dependency>
```

Gradle：

```groovy
implementation 'io.github.easy4j:spring-bytebuddy:3.0.x.x.20260630-SNAPSHOT'
```

## 6. Quick Start

运行时生成 `@RestController` 并加载：

```java
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import org.springframework.bytebuddy.bytecode.EndpointApi;
import org.springframework.bytebuddy.bytecode.EndpointApiBuilder;
import org.springframework.web.bind.annotation.RequestMethod;

Class<?> controllerClass = new EndpointApiBuilder<EndpointApi>("com.example.DemoApi")
        .restController()
        .api("Demo API", "demo")                 // Swagger @Api
        .requestMapping("/api/demo")             // 类级别映射
        .newMethod("sayHello", "/say", RequestMethod.GET,
                "application/json", null)        // 方法级别映射
        .then()                                  // 底层 ByteBuddy builder
        .make()
        .load(getClass().getClassLoader(), ClassLoadingStrategy.Default.INJECTION)
        .getLoaded();

System.out.println(controllerClass.getName());   // com.example.DemoApi
```

预期结果：生成名为 `com.example.DemoApi` 的类，携带 `@RestController`、映射与 Swagger 元数据注解，可被类加载器加载——随后即可注册为 Spring Bean / 处理器。

## 7. Configuration

编程式库——无配置文件与属性前缀，所有配置均在运行时通过构建器 DSL 完成。

## 8. Core Usage / API

用类型化参数与方法绑定描述一个方法：

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

生成的方法默认以 `StubMethod` 存根，除非通过 `proxy(...)` 或 `delegate(...)` 附加实现：

```java
EndpointApiBuilder<EndpointApi> builder = new EndpointApiBuilder<>("com.example.DemoApi3");
builder.restController().proxy(myInvocationHandler).then();
```

## 9. Testing & Build

构建与测试：

```bash
./mvnw clean verify
```

- 测试套件覆盖构建器（`Bytebuddy_EndpointApiBuilder_Test`）、注解（`ByteBuddy_Annotation_Test`）、命名策略（`ByteBuddy_NamingStrategy_Test`）、Controller 生成（`ByteBuddy_Controller_Test`）、响应式处理器（`ByteBuddy_ReactiveHandler_Test`）与泛型子类化（`ByteBuddy_GenericSubclass_Test`）；
- 构建配置了 JaCoCo Maven 插件：覆盖率报告生成于 `target/site/jacoco/index.html`，并配置了 BUNDLE 行覆盖率 90% 的校验规则（`haltOnFailure=false`，即只报告不阻断构建）；
- `central` Maven Profile（`./mvnw -Pcentral deploy`）附加 GPG 签名、源码包与 Javadoc 包用于发布。

## 10. Versioning & Branches

维护三条并行版本线：

| 分支 | JDK | 版本模式 |
|:---|:---|:---|
| `feature/1.0.x` | 8 | `1.0.x.*` |
| `feature/2.0.x` | 17 | `2.0.x.*` |
| `feature/3.0.x` | 21 | `3.0.x.*` |

维护策略：`1.0.x` 为当前活跃开发线（当前快照 `3.0.x.x.20260630-SNAPSHOT`）；`2.0.x` 与 `3.0.x` 为面向更新 JDK 的前向移植线。快照按需构建，正式 Release 通过 GitHub Releases 分发。

## 11. Contributing & License

- Fork 仓库并提交 Pull Request；`1.0.x` 版本线保持 JDK 8 兼容；
- Bug 反馈与功能建议通过 GitHub Issues 跟踪；
- 基于 [Apache License, Version 2.0](https://www.apache.org/licenses/LICENSE-2.0) 开源。
