package com.lansg.rpc.netty.client;

import com.lansg.rpc.RpcConsumer;
import com.lansg.rpc.codec.CommonDecoder;
import com.lansg.rpc.codec.CommonEncoder;
import com.lansg.rpc.entity.RpcRequestBean;
import com.lansg.rpc.entity.RpcResponseBean;
import com.lansg.rpc.enumeration.RpcError;
import com.lansg.rpc.exception.RpcException;
import com.lansg.rpc.serializer.CommonSerializer;
import com.lansg.rpc.serializer.HessianSerializer;
import com.lansg.rpc.serializer.JsonSerializer;
import com.lansg.rpc.serializer.KryoSerializer;
import com.lansg.rpc.util.RpcMessageChecker;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
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
    private static final Bootstrap bootstrap;

    private CommonSerializer serializer;

    public NettyClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    static {
        EventLoopGroup group = new NioEventLoopGroup();
        //创建bootstrap对象，配置参数
        bootstrap = new Bootstrap();
        //设置线程组
        bootstrap.group(group)
                //设置客户端的通道实现类型
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true);
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
    public Object sendRequest(RpcRequestBean rpcRequest) {
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
        AtomicReference<Object> result = new AtomicReference<>(null);
        try {
            //连接服务端
//            ChannelFuture future = bootstrap.connect(host, port).sync();
//            log.info("客户端连接到服务器 {}:{}", host, port);
//            Channel channel = future.channel();
//            if(channel != null) {
            Channel channel = ChannelProvider.get(new InetSocketAddress(host, port), serializer);
            if (channel.isActive()){
                channel.writeAndFlush(rpcRequest).addListener(future1 -> {
                    if(future1.isSuccess()) {
                        log.info(String.format("客户端发送消息: %s", rpcRequest.toString()));
                    } else {
                        log.error("发送消息时有错误发生: ", future1.cause());
                    }
                });
                //对通道关闭进行监听
                channel.closeFuture().sync();
                AttributeKey<RpcResponseBean> key = AttributeKey.valueOf("rpcResponse"+ rpcRequest.getRequestId());
                RpcResponseBean rpcResponse = channel.attr(key).get();
                RpcMessageChecker.check(rpcRequest, rpcResponse);
                result.set(rpcResponse.getData());
//                return rpcResponse.getData();
            }else{
                System.exit(0);
            }
        } catch (InterruptedException e) {
            log.error("发送消息时有错误发生: ", e);
        }
        return result.get();
    }

    @Override
    public void setSerializer(CommonSerializer serializer) {
        this.serializer=serializer;
    }


}