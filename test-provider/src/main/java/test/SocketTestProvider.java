package test;

import com.lansg.rpc.api.HelloService;
import com.lansg.rpc.provider.ServiceProviderImpl;
import com.lansg.rpc.registry.ServiceRegistry;
import com.lansg.rpc.serializer.CommonSerializer;
import com.lansg.rpc.serializer.HessianSerializer;
import com.lansg.rpc.transport.socket.server.SocketServer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SocketTestProvider {
    public static void main(String[] args) {
        HelloService helloService = new HelloServiceImpl2();
        SocketServer socketServer = new SocketServer("127.0.0.1", 9998, CommonSerializer.KRYO_SERIALIZER);
//        socketServer.setSerializer(new HessianSerializer());
        socketServer.publishService(helloService, HelloService.class);
    }

//    public static void main(String[] args) {
//        HelloService helloService = new HelloServiceImpl();
//        HelloService2 helloService2 = new HelloServiceImpl2();
//        ServiceRegistry serviceRegistry = new DefaultServiceRegistry();
//        serviceRegistry.register(helloService);
//        serviceRegistry.register(helloService2);
//        Class<?>[] s = helloService.getClass().getInterfaces();
//        Class<?>[] s2 = helloService2.getClass().getInterfaces();
//        for (Class<?> i:s){
//            log.info("-------{}",i.getName());
//            Object o = serviceRegistry.getService(i.getCanonicalName());
//            log.info("获取到服务{}",o);
//        }
//        for (Class<?> i:s2){
//            log.info("-------{}",i.getName());
//            Object o = serviceRegistry.getService(i.getCanonicalName());
//            log.info("获取到服务{}",o);
//        }
//        RpcProvider rpcProvider = new RpcProvider(serviceRegistry);
//        rpcProvider.start(9000);
//    }
}
