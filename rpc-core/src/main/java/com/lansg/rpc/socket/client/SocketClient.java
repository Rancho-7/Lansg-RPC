package com.lansg.rpc.socket.client;

import com.lansg.rpc.RpcConsumer;
import com.lansg.rpc.entity.RpcRequestBean;
import com.lansg.rpc.entity.RpcResponseBean;
import com.lansg.rpc.enumeration.ResponseCode;
import com.lansg.rpc.enumeration.RpcError;
import com.lansg.rpc.exception.RpcException;
import com.lansg.rpc.serializer.CommonSerializer;
import com.lansg.rpc.socket.util.ObjectReader;
import com.lansg.rpc.socket.util.ObjectWriter;
import com.lansg.rpc.util.RpcMessageChecker;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.Socket;

/**
* @author: Lansg
* @date: 2022/11/22 21:02
* @Description: Socket方式远程方法调用的消费者（客户端）
*/
@Slf4j
public class SocketClient implements RpcConsumer {
    private final String host;
    private final int port;

    private CommonSerializer serializer;

    public SocketClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public Object sendRequest(RpcRequestBean rpcRequest) {
        if (serializer==null){
            log.error("未设置序列化器");
            throw new RpcException(RpcError.SERIALIZER_NOT_FOUND);
        }
        try (Socket socket = new Socket(host, port)) {
//            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
//            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
//            objectOutputStream.writeObject(rpcRequest);
//            objectOutputStream.flush();
//            RpcResponseBean rpcResponse = (RpcResponseBean) objectInputStream.readObject();
            OutputStream outputStream = socket.getOutputStream();
            InputStream inputStream = socket.getInputStream();
            ObjectWriter.writeObject(outputStream,rpcRequest,serializer);
            Object obj = ObjectReader.readObject(inputStream);
            RpcResponseBean rpcResponse = (RpcResponseBean) obj;
            if(rpcResponse == null) {
                log.error("服务调用失败，service：{}", rpcRequest.getInterfaceName());
                throw new RpcException(RpcError.SERVICE_INVOCATION_FAILURE, " service:" + rpcRequest.getInterfaceName());
            }
            if(rpcResponse.getCode() == null || rpcResponse.getCode() != ResponseCode.SUCCESS.getCode()) {
                log.error("调用服务失败, service: {}, response:{}", rpcRequest.getInterfaceName(), rpcResponse);
                throw new RpcException(RpcError.SERVICE_INVOCATION_FAILURE, " service:" + rpcRequest.getInterfaceName());
            }
            RpcMessageChecker.check(rpcRequest, rpcResponse);
            return rpcResponse.getData();
        } catch (IOException e) {
            log.error("调用时有错误发生：", e);
            throw new RpcException("服务调用失败: ", e);
        }
    }

    @Override
    public void setSerializer(CommonSerializer serializer) {
        this.serializer=serializer;
    }
}
