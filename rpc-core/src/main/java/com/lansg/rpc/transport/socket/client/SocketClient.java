package com.lansg.rpc.transport.socket.client;

import com.lansg.rpc.loadbalancer.LoadBalancer;
import com.lansg.rpc.loadbalancer.RandomLoadBalancer;
import com.lansg.rpc.registry.NacosServiceDiscovery;
import com.lansg.rpc.registry.NacosServiceRegistry;
import com.lansg.rpc.registry.ServiceDiscovery;
import com.lansg.rpc.registry.ServiceRegistry;
import com.lansg.rpc.transport.RpcConsumer;
import com.lansg.rpc.entity.RpcRequestBean;
import com.lansg.rpc.entity.RpcResponseBean;
import com.lansg.rpc.enumeration.ResponseCode;
import com.lansg.rpc.enumeration.RpcError;
import com.lansg.rpc.exception.RpcException;
import com.lansg.rpc.serializer.CommonSerializer;
import com.lansg.rpc.transport.socket.util.ObjectReader;
import com.lansg.rpc.transport.socket.util.ObjectWriter;
import com.lansg.rpc.util.RpcMessageChecker;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
* @author: Lansg
* @date: 2022/11/22 21:02
* @Description: Socket方式远程方法调用的消费者（客户端）
*/
@Slf4j
public class SocketClient implements RpcConsumer {

    private final ServiceDiscovery serviceDiscovery;

    private final CommonSerializer serializer;

    public SocketClient(){
        this(DEFAULT_SERIALIZER,new RandomLoadBalancer());
    }

    public SocketClient(Integer serializer) {
        this(serializer,new RandomLoadBalancer());
    }

    public SocketClient(LoadBalancer loadBalancer){
        this(DEFAULT_SERIALIZER,loadBalancer);
    }

    public SocketClient(Integer serializer, LoadBalancer loadBalancer){
        this.serviceDiscovery=new NacosServiceDiscovery(loadBalancer);
        this.serializer=CommonSerializer.getByCode(serializer);
    }

    @Override
    public Object sendRequest(RpcRequestBean rpcRequest) {
        if (serializer==null){
            log.error("未设置序列化器");
            throw new RpcException(RpcError.SERIALIZER_NOT_FOUND);
        }
        InetSocketAddress inetSocketAddress = serviceDiscovery.lookupService(rpcRequest.getInterfaceName());
        try (Socket socket = new Socket()) {
//            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
//            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
//            objectOutputStream.writeObject(rpcRequest);
//            objectOutputStream.flush();
//            RpcResponseBean rpcResponse = (RpcResponseBean) objectInputStream.readObject();
            socket.connect(inetSocketAddress);
            OutputStream outputStream = socket.getOutputStream();
            InputStream inputStream = socket.getInputStream();
            ObjectWriter.writeObject(outputStream,rpcRequest,serializer);
            Object obj = ObjectReader.readObject(inputStream);
            RpcResponseBean rpcResponse = (RpcResponseBean) obj;
            if(rpcResponse == null) {
                log.error("服务调用失败，service：{}", rpcRequest.getInterfaceName());
                throw new RpcException(RpcError.SERVICE_INVOCATION_FAILURE, " service:" + rpcRequest.getInterfaceName());
            }
            if(rpcResponse.getCode() == null || rpcResponse.getCode() != ResponseCode.SUCCESS.getCode()) {
                log.error("调用服务失败, service: {}, response:{}", rpcRequest.getInterfaceName(), rpcResponse);
                throw new RpcException(RpcError.SERVICE_INVOCATION_FAILURE, " service:" + rpcRequest.getInterfaceName());
            }
            RpcMessageChecker.check(rpcRequest, rpcResponse);
            return rpcResponse;
        } catch (IOException e) {
            log.error("调用时有错误发生：", e);
            throw new RpcException("服务调用失败: ", e);
        }
    }

//    @Override
//    public void setSerializer(CommonSerializer serializer) {
//        this.serializer=serializer;
//    }
}
