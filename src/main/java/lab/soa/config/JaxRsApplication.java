package lab.soa.config;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.jakarta.rs.xml.JacksonXMLProvider;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import lab.soa.resource.FlatResource;

@ApplicationPath("/api/v1")
public class JaxRsApplication extends Application {
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<>();
        // Ресурсы
        classes.add(FlatResource.class);
        // Jackson XML провайдеры
        classes.add(JacksonXMLProvider.class);
        return classes;
    }
}

