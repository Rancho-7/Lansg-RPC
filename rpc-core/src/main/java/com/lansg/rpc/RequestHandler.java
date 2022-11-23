package com.lansg.rpc;


import com.lansg.rpc.entity.RpcRequestBean;
import com.lansg.rpc.entity.RpcResponseBean;
import com.lansg.rpc.enumeration.ResponseCode;
import com.sun.org.apache.regexp.internal.REUtil;
import lombok.Setter;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;

/**
* @author: Lansg
* @date: 2022/11/1 21:48
 * 进行过程调用的处理器
*/
@Slf4j
public class RequestHandler{

    public Object handle(RpcRequestBean rpcRequest,Object service){
        Object result = null;
        try {
            result = invokeTargetMethod(rpcRequest,service);
            log.info("服务:{} 成功调用方法:{}", rpcRequest.getInterfaceName(), rpcRequest.getMethodName());
        } catch (InvocationTargetException | IllegalAccessException | ClassNotFoundException e) {
            log.error("调用或发送时有错误发生:", e);
        }
        return  result;
    }

    private Object invokeTargetMethod(RpcRequestBean rpcRequest,Object service) throws IllegalAccessException, InvocationTargetException, ClassNotFoundException {
        Method method;
        try {
            method = service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParamTypes());
        } catch (NoSuchMethodException e) {
            return RpcResponseBean.fail(ResponseCode.METHOD_NOT_FOUND);
        }
        return method.invoke(service, rpcRequest.getParameters());
    }
}