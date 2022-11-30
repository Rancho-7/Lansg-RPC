package com.lansg.rpc.registry;

import com.sun.org.glassfish.gmbal.Description;

import java.net.InetSocketAddress;

/**
* @author: Lansg
* @date: 2022/11/1 20:34
 * 服务注册接口
*/
public interface ServiceRegistry {
    /**
     * @param serviceName 待注册的服务实体
     * @param inetSocketAddress  提供服务的地址
     * @return
    */
    <T> void register(String serviceName, InetSocketAddress inetSocketAddress);

}
