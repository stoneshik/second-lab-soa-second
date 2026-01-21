# Сервис-ориентированная архитектура

## Лабораторная работа № 2

### Второй сервис, написанный на Spring Boot

Для запуска в обычном режиме значение переменной окружения `SPRING_PROFILES_ACTIVE` равное `dev`

Файл `.env` должен находится в директории `resources`

Пример `.env` файла находится в `env.example`

Подключение к серверу:<br>
`ssh s33xxxx@se.ifmo.ru -p 2222`

Проброс порта для helios:<br>
`ssh -L 8080:localhost:33xxxx s33xxxx@se.ifmo.ru -p 2222`

Url подключения к БД<br>
`jdbc:postgresql://localhost:5432/studs`

Генерация ключа:
```
keytool -genkeypair -alias springboot -keyalg RSA -keysize 4096 \
  -validity 3650 -keystore keystore.p12 \
  -storetype PKCS12 -storepass changeit -keypass changeit \
  -dname "CN=localhost, OU=Development, O=Company, L=City, ST=State, C=RU"
```

Сбор jar файла с пропуском тестов<br>
`mvn package -DskipTests`

Ссылка на фронтенд - https://github.com/stoneshik/second-lab-soa-frontend

Ссылка на первый основной сервис реализованный на JAX-RS - https://github.com/stoneshik/second-lab-soa
