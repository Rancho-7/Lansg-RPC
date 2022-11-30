package test;

import com.lansg.rpc.api.HelloObject;
import com.lansg.rpc.api.HelloService;
import com.lansg.rpc.api.HelloService2;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HelloServiceImpl2 implements HelloService {
    @Override
    public String hello(HelloObject obj) {
        log.info("2号服务接收到消息:{}", obj.getMessage());
        return "本次处理以Socket方式";
    }
}
