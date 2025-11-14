# Helidon 4.3 Features - Ranked by Developer Interest

## 🥇 **1. Helidon Declarative APIs**
**Why it matters most:** This fundamentally changes how developers write Helidon SE code.

**The game-changer:**
- Eliminates boilerplate while maintaining performance (actually slight gains!)
- Built on Helidon Inject (4.2's build-time DI framework)
- **Zero runtime reflection** - everything resolved at build time
- Native image friendly by design

**The numbers speak volumes:**
- **+145% performance vs MicroProfile on JDK 21**
- **+291% performance vs MicroProfile on JDK 25**
- **No performance penalty vs imperative SE** (actually ~1% faster)
- Massive JDK 21→25 gains due to virtual threads foundation

**What you get in 4.3:**
- HTTP Web Server (declarative routing)
- Scheduling
- Fault Tolerance

**Code transformation:**
```java
// Before: Imperative boilerplate
static void routing(HttpRouting.Builder routing) {
    routing.register("/greet", new GreetService());
}

// After: Clean declarative
@RestServer.Endpoint
@Http.Path("/greet")
@Service.Singleton
class GreetEndpoint {
  @Http.GET
  String getDefaultMessageHandler(@Http.HeaderParam("X-DEV2DEV") String name) {
    return "Hello " + name + "!";
  }
}
```

**Developer impact:** Write less code, get better performance, easier maintenance. This is the future direction of Helidon SE.

---

## 🥈 **2. Helidon Data**
**Why #2:** Finally addresses the most requested feature from the community.

**What it is:**
- Implementation of the Repository pattern
- Abstraction layer between business logic and data storage
- Collection-like interface hiding data access complexity
- Domain-Driven Design friendly

**Key advantage:**
- Switch data sources without changing business logic
- Repository acts as "librarian" - you request data, it handles the details
- Works with both imperative and declarative styles

**Developer impact:** No more writing repetitive DAO/CRUD code. Especially valuable for teams practicing DDD or building complex domain models.

---

## 🥉 **3. Model Context Protocol (MCP) Support**
**Why #3:** Riding the massive AI wave - MCP is the new standard.

**Context:** MCP emerged in November and has rapidly become the de facto protocol for LLM-to-data connections.

**Helidon's advantages for MCP servers:**
- **Lightweight & fast** - built on Helidon WebServer
- **Virtual threads native** - scales instantly
- **Cloud-native ready** - deploy anywhere (VMs, containers, K8s)
- **Declarative or imperative** - your choice

**Full feature support:**
```java
@MCP.Server("my mcp server")
@MCP.Path("/mcp")
class MyMCPServer {
  @MCP.Tool("Tool Description")
  ToolContent tool(String state, Alert alert){ }

  @MCP.Prompt("Prompt Description")
  PromptContent prompt(String argument){ }

  @MCP.Resource
  @MCP.Completion
  @MCP.JsonSchema
}
```

**Build-time JSON Schema support** included.

**Developer impact:** Position your apps in the AI ecosystem. Build MCP servers that agents and LLMs can use as tools/data sources. Future-proof architecture.

---

## 4. **LangChain4j Enhancements**
**Why #4:** Makes AI integration production-ready.

**Major improvements:**

**a) Code-generated integration (the big one):**
- **Build-time introspection** generates bindings to LangChain4j APIs
- Eliminates runtime reflection → better GraalVM native images
- All LangChain4j features now available through decorators
- No waiting for Helidon releases to use new LangChain4j features
- Upgraded to LangChain4j **1.5.0**

**b) OCI Generative AI Service:**
- Helidon contributed this to LangChain4j upstream
- Benefits entire Java ecosystem, not just Helidon

**c) Java TokenStreamAdapter:**
- Wraps LangChain4j's async `StreamingChatResponseHandler`
- Converts to blocking Java Stream
- Simpler error handling for imperative code

**d) Jlama support:**
- Run LLMs **inside your JVM** (Gemma, Llama, Qwen)
- No external services needed
- Q4/Q8 quantization, MoE, tool calling

**e) Observability:**
- Based on OpenTelemetry GenAI Semantic Conventions
- Metrics for LLM requests, function calls, etc.

**f) Guardrails:**
- Built on LangChain4j's safety features

**Developer impact:** Production-grade AI features. The code-gen integration is huge - it removes the bottleneck of waiting for framework updates.

---

## 5. **Eureka Discovery**
**Why #5:** Critical for Spring/Helidon hybrid environments.

**The story:**
- 4.2 added Eureka **registration** (Helidon registers with Eureka)
- 4.3 adds Eureka **discovery** (Helidon discovers others via Eureka)

