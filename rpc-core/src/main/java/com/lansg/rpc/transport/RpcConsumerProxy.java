package com.lansg.rpc.transport;

import com.lansg.rpc.entity.RpcResponseBean;
import com.lansg.rpc.transport.RpcConsumer;
import com.lansg.rpc.entity.RpcRequestBean;
import com.lansg.rpc.transport.netty.client.NettyClient;
import com.lansg.rpc.transport.socket.client.SocketClient;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

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

    @SuppressWarnings("unchecked")
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
        Object result = null;
        if (client instanceof NettyClient){
            CompletableFuture<RpcResponseBean> completableFuture = (CompletableFuture<RpcResponseBean>) client.sendRequest(rpcRequest);
            try {
                result = completableFuture.get().getData();
            }catch (InterruptedException | ExecutionException e){
                log.error("方法调用请求发送失败",e);
                return null;
            }
        }
        if (client instanceof SocketClient){
            RpcResponseBean rpcResponse = (RpcResponseBean) client.sendRequest(rpcRequest);
            result = rpcResponse.getData();
        }
        return result;
    }
}
