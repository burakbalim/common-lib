# Common Library

This library contains common code and configurations used in microservice projects.

## Features

- Swagger/OpenAPI configuration
- Feign Client configuration
- Spring Boot auto-configuration
- Caching infrastructure

## Usage

To use this library, add the following dependency to your pom.xml file:

```xml
<dependency>
    <groupId>com.thales</groupId>
    <artifactId>common-lib</artifactId>
    <version>0.0.3-SNAPSHOT</version>
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

# Thales Cache Library

This module provides a common caching infrastructure for microservices. It is designed for Spring Boot applications and supports Redis or Caffeine cache providers.

## Features

- Spring Cache integration
- Support for different cache providers (Redis, Caffeine)
- Two usage options:
  - `CacheUtil`: For use with dependency injection as a Spring Bean
  - `StaticCacheUtil`: For static access from anywhere

## Installation

To use in microservices, add the common-lib dependency to your `pom.xml` file:

```xml
<dependency>
    <groupId>com.thales</groupId>
    <artifactId>common-lib</artifactId>
    <version>0.0.2-SNAPSHOT</version>
</dependency>
```

## Configuration

Configure cache properties in your `application.properties` or `application.yml` file using the following settings:

```yaml
thales:
  cache:
    enabled: true                   # Enable/disable caching (default: true)
    type: REDIS                     # Cache type: REDIS or IN_MEMORY (default: IN_MEMORY)
    
    # Redis cache configuration
    redis:
      defaultTtl: 3600000           # Default TTL in ms (default: 1 hour)
      useAppNameAsPrefix: true      # Add application name as prefix to cache keys (default: true)
      keyPrefix: ""                 # Additional cache key prefix (default: empty)
      serializationFormat: JSON     # Serialization format: JSON or JDK (default: JDK)
    
    # In-memory (Caffeine) cache configuration
    inMemory:
      maximumSize: 10000            # Maximum number of cache entries (default: 10000)
      expireAfterWrite: 3600000     # Expiration time after write in ms (default: 1 hour)
      expireAfterAccess: 3600000    # Expiration time after access in ms (default: 1 hour)
    
    # Custom TTL settings (per cache name)
    ttl:
      users: 86400000               # TTL for users cache in ms (1 day)
      products: 3600000             # TTL for products cache in ms (1 hour)
      tempUsers: 1800000            # TTL for temporary users in ms (30 minutes)
```

## Cache Yapılandırması

Cache yapılandırması `thales.cache` ve `thales.redis` önekleri altında yapılabilir. Örnek yapılandırma:

```yaml
thales:
  cache:
    enabled: true
    type: REDIS  # veya IN_MEMORY
    ttl:
      userCache: 1h
      tokenCache: 30m
  redis:
    host: localhost
    port: 6379
    password: şifre  # isteğe bağlı
    database: 0
    timeout: 2000
```

### Cache Tipleri

- `IN_MEMORY`: Caffeine önbelleği kullanır (varsayılan)
- `REDIS`: Redis önbelleği kullanır, performans ve dağıtık senaryolar için idealdir

### Uygulama Kodu ile Kullanım

Cache işlemlerini StaticCacheUtil veya CacheUtil ile yapabilirsiniz:

```java
// Static kullanım
StaticCacheUtil.put("userCache", userId, userObject);
Optional<User> user = StaticCacheUtil.get("userCache", userId, User.class);

// Dependency injection ile kullanım
@Autowired
private CacheUtil cacheUtil;

public void methodExample() {
    cacheUtil.put("userCache", userId, userObject);
    User user = cacheUtil.get("userCache", userId, User.class);
}
```

## Usage Examples

### 1. Using with Dependency Injection (Recommended)

```java
@Service
@RequiredArgsConstructor
public class UserService {
    private final CacheUtil cacheUtil;
    
    private static final String USER_CACHE = "users";
    
    public void saveUser(User user) {
        // Business logic...
        
        // Save user to cache
        cacheUtil.put(USER_CACHE, user.getId(), user);
    }
    
    public User getUser(String userId) {
        // Get user from cache
        User cachedUser = cacheUtil.get(USER_CACHE, userId, User.class);
        
        if (cachedUser != null) {
            return cachedUser;
        }
        
        // If not in cache, get from database
        User user = userRepository.findById(userId);
        
        if (user != null) {
            // Add user to cache
            cacheUtil.put(USER_CACHE, userId, user);
        }
        
        return user;
    }
    
