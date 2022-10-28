package com.lansg.rpc.consumer;

import com.lansg.rpc.entity.RpcRequestBean;
import com.lansg.rpc.entity.RpcResponseBean;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class RpcConsumerProxy implements InvocationHandler {
    private String host;
    private int port;

    public RpcConsumerProxy(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @SuppressWarnings("unchecked")
    public <T> T getProxy(Class<T> clazz) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz}, this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RpcRequestBean rpcRequest = RpcRequestBean.builder()
                .interfaceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .parameters(args)
                .paramTypes(method.getParameterTypes())
                .build();
        RpcConsumer rpcClient = new RpcConsumer();
        return ((RpcResponseBean) rpcClient.sendRequest(rpcRequest, host, port)).getData();
    }
}
