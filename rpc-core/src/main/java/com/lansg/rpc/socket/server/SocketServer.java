package com.lansg.rpc.socket.server;

import com.lansg.rpc.RequestHandler;
import com.lansg.rpc.RpcProvider;
import com.lansg.rpc.enumeration.RpcError;
import com.lansg.rpc.exception.RpcException;
import com.lansg.rpc.registry.ServiceRegistry;
import com.lansg.rpc.serializer.CommonSerializer;
import com.lansg.rpc.util.ThreadPoolFactory;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

/**
* @author: Lansg
* @date: 2022/11/22 21:10
* @Description: Socket方式远程方法调用的提供者（服务端）
*/
@Slf4j
public class SocketServer implements RpcProvider {
//    private static final int CORE_POOL_SIZE = 5;
//    private static final int MAXIMUM_POOL_SIZE = 50;
//    private static final int KEEP_ALIVE_TIME = 60;
//    private static final int BLOCKING_QUEUE_CAPACITY = 100;
    private final ExecutorService threadPool;
    private RequestHandler requestHandler = new RequestHandler();
    private final ServiceRegistry serviceRegistry;
    private CommonSerializer serializer;

    public SocketServer(ServiceRegistry serviceRegistry){
        this.serviceRegistry = serviceRegistry;
//        BlockingQueue<Runnable> workingQueue = new ArrayBlockingQueue<>(BLOCKING_QUEUE_CAPACITY);
//        ThreadFactory threadFactory = Executors.defaultThreadFactory();
//        threadPool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.SECONDS, workingQueue, threadFactory);
        threadPool = ThreadPoolFactory.createDefaultThreadPool("socket-rpc-server");
    }


    @Override
    public void start(int port){
        try (ServerSocket serverSocket=new ServerSocket(port)){
            if(serializer == null) {
                log.error("未设置序列化器");
                throw new RpcException(RpcError.SERIALIZER_NOT_FOUND);
            }
            log.info("服务器启动...");
            Socket socket;
            while ((socket = serverSocket.accept())!=null){
                log.info("消费者连接: {}:{}", socket.getInetAddress(), socket.getPort());
                threadPool.execute(new RequestHandlerThread(socket, requestHandler, serviceRegistry,serializer));
            }
            threadPool.shutdown();
        }catch (IOException e){
            log.info("服务器启动时有错误发生:",e);
        }
    }

    @Override
    public void setSerializer(CommonSerializer serializer) {
        this.serializer=serializer;
    }
}
