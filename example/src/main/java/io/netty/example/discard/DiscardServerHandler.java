/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.netty.example.discard;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;
import org.bouncycastle.pqc.math.linearalgebra.CharUtils;

/**
 * Handles a server-side channel.
 */
public class DiscardServerHandler extends SimpleChannelInboundHandler<Object> {

    /**
     *
     * @param ctx           上下文对象【通道channel、管道pipeline、连接地址】
     * @param msg           客户端发送的数据
     * @throws Exception
     */
    @Override
    public void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        // discard ｜ 读取数据的实现
        //业务操作
        ByteBuf buf = (ByteBuf)msg;
        System.out.println("handler-客户端发送的数据：" + buf.toString(CharsetUtil.UTF_8));

        //应对业务高并发
        //拿到客户端数据  ->   交给封装类  -> 处理数据的解码｜解析  -> 通过业务逻辑处理业务 /
        // （高并发情况下：kafka (mqtt) -> fink）


        System.out.println("handler-客户端地址：" + ctx.channel().remoteAddress());
    }

    /**
     * 数据读取完毕
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
        ctx.writeAndFlush(Unpooled.copiedBuffer("handler-hello客户端",CharsetUtil.UTF_8));
    }

    /**
     * 处理异常
     * @param ctx
     * @param cause
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close();
    }

}
