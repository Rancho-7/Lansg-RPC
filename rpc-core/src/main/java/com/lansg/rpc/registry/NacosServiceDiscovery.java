package com.lansg.rpc.registry;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.lansg.rpc.util.NacosUtil;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.List;

@Slf4j
public class NacosServiceDiscovery implements ServiceDiscovery{

    private final NamingService namingService;

    public NacosServiceDiscovery() {
        namingService = NacosUtil.getNacosNamingService();
    }

    @Override
    public InetSocketAddress lookupService(String serviceName) {
        try {
            List<Instance> instances = NacosUtil.getAllInstance(namingService, serviceName);
            Instance instance = instances.get(0);
            return new InetSocketAddress(instance.getIp(), instance.getPort());
        } catch (NacosException e) {
            log.error("获取服务时有错误发生:", e);
        }
        return null;
    }
}
