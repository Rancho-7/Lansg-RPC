package com.lansg.rpc.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
* @author: Lansg
* @date: 2022/10/31 18:43
*/
@AllArgsConstructor
@Getter
public enum RpcError {
    SERVICE_NOT_FOUND("找不到对应的服务"),
    SERVICE_NOT_IMPLEMENT_ANY_INTERFACE("注册的服务未实现接口"),
    SERVICE_INVOCATION_FAILURE("服务调用出现失败"),
    SERVICE_CAN_NOT_BE_NULL("注册的服务不得为空"),
    UNKNOWN_PROTOCOL("不识别的协议包"),
    UNKNOWN_SERIALIZER("不识别的(反)序列化器"),
    SERIALIZER_NOT_FOUND("找不到序列化器"),
    UNKNOWN_PACKAGE_TYPE("不识别的数据包类型");

    private final String message;
}
