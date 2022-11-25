package com.lansg.rpc;

import com.lansg.rpc.entity.RpcRequestBean;
import com.lansg.rpc.serializer.CommonSerializer;

/**
* @author: Lansg
* @date: 2022/11/22 16:34
* @Description: 客户端类通用接口
*/
public interface RpcConsumer {

    Object sendRequest(RpcRequestBean rpcRequest);

    void setSerializer(CommonSerializer serializer);

}
