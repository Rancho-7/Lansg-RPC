package com.lansg.rpc.provider;

/**
* @author: Lansg
* @date: 2022/11/29 8:22
* @Description: 保存和提供服务实例对象
*/
public interface ServiceProvider {

    <T> void addServiceProvider(T service, Class<T> serviceClass);

    Object getServiceProvider(String serviceName);

}
