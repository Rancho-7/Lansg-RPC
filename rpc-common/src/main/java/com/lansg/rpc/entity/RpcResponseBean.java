package com.lansg.rpc.entity;

import com.lansg.rpc.enumeration.ResponseCode;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
* @author: Lansg
* @date: 2022/10/27 20:21
*/
@Data
@NoArgsConstructor
public class RpcResponseBean<T> implements Serializable {

    //响应对应的请求号
    private String requestId;

    //响应状态码
    private Integer code;

    //响应数据
    private T data;

    //补充信息
    private String message;

    public static <T> RpcResponseBean<T> success(T data,String requestId){
        RpcResponseBean<T> response=new RpcResponseBean<>();
        response.setRequestId(requestId);
        response.setCode(ResponseCode.SUCCESS.getCode());
        response.setData(data);
        return response;
    }
    public static <T> RpcResponseBean<T> fail(ResponseCode code,String requestId){
        RpcResponseBean<T> response=new RpcResponseBean<>();
        response.setRequestId(requestId);
        response.setCode(code.getCode());
        response.setMessage(code.getMessage());
        return  response;
    }
}
