package com.lansg.rpc;

import com.lansg.rpc.entity.RpcRequestBean;

/**
* @author: Lansg
* @date: 2022/11/22 16:34
* @Description: 客户端类通用接口
*/
public interface RpcConsumer {
    Object sendRequest(RpcRequestBean rpcRequest);
}
