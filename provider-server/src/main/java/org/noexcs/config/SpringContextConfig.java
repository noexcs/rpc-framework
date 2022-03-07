package org.noexcs.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.context.annotation.*;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;

import java.io.IOException;
import java.util.Objects;
import java.util.Properties;

/**
 * @author com.noexcept
 * @since 1/17/2022 5:43 PM
 */
@Configuration
@ComponentScan("org.noexcs.service")
@ComponentScan()
@PropertySource(value = "classpath:rpc-provider-config.yaml", factory = SpringContextConfig.YamlPropertyFactory.class)
@Data
public class SpringContextConfig {

    @Value("${server.host}" )
    private String host;

    @Value("${server.port}")
    private int port;

    @Value("${registry.serviceName}")
    private String serviceName;

    @Value("${registry.enabled}")
    private Boolean registryEnabled;

    @Value("${registry.server}")
    private String registryServer;

    @Value("${registry.port}")
    private Integer registryServerPort;

    @SuppressWarnings("all")
    static class YamlPropertyFactory implements PropertySourceFactory {

        @Override
        public org.springframework.core.env.PropertySource<?> createPropertySource(String s, EncodedResource encodedResource) throws IOException {
            YamlPropertiesFactoryBean yaml = new YamlPropertiesFactoryBean();
            yaml.setResources(encodedResource.getResource());
            Properties properties = yaml.getObject();
            if (s != null) {
                assert properties != null;
                return new PropertiesPropertySource(s, properties);
            } else {
                return new PropertiesPropertySource(Objects.requireNonNull(encodedResource.getResource().getFilename()), properties);
            }
        }
    }
}
