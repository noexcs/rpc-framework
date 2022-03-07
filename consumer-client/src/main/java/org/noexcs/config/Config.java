package org.noexcs.config;

import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

/**
 * @author noexcept
 * @since 1/17/2022 2:16 PM
 */
@Slf4j
public class Config {

    static Properties properties;

    static Map<String, Object> configs;

    static Boolean hasRegistry;

    static String registryType;

    static String registryServer;

    static Integer registryServerPort;

    static String loadBalanceType;

    static String serviceName;

    static String providerServer;

    static Integer providerPort;

    public static Boolean getHasRegistry() {
        return hasRegistry;
    }

    public static String getRegistryType() {
        return registryType;
    }

    public static String getLoadBalanceType() {
        return loadBalanceType;
    }

    public static String getProviderServer() {
        return providerServer;
    }

    public static Integer getProviderPort() {
        return providerPort;
    }

    static {
        try (InputStream inputStream = Config.class.getClassLoader().getResourceAsStream("rpc-consumer-config.yml")) {
            Yaml yaml = new Yaml();
            Iterable<Object> objects = yaml.loadAll(inputStream);

            configs = (Map) objects.iterator().next();

            Map registryMap = (Map) configs.get("registry");

            if (registryMap != null && registryMap.get("enabled") != null && ((Boolean) registryMap.get("enabled"))) {
                hasRegistry = true;
                log.debug("Service registry and discover enabled.");
                registryType = (String) registryMap.get("type");
                registryServer = (String) registryMap.get("server");
                registryServerPort = (Integer) registryMap.get("port");
                loadBalanceType = (String) registryMap.get("loadBalancer");
                serviceName = ((String) registryMap.get("serviceName"));

                if (registryType == null) {
                    registryType = "nacos";
                    log.debug("Registry type will be nacos by default.");
                }
                if (loadBalanceType == null) {
                    loadBalanceType = "org.noexcept.loadBalance.impl.RandomBalance";
                    log.debug("load balancer is {} by default", loadBalanceType);
                }
                if (registryServer == null || registryServerPort == null || serviceName == null) {
                    hasRegistry = false;
                    log.debug("Fail to load registry center configurations!");
                    if (registryServer == null) {
                        registryServer = "127.0.0.1";
                        log.debug("registry server will be 127.0.0.1 by default!");
                    }
                    if (registryServerPort == null) {
                        registryServerPort = 8848;
                        log.debug("registry server port will be 8848 by default!");
                    }
                    if (serviceName == null) {
                        serviceName = "rpc-public";
                        log.debug("service name will be rpc-public by default!");
                    }
                }
            } else {
                hasRegistry = false;
                log.debug("Straightforward rpc call without registry mode enabled.");
                Map provider = (Map) configs.get("provider");
                if (provider == null) {
                    log.debug("rpc provider not been configured, will enabled default configuration.");
                } else {
                    providerServer = ((String) provider.get("provider.server"));
                    providerPort = (Integer) provider.get("provider.port");
                }
                if (providerServer == null) {
                    providerServer = "127.0.0.1";
                }
                log.debug("Service provider IP is {}.", providerServer);
                if (providerPort == null) {
                    providerPort = 8007;
                }
                log.debug("Service provider port is {}.", providerPort);
            }

        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
    }


    public static String getRegistryServer() {
        if (registryServer != null) {
            return registryServer;
        }
        return "127.0.0.1";
    }

    public static Integer getRegistryServerPort() {
        if (registryServerPort != null) {
            return registryServerPort;
        }
        return 8848;
    }


    public static String getServiceName() {
        return serviceName;
    }

    /**
     * 当注册中心不存在时直接使用 provider 的地址端口
     *
     * @return return service provider's port, default 8007
     */
    public static Integer getServerPort() {
        if (providerPort != null) {
            return providerPort;
        }
        return 8007;
    }

    /**
     * 当注册中心不存在时直接用 provider 的地址
     *
     * @return return service provider's address, default "127.0.0.1"
     */
    public static String getServerHost() {
        if (providerServer != null) {
            return providerServer;
        }
        return "127.0.0.1";
    }
}
