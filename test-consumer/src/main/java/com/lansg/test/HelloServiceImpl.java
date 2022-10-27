package com.lansg.test;

import com.lansg.rpc.api.HelloObject;
import com.lansg.rpc.api.HelloService;
import lombok.extern.slf4j.Slf4j;
/**
* @author: Lansg
* @date: 2022/10/27 16:47
*/
@Slf4j
public class HelloServiceImpl implements HelloService {
    @Override
    public String hello(HelloObject obj) {
        log.info("接收到消息:{}",obj.getMessage());
        return "这是调用的返回值,id="+obj.getId();
    }
}
