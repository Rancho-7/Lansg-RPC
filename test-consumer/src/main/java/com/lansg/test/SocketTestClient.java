package com.lansg.test;

import com.lansg.rpc.RpcConsumerProxy;
import com.lansg.rpc.api.HelloObject;
import com.lansg.rpc.api.HelloService;
import com.lansg.rpc.api.HelloService2;
import com.lansg.rpc.socket.client.SocketClient;


public class SocketTestClient {
    public static void main(String[] args) {
        SocketClient client = new SocketClient("127.0.0.1", 9000);
        RpcConsumerProxy proxy = new RpcConsumerProxy(client);
        HelloService helloService = proxy.getProxy(HelloService.class);
//        HelloService2 helloService2 = proxy.getProxy(HelloService2.class);
        HelloObject object = new HelloObject(12, "This is a message");
        String res1 = helloService.hello(object);
//        String res2 = helloService2.hello(object,666);
        System.out.println(res1);
//        System.out.println(res2);
    }
}