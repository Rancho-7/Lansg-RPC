package com.lansg.rpc.socket.server;

import com.lansg.rpc.RequestHandler;
import com.lansg.rpc.entity.RpcRequestBean;
import com.lansg.rpc.entity.RpcResponseBean;
import com.lansg.rpc.registry.ServiceRegistry;
import com.lansg.rpc.serializer.CommonSerializer;
import com.lansg.rpc.socket.util.ObjectReader;
import com.lansg.rpc.socket.util.ObjectWriter;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.Socket;

/**
* @author: Lansg
* @date: 2022/11/1 21:56
 * 处理RpcRequest的工作线程
*/
@Slf4j
public class RequestHandlerThread implements Runnable{

    private Socket socket;
    private RequestHandler requestHandler;
    private ServiceRegistry serviceRegistry;
    private CommonSerializer serializer;

    public RequestHandlerThread(Socket socket,RequestHandler requestHandler, ServiceRegistry serviceRegistry,CommonSerializer serializer){
        this.socket = socket;
        this.requestHandler = requestHandler;
        this.serviceRegistry = serviceRegistry;
        this.serializer=serializer;
    }

    @Override
    public void run() {
//        try (ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
//             ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream())){
//            RpcRequestBean rpcRequest = (RpcRequestBean) objectInputStream.readObject();
        try (InputStream inputStream = socket.getInputStream();
             OutputStream outputStream = socket.getOutputStream()) {
            RpcRequestBean rpcRequest = (RpcRequestBean) ObjectReader.readObject(inputStream);
            String interfaceName = rpcRequest.getInterfaceName();
            Object service = serviceRegistry.getService(interfaceName);
            Object result = requestHandler.handle(rpcRequest,service);
//            objectOutputStream.writeObject(RpcResponseBean.success(result));
//            objectOutputStream.flush();
            RpcResponseBean<Object> response = RpcResponseBean.success(result);
            ObjectWriter.writeObject(outputStream, response, serializer);
        }catch (IOException e){
            log.error("调用或发送时有错误发生：", e);
        }
    }
}
