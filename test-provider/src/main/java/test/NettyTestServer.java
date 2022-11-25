package test;

import com.lansg.rpc.api.HelloService;
import com.lansg.rpc.netty.server.NettyServer;
import com.lansg.rpc.registry.DefaultServiceRegistry;
import com.lansg.rpc.registry.ServiceRegistry;
import com.lansg.rpc.serializer.KryoSerializer;


/**
* @author: Lansg
* @date: 2022/11/22 21:21
* @Description: 测试用Netty服务提供者（服务端）
*/
public class NettyTestServer {
    public static void main(String[] args) {
        HelloService helloService = new HelloServiceImpl();
        ServiceRegistry registry = new DefaultServiceRegistry();
        registry.register(helloService);
        NettyServer server = new NettyServer();
        server.setSerializer(new KryoSerializer());
        server.start(9999);
    }
}
