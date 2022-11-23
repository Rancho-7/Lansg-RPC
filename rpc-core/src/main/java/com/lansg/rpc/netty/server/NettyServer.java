package com.lansg.rpc.netty.server;

import com.lansg.rpc.RpcProvider;
import com.lansg.rpc.codec.CommonDecoder;
import com.lansg.rpc.codec.CommonEncoder;
import com.lansg.rpc.serializer.JsonSerializer;
import com.lansg.rpc.serializer.KryoSerializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

/**
* @author: Lansg
* @date: 2022/11/22 20:17
* @Description: NIO方式服务提供侧
*/
@Slf4j
public class NettyServer implements RpcProvider {
    @Override
    public void start(int port) {
        //创建两个线程组bossGroup、workerGroup
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            //创建服务端的启动对象，设置参数
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            //设置两个线程组boosGroup和workerGroup
            serverBootstrap.group(bossGroup, workerGroup)
                    //设置服务端通道实现类型
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    //设置线程队列得到连接个数
                    .option(ChannelOption.SO_BACKLOG, 256)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    //设置保持活动连接状态
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    //初始化通道对象
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            //给pipeline管道设置处理器
                            ChannelPipeline pipeline = ch.pipeline();
//                            pipeline.addLast(new CommonEncoder(new JsonSerializer()));
                            pipeline.addLast(new CommonEncoder(new KryoSerializer()));
                            pipeline.addLast(new CommonDecoder());
                            pipeline.addLast(new NettyServerHandler());
                        }
                    });
            //绑定端口号，启动服务端
            ChannelFuture future = serverBootstrap.bind(port).sync();
            //对关闭通道进行监听
            future.channel().closeFuture().sync();

        } catch (InterruptedException e) {
            log.error("启动服务器时有错误发生: ", e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
