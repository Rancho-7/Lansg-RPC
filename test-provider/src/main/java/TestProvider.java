import com.lansg.rpc.api.HelloService;
import com.lansg.rpc.api.HelloService2;
import com.lansg.rpc.provider.RpcProvider;
import com.lansg.rpc.registry.DefaultServiceRegistry;
import com.lansg.rpc.registry.ServiceRegistry;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TestProvider {
    public static void main(String[] args) {
        HelloService helloService = new HelloServiceImpl();
        HelloService2 helloService2 = new HelloServiceImpl2();
        ServiceRegistry serviceRegistry = new DefaultServiceRegistry();
        serviceRegistry.register(helloService);
        serviceRegistry.register(helloService2);
        Class<?>[] s = helloService.getClass().getInterfaces();
        Class<?>[] s2 = helloService2.getClass().getInterfaces();
        for (Class<?> i:s){
            log.info("-------{}",i.getName());
            Object o = serviceRegistry.getService(i.getCanonicalName());
            log.info("获取到服务{}",o);
        }
        for (Class<?> i:s2){
            log.info("-------{}",i.getName());
            Object o = serviceRegistry.getService(i.getCanonicalName());
            log.info("获取到服务{}",o);
        }
        RpcProvider rpcProvider = new RpcProvider(serviceRegistry);
        rpcProvider.start(9000);
    }
}
