package com.lansg.rpc.consumer;

import com.lansg.rpc.entity.RpcRequestBean;
import com.lansg.rpc.entity.RpcResponseBean;
import com.lansg.rpc.enumeration.ResponseCode;
import com.lansg.rpc.enumeration.RpcError;
import com.lansg.rpc.exception.RpcException;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
* @author: Lansg
* @date: 2022/10/28 17:54
*/
@Slf4j
public class RpcConsumer {
    public Object sendRequest(RpcRequestBean rpcRequest,String host,int port){
        try (Socket socket=new Socket(host,port)){
            ObjectOutputStream objectOutputStream=new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream objectInputStream=new ObjectInputStream(socket.getInputStream());
            objectOutputStream.writeObject(rpcRequest);
            //flush()方法用于刷新此流，并将任何缓冲输出的字节立即写入基础流。
            objectOutputStream.flush();
           RpcResponseBean rpcResponse = (RpcResponseBean) objectInputStream.readObject();
           if (rpcResponse == null){
               log.error("服务调用失败，service：{}",rpcRequest.getInterfaceName());
               throw new RpcException(RpcError.SERVICE_INVOCATION_FAILURE, " service:" + rpcRequest.getInterfaceName());
           }
            if(rpcResponse.getCode() == null || rpcResponse.getCode() != ResponseCode.SUCCESS.getCode()) {
                log.error("调用服务失败, service: {}, response:{}", rpcRequest.getInterfaceName(), rpcResponse);
                throw new RpcException(RpcError.SERVICE_INVOCATION_FAILURE, " service:" + rpcRequest.getInterfaceName());
            }
            return rpcResponse.getData();
        }catch (IOException | ClassNotFoundException e){
            log.info("调用有错误发生:",e);
            throw new RpcException("服务调用失败:",e);
        }
    }
}
