package com.lansg.rpc.loadbalancer;

import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;

/**
* @author: Lansg
* @date: 2023/1/11 22:17
* @Description: 轮询负载均衡
*/
public class RoundRobinLoadBalancer implements LoadBalancer{

    private int index = 0;

    @Override
    public Instance select(List<Instance> instances) {
        if (index >= instances.size()){
            index %=instances.size();
        }
        return instances.get(index++);
    }
}
