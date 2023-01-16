package com.waylau.netty.demo.codec;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * My Client.
 *
 * @author <a href="https://waylau.com">Way Lau</a>
 * @since 1.0.0 2019年12月16日
 */
public class MyClient {

    private final String host;
    private final int port;

    public MyClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public static void main(String[] args) throws InterruptedException {
        new MyClient("localhost", 8082).run();
    }

    public void run() throws InterruptedException {

        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            Bootstrap b = new Bootstrap();
            b.group(workerGroup);
            b.channel(NioSocketChannel.class);
            b.option(ChannelOption.SO_KEEPALIVE, true);
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast("codec", new MyCodec());
                    ch.pipeline().addLast(new MyClientHandler());

                }
            });

            // 启动客户端
            ChannelFuture f = b.connect(host, port).sync();

            while (true) {

                // 发送消息给服务器
                Msg msg = new Msg();
                MsgHeader msgHeader = new MsgHeader();
                msgHeader.setMsgType(MsgType.EMGW_LOGIN_REQ.getValue());
                String body = "床前明月光，疑是地上霜。举头望明月，低头思故乡。";

                byte[] bodyBytes = body.getBytes(StandardCharsets.UTF_8);
                int bodySize = bodyBytes.length;
                msgHeader.setLen(bodySize);
                msg.setMsgHeader(msgHeader);
                msg.setBody(body);

                f.channel().writeAndFlush(msg);
                Thread.sleep(2000);
            }
        } finally {
            workerGroup.shutdownGracefully();
        }
    }

}
