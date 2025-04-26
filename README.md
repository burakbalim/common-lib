# Common Library

This library contains common code and configurations used in microservice projects.

## Features

- Swagger/OpenAPI configuration
- Feign Client configuration
- Spring Boot auto-configuration

## Usage

To use this library, add the following dependency to your pom.xml file:

```xml
<dependency>
    <groupId>com.thales</groupId>
    <artifactId>common-lib</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

## Included Classes

### OpenApiConfig

Class used for Swagger/OpenAPI configuration. You can customize the following properties in your application.properties file:

```properties
# Swagger/OpenAPI configuration
springdoc.server.url=http://localhost:8080
springdoc.api.title=API Title
springdoc.api.description=API Description
springdoc.api.version=1.0.0
springdoc.api.contact.name=Support Team
springdoc.api.contact.email=support@example.com
springdoc.api.license.name=MIT License
springdoc.api.license.url=https://opensource.org/licenses/MIT
```

### FeignClientInterceptor

Class that passes the Authorization header in Feign Client requests.

### CommonLibAutoConfiguration

Spring Boot auto-configuration class. This allows automatic configuration when the library dependency is added.

## Development

To develop the library:

```bash
cd common-parent-lib
mvn clean install
```

## Cache Provider Usage

The Cache Provider in the Common Library provides two different caching mechanisms for microservices: In-memory (Caffeine) and Redis.

### Dependencies

The Cache Provider includes these dependencies:
- `spring-boot-starter-cache`: Spring Cache infrastructure
- `caffeine`: In-memory cache support (default)
- `spring-boot-starter-data-redis`: Redis cache support (optional)

### Configuration

The Cache Provider provides automatic configuration and can be used in microservices without writing additional code.

#### Application Configuration Example (application.yml)

```yaml
thales:
  cache:
    enabled: true
    # Cache type: IN_MEMORY, REDIS, or NONE
    type: IN_MEMORY
    
    # Custom TTL durations for each cache (optional)
    ttl:
      userCache: 30m
      productCache: 10m
    
    # In-memory (Caffeine) cache configuration
    in-memory:
      maximum-size: 1000
      default-ttl: 10m
      expire-after-write: 10m
      expire-after-access: 10m
    
    # Redis cache configuration
    redis:
      default-ttl: 30m
      key-prefix: myapp
      use-app-name-as-prefix: true
      serialization-format: JDK # or JSON
```

For Redis usage, you also need to configure Spring Redis connection settings:

```yaml
spring:
  redis:
    host: localhost
    port: 6379
    password: password
```

### Usage Example

1. **Using Spring Cache Annotations**

```java
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Cacheable(value = "userCache", key = "#userId")
    public User getUserById(String userId) {
        // Get user from database
    }
    
    @CacheEvict(value = "userCache", key = "#user.id")
    public void updateUser(User user) {
        // Update user
    }
}
```

2. **Programmatic Usage**

```java
import com.thales.common.cache.CacheUtil;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final CacheManager cacheManager;
    
    public UserService(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }
    
    public User getUserById(String userId) {
        // Try reading from cache
        return CacheUtil.getFromCache(cacheManager, "userCache", userId, User.class)
                .orElseGet(() -> {
                    // Get from database if not in cache
                    User user = fetchUserFromDatabase(userId);
                    
                    // Save to cache
                    CacheUtil.putToCache(cacheManager, "userCache", userId, user);
                    return user;
                });
    }
    
    public void clearUserCache(String userId) {
        CacheUtil.evictFromCache(cacheManager, "userCache", userId);
    }
    
    public void clearAllUserCaches() {
        CacheUtil.clearCache(cacheManager, "userCache");
    }
}
```

### Changing Cache Type

To change the cache type, you only need to update the configuration; no code changes are required.

```yaml
# Switching from in-memory cache to Redis
thales:
  cache:
    type: REDIS
```

### Disabling Caching

To temporarily disable caching:

```yaml
thales:
  cache:
    enabled: false
``` 