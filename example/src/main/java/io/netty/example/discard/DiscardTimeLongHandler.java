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

import java.util.concurrent.TimeUnit;

/**
 * Handles a server-side channel.
 */
public class DiscardTimeLongHandler extends SimpleChannelInboundHandler<Object> {

    /**
     * 通道读取事件
     * @param ctx           上下文对象【通道channel、管道pipeline、连接地址】
     * @param msg           客户端发送的数据
     * @throws Exception
     */
    @Override
    public void channelRead0(final ChannelHandlerContext ctx, Object msg) throws Exception {
        // discard ｜ 读取数据的实现


        //方案（创建普通任务线程）
        ctx.channel().eventLoop().execute(new Runnable() {
            @Override
            public void run() {
                try{
                    Thread.sleep(10 * 1000); //模拟我们长时间处理的业务
                    ctx.writeAndFlush(Unpooled.copiedBuffer("hello-1客户端:",CharsetUtil.UTF_8));
                }catch (Exception e){

                }
            }
        });

        //方案二（用户自定义定时任务）
        ctx.channel().eventLoop().schedule(new Runnable() {
            @Override
            public void run() {
                try{
                    Thread.sleep(10 * 1000);
                    ctx.writeAndFlush(Unpooled.copiedBuffer("hello-2客户端:",CharsetUtil.UTF_8));
                }catch (Exception e){

                }
            }
        },5, TimeUnit.SECONDS);




        // 通过Handler之外的代码进行系统调用我们通道进行添加任务

        ByteBuf buf = (ByteBuf)msg;
        System.out.printf("time-out-客户端发送的数据：" + buf.toString(CharsetUtil.UTF_8));
        System.out.printf("time-out-客户端地址：" + ctx.channel().remoteAddress());
    }

    /**
     * 数据读取完毕
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
        ctx.writeAndFlush(Unpooled.copiedBuffer("time-out-hello客户端",CharsetUtil.UTF_8));
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
