package network.springboot.netty;

import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.cors.CorsConfig;
import io.netty.handler.codec.http.cors.CorsConfigBuilder;
import io.netty.handler.codec.http.cors.CorsHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * @Des:
 * @Author: jiangchuan
 * <p>
 * @Date: 20-8-27
 */
public class NettyServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(NettyServer.class);

    private List<ChannelInboundHandlerAdapter> inboundHandlerAdapterList = new LinkedList<>();

    private List<ChannelOutboundHandlerAdapter> outboundHandlerAdapterList = new LinkedList<>();

    private int nBossThreads;

    private int nWorkerThreads;

    public NettyServer() throws Exception {
        this(1, 10);
    }

    public NettyServer(int nBossThreads, int nWorkerThreads) {
        this.nBossThreads = nBossThreads;
        this.nWorkerThreads = nWorkerThreads;

    }

    public void appendLastInboundHandler(ChannelInboundHandlerAdapter inboundHandlerAdapter){
        inboundHandlerAdapterList.add(inboundHandlerAdapter);
    }

    public void appendLastOutboundHandler(ChannelOutboundHandlerAdapter outboundHandlerAdapter) {
        outboundHandlerAdapterList.add(outboundHandlerAdapter);
    }


    /**
     * 启动netty监听服务
     */
    public void start(int port) throws Exception {
        final NioEventLoopGroup bossSelecors = new NioEventLoopGroup(nBossThreads);
        final NioEventLoopGroup workerSelectors = new NioEventLoopGroup(nWorkerThreads);

        NioServerSocketChannel serverSocketChannel = new NioServerSocketChannel();
        bossSelecors.register(serverSocketChannel);
        serverSocketChannel.pipeline().addLast(new AcceptHandler(workerSelectors));

        serverSocketChannel.bind(new InetSocketAddress(port)).sync();

        LOGGER.info(" >>>>>>>> netty server started, listen : {}", port);
    }


    /**
     * accept入口
     */
    protected class AcceptHandler extends ChannelInboundHandlerAdapter {

        private NioEventLoopGroup workerSelector;

        public AcceptHandler(NioEventLoopGroup workerSelector) {
            this.workerSelector = workerSelector;
        }

        @Override
        public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
            ctx.fireChannelRegistered();
        }

        /**
         * 请求进来，从最上层走到中间
         * 响应出去，从最下层走到中间
         * @param ctx
         * @param msg
         * @throws Exception
         */
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            CorsConfig corsConfig = CorsConfigBuilder.forAnyOrigin().allowNullOrigin().allowCredentials().build();
            NioSocketChannel client = (NioSocketChannel) msg;
            LOGGER.info(Thread.currentThread().getName() + " >>>>>>>> accept from " + client.remoteAddress().toString());
            // ----------inbound------------
            client.pipeline().addLast(new HttpRequestDecoder());
            client.pipeline().addLast(new HttpObjectAggregator(65536));
            for (ChannelInboundHandlerAdapter inboundHandler : inboundHandlerAdapterList) {
                client.pipeline().addLast(inboundHandler);
            }

            // ----------Duplex------------
            client.pipeline().addLast(new CorsHandler(corsConfig)); /*Duplex 双工*/

            // ----------outbound------------
            client.pipeline().addLast(new HttpResponseEncoder());
            for (ChannelOutboundHandlerAdapter outboundHandler : outboundHandlerAdapterList) {
                client.pipeline().addLast(outboundHandler);
            }
            workerSelector.register(client);
        }

    }

}
