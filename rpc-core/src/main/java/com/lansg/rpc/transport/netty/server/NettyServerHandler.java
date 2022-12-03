package com.lansg.rpc.transport.netty.server;

import com.lansg.rpc.factory.SingletonFactory;
import com.lansg.rpc.handler.RequestHandler;
import com.lansg.rpc.entity.RpcRequestBean;
import com.lansg.rpc.entity.RpcResponseBean;
import com.lansg.rpc.factory.ThreadPoolFactory;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;

/**
* @author: Lansg
* @date: 2022/11/22 20:24
* @Description: Netty中处理RpcRequest的Handler
 *  自定义的Handler需要继承Netty规定好的HandlerAdapter
 *  才能被Netty框架所关联，有点类似SpringMVC的适配器模式
*/
@Slf4j
public class NettyServerHandler extends SimpleChannelInboundHandler<RpcRequestBean> {

    private static RequestHandler requestHandler;
    private  final String THREAD_NAME_PREFIX = "netty-server-handler";
    private  final ExecutorService threadPool;

//    static {
//        requestHandler = new RequestHandler();
//        threadPool = ThreadPoolFactory.createDefaultThreadPool(THREAD_NAME_PREFIX);
//    }
    public NettyServerHandler() {
        this.requestHandler = SingletonFactory.getInstance(RequestHandler.class);
        this.threadPool = ThreadPoolFactory.createDefaultThreadPool(THREAD_NAME_PREFIX);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequestBean msg) throws Exception {
//        try {
//            log.info("服务器接收到请求: {}", msg);
//            String interfaceName = msg.getInterfaceName();
//            Object service = serviceRegistry.getService(interfaceName);
//            Object result = requestHandler.handle(msg, service);
//            ChannelFuture future = ctx.writeAndFlush(RpcResponseBean.success(result, msg.getRequestId()));
//            future.addListener(ChannelFutureListener.CLOSE);
//        } finally {
//            ReferenceCountUtil.release(msg);
//        }
        threadPool.execute(() -> {
            try {
                log.info("服务器接收到请求: {}", msg);
                Object result = requestHandler.handle(msg);
                ChannelFuture future = ctx.writeAndFlush(RpcResponseBean.success(result, msg.getRequestId()));
                future.addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            } finally {
                ReferenceCountUtil.release(msg);
            }
        });
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("处理过程调用时有错误发生:");
        cause.printStackTrace();
        ctx.close();
    }
}
