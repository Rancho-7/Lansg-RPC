import com.lansg.rpc.api.HelloService;
import com.lansg.rpc.provider.RpcProvider;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TestProvider {
    public static void main(String[] args) {
        HelloService helloService = new HelloServiceImpl();
        RpcProvider rpcProvider = new RpcProvider();
        rpcProvider.register(helloService, 9000);
    }
}
