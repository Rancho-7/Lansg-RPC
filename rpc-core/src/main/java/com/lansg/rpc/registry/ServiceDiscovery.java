package com.lansg.rpc.registry;

import java.net.InetSocketAddress;

/**
* @author: Lansg
* @date: 2022/11/30 10:33
* @Description: 服务发现接口
*/
public interface ServiceDiscovery {

    /**
     * @param serviceName 服务全称
     * @return 服务实体
    */
    InetSocketAddress lookupService(String serviceName);
}
