package com.lansg.rpc.registry;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.lansg.rpc.enumeration.RpcError;
import com.lansg.rpc.exception.RpcException;
import com.lansg.rpc.util.NacosUtil;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.rmi.Naming;
import java.util.List;

/**
* @author: Lansg
* @date: 2022/11/29 8:39
* @Description: Nacos服务注册中心
*/
@Slf4j
public class NacosServiceRegistry implements ServiceRegistry{

//    public final NamingService namingService;

//    static {
//        try {
//            namingService = NamingFactory.createNamingService(SERVER_ADDR);
//        } catch (NacosException e) {
//            log.error("连接到Nacos时有错误发生:{}",e);
//            throw new RpcException(RpcError.FAILED_TO_CONNECT_TO_SERVICE_REGISTRY);
//        }
//    }
//    public NacosServiceRegistry() {
//        this.namingService = NacosUtil.getNacosNamingService();
//    }

    @Override
    public void register(String serviceName, InetSocketAddress inetSocketAddress) {
        try {
            NacosUtil.registerService(serviceName, inetSocketAddress);
        } catch (NacosException e) {
            log.error("注册服务时有错误发生:{}",e);
            throw new RpcException(RpcError.REGISTER_SERVICE_FAILED);
        }
    }

}
