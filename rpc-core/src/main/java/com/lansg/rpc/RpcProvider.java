package com.lansg.rpc;

import com.lansg.rpc.serializer.CommonSerializer;

/**
* @author: Lansg
* @date: 2022/11/23 9:18
* @Description: 服务端通用接口
*/
public interface RpcProvider {

    void start(int port);

    void setSerializer(CommonSerializer serializer);

}
