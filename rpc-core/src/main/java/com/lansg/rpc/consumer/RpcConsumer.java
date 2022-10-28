package com.lansg.rpc.consumer;

import com.lansg.rpc.entity.RpcRequestBean;
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
            objectOutputStream.flush();
            return objectInputStream.readObject();
        }catch (IOException | ClassNotFoundException e){
            log.info("调用有错误发生:",e);
            return null;
        }
    }
}
