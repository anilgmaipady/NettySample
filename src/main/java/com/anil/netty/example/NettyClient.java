package com.anil.netty.example;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class NettyClient {

    static class Task implements Runnable {

        @Override
        public void run() {
            while (true) {
                String host = "localhost";
                int port = 8080;
                EventLoopGroup workerGroup = new NioEventLoopGroup();

                try {
                    Bootstrap b = new Bootstrap();
                    b.group(workerGroup);
                    b.channel(NioSocketChannel.class);
                    b.option(ChannelOption.SO_KEEPALIVE, true);
                    b.handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new RequestDataEncoder(), new ResponseDataDecoder(), new ClientHandler());
                        }
                    });

                    ChannelFuture f = null;
                    try {
                        f = b.connect(host, port).sync();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    try {
                        f.channel().closeFuture().sync();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } finally {
                    workerGroup.shutdownGracefully();
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(8);
        executorService.submit(new Task());
        executorService.submit(new Task());
        executorService.submit(new Task());
        executorService.submit(new Task());
        executorService.submit(new Task());
        executorService.submit(new Task());
        executorService.submit(new Task());
        executorService.submit(new Task());
    }
}
