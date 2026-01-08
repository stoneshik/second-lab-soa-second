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

Меняем в файле `standalone/configuration/standalone-full.xml`:
```
<subsystem xmlns="urn:jboss:domain:datasources:7.2">
    <datasources>
        <datasource jndi-name="java:jboss/datasources/ExampleDS" pool-name="ExampleDS" enabled="true" use-java-context="true" statistics-enabled="${wildfly.datasources.statistics-enabled:${wildfly.statistics-enabled:false}}">
            <connection-url>jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE;MODE=${wildfly.h2.compatibility.mode:REGULAR}</connection-url>
            <driver>h2</driver>
            <security user-name="sa" password="sa"/>
        </datasource>
        <datasource jndi-name="java:/jdbc/SoaServiceDS" pool-name="SoaServiceDS" enabled="true" use-java-context="true">
            <connection-url>jdbc:postgresql://localhost:5432/soa_service</connection-url>
            <driver>postgresql</driver>
            <security user-name="postgres" password="admin"/>
            <validation>
                <valid-connection-checker class-name="org.jboss.jca.adapters.jdbc.extensions.postgres.PostgreSQLValidConnectionChecker"/>
                <validate-on-match>true</validate-on-match>
                <background-validation>false</background-validation>
                <exception-sorter class-name="org.jboss.jca.adapters.jdbc.extensions.postgres.PostgreSQLExceptionSorter"/>
            </validation>
            <pool>
                <min-pool-size>5</min-pool-size>
                <max-pool-size>20</max-pool-size>
                <prefill>true</prefill>
            </pool>
        </datasource>
        <drivers>
            <driver name="h2" module="com.h2database.h2">
                <xa-datasource-class>org.h2.jdbcx.JdbcDataSource</xa-datasource-class>
            </driver>
            <driver name="postgresql" module="org.postgresql">
                <xa-datasource-class>org.postgresql.xa.PGXADataSource</xa-datasource-class>
            </driver>
        </drivers>
    </datasources>
</subsystem>
```
