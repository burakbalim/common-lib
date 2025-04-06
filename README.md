# Common Library

Bu kütüphane, mikroservis projelerinde ortak olarak kullanılan kodları ve yapılandırmaları içerir.

## Özellikler

- Swagger/OpenAPI yapılandırması
- Feign Client yapılandırması
- Spring Boot otomatik yapılandırma

## Kullanım

Bu kütüphaneyi kullanmak için pom.xml dosyanıza aşağıdaki bağımlılığı ekleyin:

```xml
<dependency>
    <groupId>com.thales</groupId>
    <artifactId>common-lib</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

## İçerdiği Sınıflar

### OpenApiConfig

Swagger/OpenAPI yapılandırması için kullanılan sınıf. Aşağıdaki özellikleri application.properties dosyanızda özelleştirebilirsiniz:

```properties
# Swagger/OpenAPI yapılandırması
springdoc.server.url=http://localhost:8080
springdoc.api.title=API Başlığı
springdoc.api.description=API Açıklaması
springdoc.api.version=1.0.0
springdoc.api.contact.name=Destek Ekibi
springdoc.api.contact.email=destek@ornek.com
springdoc.api.license.name=MIT Lisansı
springdoc.api.license.url=https://opensource.org/licenses/MIT
```

### FeignClientInterceptor

Feign Client isteklerinde Authorization header'ını aktaran sınıf.

### CommonLibAutoConfiguration

Spring Boot otomatik yapılandırma sınıfı. Bu sayede kütüphane bağımlılığı eklendiğinde otomatik olarak yapılandırma yapılır.

## Geliştirme

Kütüphaneyi geliştirmek için:

```bash
cd common-parent-lib
mvn clean install
``` 