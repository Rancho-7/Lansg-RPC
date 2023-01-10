package com.lansg.rpc.transport.netty.client;

import com.lansg.rpc.codec.CommonDecoder;
import com.lansg.rpc.codec.CommonEncoder;
import com.lansg.rpc.enumeration.RpcError;
import com.lansg.rpc.exception.RpcException;
import com.lansg.rpc.serializer.CommonSerializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.*;

/**
* @author: Lansg
* @date: 2022/11/27 18:18
* @Description: 用于获取Channel对象
*/
@Slf4j
public class ChannelProvider {

    private static EventLoopGroup eventLoopGroup;
    private static Bootstrap bootstrap = initializeBootstrap() ;

//    private static final int MAX_RETRY_COUNT = 5;
//    private static Channel channel = null;
    private static Map<String,Channel> channels = new ConcurrentHashMap<>();

    public static Channel get(InetSocketAddress inetSocketAddress, CommonSerializer serializer) throws InterruptedException {
        String key = inetSocketAddress.toString() + serializer.getCode();
        if (channels.containsKey(key)){
            Channel channel = channels.get(key);
            if (channels != null && channel.isActive()){
                return channel;
            }else{
                channels.remove(key);
            }
        }
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) {
                /*自定义序列化编解码器*/
                // RpcResponse -> ByteBuf
                ch.pipeline().addLast(new CommonEncoder(serializer))
                        .addLast(new IdleStateHandler(0, 5, 0, TimeUnit.SECONDS))
                        .addLast(new CommonDecoder())
                        .addLast(new NettyClientHandler());
            }
        });
//        CountDownLatch countDownLatch = new CountDownLatch(1);
        Channel channel = null;
        try {
            connect(bootstrap, inetSocketAddress);
//            countDownLatch.await();
            channel = connect(bootstrap,inetSocketAddress);
        } catch (ExecutionException e) {
            log.error("获取channel时有错误发生:", e);
            return null;
        }
        channels.put(key,channel);
        return channel;
    }

//    private static void connect(Bootstrap bootstrap, InetSocketAddress inetSocketAddress, CountDownLatch countDownLatch) {
//        connect(bootstrap, inetSocketAddress, MAX_RETRY_COUNT, countDownLatch);
//    }

    private static Channel connect(Bootstrap bootstrap, InetSocketAddress inetSocketAddress) throws ExecutionException, InterruptedException {
        CompletableFuture<Channel> completableFuture = new CompletableFuture<>();
        bootstrap.connect(inetSocketAddress).addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                log.info("客户端连接成功!");
//                channel = future.channel();
//                countDownLatch.countDown();
//                return;
//            }
//            if (retry == 0) {
//                log.error("客户端连接失败:重试次数已用完，放弃连接！");
//                countDownLatch.countDown();
//                throw new RpcException(RpcError.CLIENT_CONNECT_SERVER_FAILURE);
                completableFuture.complete(future.channel());
            }else{
                throw new IllegalStateException();
            }
            // 第几次重连
//            int order = (MAX_RETRY_COUNT - retry) + 1;
            // 本次重连的间隔
//            int delay = 1 << order;
//            log.error("{}: 连接失败，第 {} 次重连……", new Date(), order);
//            bootstrap.config().group().schedule(() -> connect(bootstrap, inetSocketAddress, retry - 1, countDownLatch), delay, TimeUnit
//                    .SECONDS);
        });
        return completableFuture.get();
    }

    private static Bootstrap initializeBootstrap() {
        eventLoopGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                //连接的超时时间，超过这个时间还是建立不上的话则代表连接失败
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                //是否开启 TCP 底层心跳机制
                .option(ChannelOption.SO_KEEPALIVE, true)
                //TCP默认开启了 Nagle 算法，该算法的作用是尽可能的发送大数据快，减少网络传输。TCP_NODELAY 参数的作用就是控制是否启用 Nagle 算法。
                .option(ChannelOption.TCP_NODELAY, true);
        return bootstrap;
    }
}
