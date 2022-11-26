package com.lansg.rpc.netty.client;

import com.lansg.rpc.entity.RpcResponseBean;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

/**
* @author: Lansg
* @date: 2022/11/22 20:04
* @Description: Netty客户端侧处理器
*/
@Slf4j
public class NettyClientHandler extends SimpleChannelInboundHandler<RpcResponseBean> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponseBean msg) throws Exception {
        try {
            log.info(String.format("客户端接收到消息: %s", msg));
            AttributeKey<RpcResponseBean> key = AttributeKey.valueOf("rpcResponse"+ msg.getRequestId());
            ctx.channel().attr(key).set(msg);
            ctx.channel().close();
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
}
