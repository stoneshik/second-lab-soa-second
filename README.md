# Сервис-ориентированная архитектура

## Лабораторная работа № 2

### Вызываемый сервис, написанный на JAX-RS

Wildfly располагаем в корневую директорию проекта

Для сборки: `mvn clean package`<br>
Эта команда упаковывает проект в `war` и копирует `war` в `wildfly`

И запускаем wildfly:<br>
`./wildfly/bin/standalone.sh -c standalone-my.xml`

Настройка `wildfly`:<br>
```
cd wildfly
mkdir modules/org/postgresql/main
cd modules/org/postgresql/main
wget https://jdbc.postgresql.org/download/postgresql-42.7.8.jar
```

Создаем `modules/org/postgresql/main/module.xml`:
```
<?xml version="1.0" encoding="UTF-8"?>
<module xmlns="urn:jboss:module:1.1" name="org.postgresql">
    <resources>
        <resource-root path="postgresql-42.7.8.jar"/>
    </resources>
    <dependencies>
        <module name="javax.api"/>
        <module name="javax.transaction.api"/>
    </dependencies>
</module>
```

Пример конфигурации `standalone/configuration/standalone-my.xml`

Генерация ключа:
```
keytool -genkeypair -alias wildfly -keyalg RSA -keysize 4096 \
  -validity 3650 -keystore keystore.p12 \
  -storetype PKCS12 -storepass changeit -keypass changeit \
  -dname "CN=localhost, OU=Development, O=Company, L=City, ST=State, C=RU"
```

Экспорт сертификата:
```
keytool -exportcert -alias wildfly -keystore wildfly.p12 -storetype PKCS12 -storepass changeit -file wildfly.crt
```

Добавление сертификата в trustore:
```
keytool -importcert -alias wildfly -file wildfly.crt -keystore wildfly-truststore.p12 -storetype PKCS12 -storepass changeit -noprompt
```
