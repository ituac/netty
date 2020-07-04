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

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;

/**
 * Discards any incoming data.
 */
public final class DiscardServer {

    static final boolean SSL = System.getProperty("ssl") != null;
    static final int PORT = Integer.parseInt(System.getProperty("port", "8009"));

    public static void main(String[] args) throws Exception {
        // Configure SSL.

        final SslContext sslCtx;
        if (SSL) {
            SelfSignedCertificate ssc = new SelfSignedCertificate();
            sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
        } else {
            sslCtx = null;
        }



        //创建两个线程组 bossGroup（只是处理连接请求） workerGroup（真正的与客户端进行业务处理）
        //EventLoopGroup本身都是一直循环 非守护线程 一直循环
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);

        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            //创建服务器端启动，并进行启动参数配置
            ServerBootstrap b = new ServerBootstrap();

            //链式编程设置线程组及其他配置信息
            b.group(bossGroup, workerGroup)
             .channel(NioServerSocketChannel.class)     //使用NioServerSocketChannel作为服务器通道实现

             .option(ChannelOption.SO_BACKLOG,100)  //设置线程队列得到连接个数
             .childOption(ChannelOption.SO_KEEPALIVE,true)      //保持活动连接状态

             .handler(new LoggingHandler(LogLevel.INFO))    //设置我们Boss中的Handler处理器

             // 创建一个通道
             .childHandler(new ChannelInitializer<SocketChannel>() {
                 @Override
                 public void initChannel(SocketChannel ch) {
                     //得到channel关联的pipeline
                     ChannelPipeline p = ch.pipeline();

                     if (sslCtx != null) {
                         p.addLast(sslCtx.newHandler(ch.alloc()));
                     }

                     //向管道最后增加我们需要的处理器
                     //p.addLast(new DiscardServerHandler());
                     p.addLast(new DiscardTimeLongHandler());
                 }
             });
             // 创建一个通道


            // 绑定一个端口，生一个ChannelFuture对象（ChannelFuture） ChannelFuture：异步模型
            ChannelFuture f = b.bind(PORT).sync();
            /*
            f.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if(future.isSuccess()){
                        System.out.printf("监听端口成功");
                    }else {
                        System.out.printf("监听端口失败");
                    }
                }
            });*/

            // Wait until the server socket is closed.
            // In this example, this does not happen, but you can do that to gracefully
            // shut down your server.对关闭通道进行监听
            f.channel().closeFuture().sync();
        } finally {
            //shutdownGracefully Netty优雅退出接口
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}
