package com.zsq.websocket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;

public class ChatServer {
	public void run(int port) throws InterruptedException{
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try{
		ServerBootstrap b = new ServerBootstrap();
		b.group(bossGroup,workerGroup).channel(NioServerSocketChannel.class)
		.childHandler(new ChannelInitializer<SocketChannel>() {
			protected void initChannel(SocketChannel arg0) throws Exception {
				ChannelPipeline pipeline = arg0.pipeline();
				pipeline.addLast("http-codec", new HttpServerCodec());
				pipeline.addLast("aggregator", new HttpObjectAggregator(65536));
				pipeline.addLast("http-chunked", new ChunkedWriteHandler());
				pipeline.addLast("handler", new ServerHandler());
			};
		}).option(ChannelOption.SO_BACKLOG, 1024);
		Channel ch = b.bind(port).sync().channel();
		System.out.println("websocket server started at port:"+port+".");
		ch.closeFuture().sync();
		}
		finally{
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}
	
	public static void main(String [] args) throws InterruptedException{
		int port = 8080;
		new ChatServer().run(port);
	}
}
