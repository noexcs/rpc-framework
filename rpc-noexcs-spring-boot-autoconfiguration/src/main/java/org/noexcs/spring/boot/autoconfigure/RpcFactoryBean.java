package org.noexcs.spring.boot.autoconfigure;

import org.noexcs.RpcClientProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;

/**
 * @author noexcs
 * @since 3/13/2022 8:51 AM
 */
public class RpcFactoryBean<T> implements FactoryBean<T> {

    public static final Logger LOGGER = LoggerFactory.getLogger(RpcFactoryBean.class);

    private Class<T> rpcInterface;

    private RpcClientProxy rpcClientProxy;

    public RpcFactoryBean(Class<T> rpcInterface) {
        this.rpcInterface = rpcInterface;
    }

    public Class<T> getRpcInterface() {
        return rpcInterface;
    }

    public void setRpcInterface(Class<T> rpcInterface) {
        this.rpcInterface = rpcInterface;
    }

    public RpcClientProxy getRpcClientProxy() {
        return rpcClientProxy;
    }

    public void setRpcClientProxy(RpcClientProxy rpcClientProxy) {
        this.rpcClientProxy = rpcClientProxy;
    }

    @Override
    public T getObject() throws Exception {
        LOGGER.debug("Creating {} proxy.", rpcInterface.getName());
        return rpcClientProxy.getProxy(rpcInterface);
    }

    @Override
    public Class<?> getObjectType() {
        return rpcInterface;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