    public void deleteUser(String userId) {
        // Business logic...
        
        // Remove user from cache
        cacheUtil.evict(USER_CACHE, userId);
    }
}
```

### 2. Using with Static Access

```java
import com.thales.common.cache.StaticCacheUtil;

public class AuthenticationHelper {
    
    private static final String TOKEN_CACHE = "tokens";
    
    public static void storeToken(String userId, String token) {
        StaticCacheUtil.put(TOKEN_CACHE, userId, token);
    }
    
    public static Optional<String> getToken(String userId) {
        return StaticCacheUtil.get(TOKEN_CACHE, userId, String.class);
    }
    
    public static void invalidateToken(String userId) {
        StaticCacheUtil.evict(TOKEN_CACHE, userId);
    }
}
```

## Available Methods

### CacheUtil (Bean)

- `clear(String cacheName)`: Clears the specified cache
- `clearAll()`: Clears all caches
- `evict(String cacheName, Object key)`: Removes the specified key from cache
- `get(String cacheName, Object key, Class<T> type)`: Reads value from cache
- `getWithDefault(String cacheName, Object key, Class<T> type, T defaultValue)`: Returns default value if not found
- `put(String cacheName, Object key, Object value)`: Saves value to cache
- `isCacheEnabled()`: Checks if caching is enabled
- `getCacheManager()`: Returns the underlying CacheManager object

### StaticCacheUtil (Static)

- `clearAllCaches()`: Clears all caches
- `clearCache(String cacheName)`: Clears the specified cache
- `evict(String cacheName, Object key)`: Removes the specified key from cache
- `get(String cacheName, Object key, Class<T> type)`: Reads value from cache (as Optional)
- `getWithDefault(String cacheName, Object key, Class<T> type, T defaultValue)`: Returns default value if not found
- `put(String cacheName, Object key, Object value)`: Saves value to cache
- `isCacheEnabled()`: Checks if caching is enabled

## Best Practices

1. Use the `CacheUtil` bean whenever possible (with dependency injection)
2. Static access should only be used for utility classes or when Spring context is not accessible
3. Define all cache keys as constants
4. Specify cache durations (TTL) in configuration files

## Redis Auto-Configuration

Bu kütüphane, Spring Boot'un varsayılan `RedisAutoConfiguration` sınıfını otomatik olarak devre dışı bırakır. Bu, uygulamanın kendi Redis yapılandırmasını oluşturmasına olanak tanır ve çakışan bean tanımlarını önler.

CommonLibAutoConfiguration sınıfı, `@ImportAutoConfiguration(exclude = {RedisAutoConfiguration.class})` ve `@AutoConfigureBefore(RedisAutoConfiguration.class)` anotasyonları ile Spring Boot'un Redis auto-configuration mekanizmasını devre dışı bırakır.

Bu sayede her mikroservis uygulamasında ayrı ayrı `@SpringBootApplication` anotasyonunda exclude etmeye gerek kalmaz.

```java
@SpringBootApplication(
    // Bu exclude işlemini yapmaya gerek yok - common-lib bunu otomatik olarak hallediyor
    /*exclude = {
        RedisAutoConfiguration.class,
        RedisRepositoriesAutoConfiguration.class
    }*/
)
public class YourServiceApplication {
    // ...
}
```

## URL Utility Class (URLUtil)

This library includes a `URLUtil` class for managing URLs in applications. This class is used to read and manage URLs from the configuration file (application.yml or application.properties).

### Configuration

```yaml
thales:
  url:
    base: www.wordflashy.com  # Base URL (e.g. www.wordflashy.com, staging.wordflashy.com)
```

### Usage

```java
@RestController
@RequiredArgsConstructor
public class WebController {

    private final URLUtil urlUtil;

    @GetMapping("/redirect")
    public ResponseEntity<Void> redirect() {
        // Get the base URL
        String baseUrl = urlUtil.getBaseUrl();
        
        // Create a full URL with a specific path
        String fullUrl = urlUtil.buildUrl("/login");
        
        // Redirect
        return ResponseEntity.status(HttpStatus.FOUND)
                .header("Location", fullUrl)
                .build();
    }
}
```

For static URL concatenation:

```java
String fullUrl = URLUtil.buildUrl("https://www.wordflashy.com", "/api/v1/users");
// Result: https://www.wordflashy.com/api/v1/users
```
