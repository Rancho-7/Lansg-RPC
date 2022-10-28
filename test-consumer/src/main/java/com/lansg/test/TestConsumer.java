package com.lansg.test;

import com.lansg.rpc.api.HelloObject;
import com.lansg.rpc.api.HelloService;
import com.lansg.rpc.consumer.RpcConsumer;
import com.lansg.rpc.consumer.RpcConsumerProxy;

public class TestConsumer {
    public static void main(String[] args) {
        RpcConsumerProxy proxy = new RpcConsumerProxy("127.0.0.1", 9000);
        HelloService helloService = proxy.getProxy(HelloService.class);
        HelloObject object = new HelloObject(12, "This is a message");
        String res = helloService.hello(object);
        System.out.println(res);
    }
}
