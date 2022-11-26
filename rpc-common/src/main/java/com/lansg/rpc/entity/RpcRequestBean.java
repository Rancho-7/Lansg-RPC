package com.lansg.rpc.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
* @author: Lansg
* @date: 2022/10/27 20:16
*/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RpcRequestBean implements Serializable {

    //请求号
    private String requestId;

    //待调用接口名称
    private String interfaceName;

    //待调用方法名称
    private String methodName;

    //调用方法参数列表
    private Object[] parameters;

    //调用方法的参数类型
    private Class<?>[] paramTypes;
}
