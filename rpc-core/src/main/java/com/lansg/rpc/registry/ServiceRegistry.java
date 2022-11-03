package com.lansg.rpc.registry;

import com.sun.org.glassfish.gmbal.Description;

/**
* @author: Lansg
* @date: 2022/11/1 20:34
 * 服务注册表通用接口
*/
public interface ServiceRegistry {
    /**
     * @param service 待注册的服务实体
     * @param <T> 服务实体类
     * @return
    */
    <T> void register(T service);

    /**
     * @param serviceName 服务名称
     * @return 服务实体
    */
    Object getService(String serviceName);
}
