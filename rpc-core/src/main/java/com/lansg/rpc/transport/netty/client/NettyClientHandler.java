package com.lansg.rpc.transport.netty.client;

import com.lansg.rpc.entity.RpcRequestBean;
import com.lansg.rpc.entity.RpcResponseBean;
import com.lansg.rpc.factory.SingletonFactory;
import com.lansg.rpc.serializer.CommonSerializer;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.AttributeKey;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

/**
* @author: Lansg
* @date: 2022/11/22 20:04
* @Description: Netty客户端侧处理器
*/
@Slf4j
public class NettyClientHandler extends SimpleChannelInboundHandler<RpcResponseBean> {


    private final UnprocessedRequests unprocessedRequests;

    public NettyClientHandler() {
        this.unprocessedRequests = SingletonFactory.getInstance(UnprocessedRequests.class);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponseBean msg) throws Exception {
        try {
            log.info(String.format("客户端接收到消息: %s", msg));
//            AttributeKey<RpcResponseBean> key = AttributeKey.valueOf("rpcResponse"+ msg.getRequestId());
//            ctx.channel().attr(key).set(msg);
//            ctx.channel().close();
            unprocessedRequests.complete(msg);
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("过程调用时有错误发生:");
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.WRITER_IDLE) {
                log.info("发送心跳包 [{}]", ctx.channel().remoteAddress());
                Channel channel = ChannelProvider.get((InetSocketAddress) ctx.channel().remoteAddress(), CommonSerializer.getByCode(CommonSerializer.DEFAULT_SERIALIZER));
                RpcRequestBean rpcRequest = new RpcRequestBean();
                rpcRequest.setHeartBeat(true);
                channel.writeAndFlush(rpcRequest).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
