package com.lansg.rpc.transport;

import com.lansg.rpc.transport.RpcConsumer;
import com.lansg.rpc.entity.RpcRequestBean;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;

@Slf4j
public class RpcConsumerProxy implements InvocationHandler {
//    private String host;
//    private int port;

//    public RpcConsumerProxy(String host, int port) {
//        this.host = host;
//        this.port = port;
//    }

    private final RpcConsumer client;

    public RpcConsumerProxy(RpcConsumer client){
        this.client=client;
    }

    //@SuppressWarnings用来取消Java编译器发出的警告，避免警告过于繁杂。
    @SuppressWarnings("unchecked")
    public <T> T getProxy(Class<T> clazz) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz}, this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        log.info("调用方法: {}#{}", method.getDeclaringClass().getName(), method.getName());
//        RpcRequestBean rpcRequest = RpcRequestBean.builder()
//                .interfaceName(method.getDeclaringClass().getName())
//                .methodName(method.getName())
//                .parameters(args)
//                .paramTypes(method.getParameterTypes())
//                .build();
//        com.lansg.rpc.consumer.RpcConsumer rpcConsumer = new RpcConsumer();
//        return rpcConsumer.sendRequest(rpcRequest, host, port);
        RpcRequestBean rpcRequest = new RpcRequestBean(UUID.randomUUID().toString(),method.getDeclaringClass().getName(),
                method.getName(),args,method.getParameterTypes(),false);
        return client.sendRequest(rpcRequest);
    }
}
