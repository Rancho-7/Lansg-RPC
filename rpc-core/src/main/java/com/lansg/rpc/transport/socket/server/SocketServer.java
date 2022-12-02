package com.lansg.rpc.transport.socket.server;

import com.lansg.rpc.handler.RequestHandler;
import com.lansg.rpc.hook.ShutdownHook;
import com.lansg.rpc.provider.ServiceProvider;
import com.lansg.rpc.provider.ServiceProviderImpl;
import com.lansg.rpc.registry.NacosServiceRegistry;
import com.lansg.rpc.transport.RpcProvider;
import com.lansg.rpc.enumeration.RpcError;
import com.lansg.rpc.exception.RpcException;
import com.lansg.rpc.registry.ServiceRegistry;
import com.lansg.rpc.serializer.CommonSerializer;
import com.lansg.rpc.factory.ThreadPoolFactory;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
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
//    private final ExecutorService threadPool;
//    private final ServiceRegistry serviceRegistry;
    private final ExecutorService threadPool;;
    private final String host;
    private final int port;
    private CommonSerializer serializer;
    private RequestHandler requestHandler = new RequestHandler();
    private final ServiceRegistry serviceRegistry;
    private final ServiceProvider serviceProvider;

    public SocketServer(String host,int port){
//        this.serviceRegistry = serviceRegistry;
//        BlockingQueue<Runnable> workingQueue = new ArrayBlockingQueue<>(BLOCKING_QUEUE_CAPACITY);
//        ThreadFactory threadFactory = Executors.defaultThreadFactory();
//        threadPool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.SECONDS, workingQueue, threadFactory);
        threadPool = ThreadPoolFactory.createDefaultThreadPool("socket-rpc-server");
        this.port=port;
        this.host=host;
        this.serviceRegistry=new NacosServiceRegistry();
        this.serviceProvider=new ServiceProviderImpl();
    }

    @Override
    public <T> void publishService(T service, Class<T> serviceClass) {
        if(serializer == null) {
            log.error("未设置序列化器");
            throw new RpcException(RpcError.SERIALIZER_NOT_FOUND);
        }
        serviceProvider.addServiceProvider(service,serviceClass);
        serviceRegistry.register(serviceClass.getCanonicalName(), new InetSocketAddress(host, port));
        start();
    }

    @Override
    public void start(){
        try (ServerSocket serverSocket=new ServerSocket()){
            serverSocket.bind(new InetSocketAddress(host,port));
            log.info("服务器启动...");
            ShutdownHook.getShutdownHook().addClearAllHook();
            Socket socket;
            while ((socket = serverSocket.accept())!=null){
                log.info("消费者连接: {}:{}", socket.getInetAddress(), socket.getPort());
                threadPool.execute(new SocketRequestHandlerThread(socket, requestHandler,serializer));
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
