package com.lansg.rpc.registry;

import com.sun.org.glassfish.gmbal.Description;

import java.net.InetSocketAddress;

/**
* @author: Lansg
* @date: 2022/11/1 20:34
 * 服务注册中心通用接口
*/
public interface ServiceRegistry {
    /**
     * @param serviceName 待注册的服务实体
     * @param inetSocketAddress  提供服务的地址
     * @return
    */
    <T> void register(String serviceName, InetSocketAddress inetSocketAddress);

    /**
     * 根据服务名称查找服务实体
     * @param serviceName 服务名称
     * @return 服务实体
    */
    InetSocketAddress lookupService(String serviceName);
}
