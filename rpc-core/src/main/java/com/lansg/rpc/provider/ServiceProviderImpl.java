package com.lansg.rpc.provider;

import com.lansg.rpc.enumeration.RpcError;
import com.lansg.rpc.exception.RpcException;
import com.lansg.rpc.registry.ServiceRegistry;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: Lansg
 * @date: 2022/11/1 20:34
 * 默认的服务注册表,保存服务端本地服务
 */
@Slf4j
public class ServiceProviderImpl implements ServiceProvider {

    private static final Map<String,Object> serviceMap = new ConcurrentHashMap<>();
    private static final Set<String> registeredService = ConcurrentHashMap.newKeySet();

    @Override
    public  <T> void addServiceProvider(T service, Class<T> serviceClass) {
        //getCanonicalName()返回正常包含路径的类名:HelloServiceImpl
        String serviceName = serviceClass.getCanonicalName();
        if (registeredService.contains(serviceName)) return;
        registeredService.add(serviceName);
//        Class<?>[] interfaces = service.getClass().getInterfaces();
//        if (interfaces.length == 0){
//            throw new RpcException(RpcError.SERVICE_NOT_IMPLEMENT_ANY_INTERFACE);
//        }
//        for (Class<?> i:interfaces){
//            serviceMap.put(i.getCanonicalName(),service);
//        }
        serviceMap.put(serviceName,service);
        log.info("向接口: {} 注册服务: {}", service.getClass().getInterfaces(), serviceName);
    }

    @Override
    public  Object getServiceProvider(String serviceName) {
        Object service = serviceMap.get(serviceName);
        if (service == null){
            throw new RpcException(RpcError.SERVICE_NOT_FOUND);
        }
        return service;
    }
}
