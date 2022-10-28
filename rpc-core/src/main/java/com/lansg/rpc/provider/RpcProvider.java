package com.lansg.rpc.provider;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

/**
* @author: Lansg
* @date: 2022/10/28 23:36
*/
@Slf4j
public class RpcProvider {
    private final ExecutorService threadPool;

    public RpcProvider(){
        int corePoolSize=5;
        int maximumPoolSize=50;
        long keepAliveTime=60;
        BlockingQueue<Runnable> workingQueue=new ArrayBlockingQueue<>(100);
        ThreadFactory threadFactory= Executors.defaultThreadFactory();
        threadPool=new ThreadPoolExecutor(corePoolSize,maximumPoolSize,keepAliveTime,TimeUnit.SECONDS,workingQueue,threadFactory);
    }

    public void register(Object service,int port){
        try (ServerSocket serverSocket=new ServerSocket(port)){
            log.info("服务器正在启动...");
            Socket socket;
            while ((socket = serverSocket.accept())!=null){
                log.info("客户端连接！Ip为:"+socket.getInetAddress());
                threadPool.execute(new WorkerThread(socket,service));
            }
        }catch (IOException e){
            log.info("连接时有错误发生:",e);
        }
    }
}
