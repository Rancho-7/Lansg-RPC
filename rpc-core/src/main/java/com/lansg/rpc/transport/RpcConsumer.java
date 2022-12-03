package com.lansg.rpc.transport;

import com.lansg.rpc.entity.RpcRequestBean;
import com.lansg.rpc.serializer.CommonSerializer;

/**
* @author: Lansg
* @date: 2022/11/22 16:34
* @Description: 客户端类通用接口
*/
public interface RpcConsumer {

    int DEFAULT_SERIALIZER = CommonSerializer.KRYO_SERIALIZER;

    Object sendRequest(RpcRequestBean rpcRequest);

//    void setSerializer(CommonSerializer serializer);

}