**Complete Spring interoperability:**
```
Spring microservices ←→ Eureka ←→ Helidon microservices
```

**Real-world value:**
- Migrate Spring→Helidon incrementally
- Run both frameworks side-by-side
- Helidon can call Spring services and vice versa
- Leverage existing Spring Cloud infrastructure

**Developer impact:** Makes Helidon viable in enterprise Spring shops. Political/organizational barriers removed.

---

## 6. **OpenTelemetry Enhancements**
**Why #6:** Observability is non-negotiable in production.

**Three key improvements:**

**a) Config-based setup:**
```yaml
telemetry:
  service: "test-otel"
  signals:
    tracing:
      sampler:
        type: "always_off"
      exporters:
        - type: otlp
          compression: gzip
```
- Programmatic (builders) OR declarative (config)
- No code changes to adjust tracing behavior

**b) Semantic conventions:**
- Auto-created HTTP spans now follow OpenTelemetry standards
- Dependency: `helidon-webserver-observe-telemetry-tracing`
- Better integration with OTel ecosystem tools

**c) Smoother Tracer initialization**

**Developer impact:** Production observability becomes configuration, not code. Standards compliance means better tool support (Jaeger, Grafana, etc.).

---

## 7. **gRPC Enhancements**
**Why #7:** Makes gRPC production-ready with enterprise features.

**New capabilities:**

**a) Client & server metrics:**
- Prometheus exportable
- Grafana visualizable
- Performance insight

**b) Client tracing:**
- Distributed tracing with OpenTelemetry
- Works alongside REST service tracing
- End-to-end visibility in polyglot systems

**c) gRPC Reflection:**
- Servers declare APIs dynamically
- Clients encode/decode in human-readable format
- **Debugging game-changer** - like Swagger for gRPC

**d) Optional compression:**
- Turn off for short messages (compression overhead > network time)
- Performance optimization option

**e) ACK frames & long data frame improvements:**
- Better handling of large payloads

**Developer impact:** gRPC moves from "it works" to "it's observable, debuggable, and optimized." Reflection alone is worth the upgrade.

---

## 8. **JSON-RPC**
**Why #8:** Niche but important for specific use cases.

**What it enables:**
- JSON-RPC protocol support
- Build-time JSON Schema support (used by MCP)
- Alternative RPC style to gRPC/REST

**Developer impact:** Useful for specific integrations, especially MCP-related work.

---

## 9. **JSON Schema**
**Why #9:** Supporting feature, but essential for MCP.

- Build-time support
- Used by JSON-RPC protocol
- Type safety for JSON data structures

---

## 10. **Gson Support**
**Why #10:** Nice-to-have, completes the JSON ecosystem.

**Existing:** Jackson, JSON-P, JSON-B
**New:** Gson

**Developer impact:** Freedom of choice. If you're already using Gson elsewhere, you can use it in Helidon too.

---

## 11. **Scheduling (Declarative)**
**Why #11:** Part of Declarative APIs, but less exciting than HTTP.

- Schedule tasks declaratively
- No manual timer management

---

## **Deep Think: Strategic Insights**

### **The Big Picture:**

1. **Helidon is betting on compile-time over runtime** - Declarative APIs, Helidon Inject, code-gen for LangChain4j all eliminate reflection and runtime overhead.

2. **Virtual threads are the foundation** - The JDK 21→25 performance gains prove this. Helidon 4 was built from the ground up for virtual threads.

3. **AI-first positioning** - MCP + LangChain4j enhancements show Oracle is serious about positioning Helidon in the AI space.

4. **Enterprise pragmatism** - Eureka support shows they understand enterprises have Spring investments and need migration paths.

5. **Performance without compromise** - Declarative APIs prove you can have clean code AND better performance. This is rare.

### **What to adopt first:**

**If greenfield:** Start with Declarative APIs immediately. It's the future.

**If existing app:**
1. Add Eureka Discovery if in Spring environment
2. Upgrade OpenTelemetry for better observability
3. Migrate to Declarative APIs incrementally

**If AI-curious:** MCP + LangChain4j is production-ready now.

**If data-heavy:** Helidon Data will save weeks of boilerplate.

### **Performance story:**

The 291% gain over MicroProfile on JDK 25 is **staggering**. This positions Helidon as potentially the fastest Java microservices framework, period.

---

## **Additional Resources**

- [Official Release Announcement](https://medium.com/helidon/helidon-4-3-released-29213af35587)
- [Helidon Documentation](https://helidon.io/docs/v4/about/doc_overview)
- [GitHub CHANGELOG](https://github.com/helidon-io/helidon/blob/main/CHANGELOG.md)
