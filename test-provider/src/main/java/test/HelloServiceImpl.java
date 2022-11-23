package test;

import com.lansg.rpc.api.HelloObject;
import com.lansg.rpc.api.HelloService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HelloServiceImpl implements HelloService {
    @Override
    public String hello(HelloObject object) {
        log.info("1号服务接收到：{}", object.getMessage());
        return "这是1号服务调用的返回值，id=" + object.getId();
    }
}
