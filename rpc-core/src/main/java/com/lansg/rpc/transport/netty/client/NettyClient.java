package com.lansg.rpc.transport.netty.client;

import com.lansg.rpc.factory.SingletonFactory;
import com.lansg.rpc.loadbalancer.LoadBalancer;
import com.lansg.rpc.loadbalancer.RandomLoadBalancer;
import com.lansg.rpc.registry.NacosServiceDiscovery;
import com.lansg.rpc.registry.NacosServiceRegistry;
import com.lansg.rpc.registry.ServiceDiscovery;
import com.lansg.rpc.registry.ServiceRegistry;
import com.lansg.rpc.transport.RpcConsumer;
import com.lansg.rpc.entity.RpcRequestBean;
import com.lansg.rpc.entity.RpcResponseBean;
import com.lansg.rpc.enumeration.RpcError;
import com.lansg.rpc.exception.RpcException;
import com.lansg.rpc.serializer.CommonSerializer;
import com.lansg.rpc.util.RpcMessageChecker;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

/**
* @author: Lansg
* @date: 2022/11/22 20:14
* @Description: NIO方式消费侧客户端类
*/
@Slf4j
public class NettyClient implements RpcConsumer {

    private String host;
    private int port;
    private static final EventLoopGroup group;
    private static final Bootstrap bootstrap;
    private final ServiceDiscovery serviceDiscovery;

    private final CommonSerializer serializer;
    private final UnprocessedRequests unprocessedRequests;

    public NettyClient(){
        this(DEFAULT_SERIALIZER,new RandomLoadBalancer());
    }

    public NettyClient(Integer serializer){
        this(serializer,new RandomLoadBalancer());
    }

    public NettyClient(LoadBalancer loadBalancer){
        this(DEFAULT_SERIALIZER,loadBalancer);
    }

    public NettyClient(Integer serializer, LoadBalancer loadBalancer){
        this.serviceDiscovery = new NacosServiceDiscovery(loadBalancer);
        this.serializer=CommonSerializer.getByCode(serializer);
        this.unprocessedRequests = SingletonFactory.getInstance(UnprocessedRequests.class);
    }

    static {
//        EventLoopGroup group = new NioEventLoopGroup();
        group = new NioEventLoopGroup();
        //创建bootstrap对象，配置参数
        bootstrap = new Bootstrap();
        //设置线程组
        bootstrap.group(group)
                .channel(NioSocketChannel.class);
                //设置客户端的通道实现类型
//                .channel(NioSocketChannel.class)
//                .option(ChannelOption.SO_KEEPALIVE, true);
                //初始化通道
//                .handler(new ChannelInitializer<SocketChannel>() {
//                    @Override
//                    protected void initChannel(SocketChannel ch) throws Exception {
                        //添加客户端通道的处理器
//                        ChannelPipeline pipeline = ch.pipeline();
//                        pipeline.addLast(new CommonDecoder())
//                                .addLast(new CommonEncoder(new JsonSerializer()))
//                                .addLast(new CommonEncoder(new KryoSerializer()))
//                                .addLast(new CommonEncoder(new HessianSerializer()))
//                                .addLast(new NettyClientHandler());
//                    }
//                });
    }

    @Override
    public CompletableFuture<RpcResponseBean> sendRequest(RpcRequestBean rpcRequest) {
        if (serializer == null){
            log.info("未设置序列化器");
            throw new RpcException(RpcError.SERIALIZER_NOT_FOUND);
        }
//        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
//            @Override
//            protected void initChannel(SocketChannel ch) throws Exception {
//                ChannelPipeline pipeline = ch.pipeline();
//                pipeline.addLast(new CommonDecoder())
//                        .addLast(new CommonEncoder(serializer))
//                        .addLast(new NettyClientHandler());
//            }
//        });
//        AtomicReference<Object> result = new AtomicReference<>(null);
        CompletableFuture<RpcResponseBean> resultFuture = new CompletableFuture<>();
        try {
            //连接服务端
//            ChannelFuture future = bootstrap.connect(host, port).sync();
//            log.info("客户端连接到服务器 {}:{}", host, port);
//            Channel channel = future.channel();
//            if(channel != null) {
            InetSocketAddress inetSocketAddress = serviceDiscovery.lookupService(rpcRequest.getInterfaceName());
            Channel channel = ChannelProvider.get(inetSocketAddress, serializer);
//            if (channel.isActive()){
//                channel.writeAndFlush(rpcRequest).addListener(future1 -> {
//                    if(future1.isSuccess()) {
//                        log.info(String.format("客户端发送消息: %s", rpcRequest.toString()));
//                    } else {
//                        log.error("发送消息时有错误发生: ", future1.cause());
//                    }
//                });
//                //对通道关闭进行监听
//                channel.closeFuture().sync();
//                AttributeKey<RpcResponseBean> key = AttributeKey.valueOf("rpcResponse"+ rpcRequest.getRequestId());
//                RpcResponseBean rpcResponse = channel.attr(key).get();
//                RpcMessageChecker.check(rpcRequest, rpcResponse);
//                result.set(rpcResponse.getData());
////                return rpcResponse.getData();
//            }else{
//                channel.close();
//                System.exit(0);
//            }
            if (!channel.isActive()) {
                group.shutdownGracefully();
                return null;
            }
            unprocessedRequests.put(rpcRequest.getRequestId(),resultFuture);
            channel.writeAndFlush(rpcRequest).addListener((ChannelFutureListener)future1 -> {
                if (future1.isSuccess()) {
                    log.info(String.format("客户端发送消息: %s", rpcRequest.toString()));
                } else {
                    future1.channel().close();
                    resultFuture.completeExceptionally(future1.cause());
                    log.error("发送消息时有错误发生: ", future1.cause());
                }
            });
//            channel.closeFuture().sync();
//            AttributeKey<RpcResponseBean> key = AttributeKey.valueOf("rpcResponse" + rpcRequest.getRequestId());
//            RpcResponseBean rpcResponse = channel.attr(key).get();
//            RpcMessageChecker.check(rpcRequest, rpcResponse);
//            result.set(rpcResponse.getData());
        } catch (InterruptedException e) {
//            log.error("发送消息时有错误发生: ", e);
            unprocessedRequests.remove(rpcRequest.getRequestId());
            log.error(e.getMessage(),e);
            Thread.currentThread().interrupt();
        }
        return resultFuture;
    }

//    @Override
//    public void setSerializer(CommonSerializer serializer) {
//        this.serializer=serializer;
//    }


}