package test;

import com.lansg.rpc.api.HelloService;
import com.lansg.rpc.transport.netty.server.NettyServer;
import com.lansg.rpc.provider.ServiceProviderImpl;
import com.lansg.rpc.registry.ServiceRegistry;
import com.lansg.rpc.serializer.ProtobufSerializer;


/**
* @author: Lansg
* @date: 2022/11/22 21:21
* @Description: 测试用Netty服务提供者（服务端）
*/
public class NettyTestServer {
    public static void main(String[] args) {
        HelloService helloService = new HelloServiceImpl();
        NettyServer server = new NettyServer("127.0.0.1", 9999);
        server.setSerializer(new ProtobufSerializer());
        server.publishService(helloService,HelloService.class);
    }
}
