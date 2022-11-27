package com.lansg.test;

import com.lansg.rpc.RpcConsumer;
import com.lansg.rpc.RpcConsumerProxy;
import com.lansg.rpc.api.HelloObject;
import com.lansg.rpc.api.HelloService;
import com.lansg.rpc.netty.client.NettyClient;
import com.lansg.rpc.serializer.HessianSerializer;
import com.lansg.rpc.serializer.ProtobufSerializer;

/**
* @author: Lansg
* @date: 2022/11/22 21:18
* @Description: 测试用Netty消费者
*/
public class NettyTestClient {
    public static void main(String[] args) {
        RpcConsumer client = new NettyClient("127.0.0.1", 9999);
        client.setSerializer(new ProtobufSerializer());
        RpcConsumerProxy rpcClientProxy = new RpcConsumerProxy(client);
        HelloService helloService = rpcClientProxy.getProxy(HelloService.class);
        HelloObject object = new HelloObject(12, "This is a message");
        String res = helloService.hello(object);
        System.out.println(res);

    }
}
