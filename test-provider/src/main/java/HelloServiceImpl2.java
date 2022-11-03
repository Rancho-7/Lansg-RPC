import com.lansg.rpc.api.HelloObject;
import com.lansg.rpc.api.HelloService;
import com.lansg.rpc.api.HelloService2;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HelloServiceImpl2 implements HelloService2 {
    @Override
    public String hello(HelloObject obj,int helloId) {
        log.info("2号服务接收到：{} helloId为：{}", obj.getMessage(),helloId);
        return "这是调用2号服务的返回值，id=" + obj.getId();
    }
}
