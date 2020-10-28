package network.springboot.netty;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;


/**
 * @Des:
 * @Author: jiangchuan
 * <p>
 * @Date: 20-9-2
 */
@ChannelHandler.Sharable
public class HttpRequestDispatcherHandler extends ChannelInboundHandlerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpRequestDispatcherHandler.class);


    private ConfigurableApplicationContext springApplicationContext;

    public HttpRequestDispatcherHandler(ConfigurableApplicationContext springApplicationContext) {
        this.springApplicationContext = springApplicationContext;
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        LOGGER.info(Thread.currentThread().getName() + " >>>>>>>> read");
        if (msg instanceof FullHttpRequest) {
            FullHttpRequest httpRequest = (FullHttpRequest) msg;
            LOGGER.info(Thread.currentThread().getName() + " >>>>>>>> uri : {}", httpRequest.uri());
            dispatch(ctx, httpRequest);
        }
    }


    /**
     * 处理具体的uri
     *
     * @param ctx netty的channel上下文
     * @param httpRequest netty的请求对象
     */
    public void dispatch(ChannelHandlerContext ctx, FullHttpRequest httpRequest){


    }



    /**
     * uri找不到时，404
     *
     * @param ctx
     * @param result
     */
    private void do404Response(ChannelHandlerContext ctx, String result) {
        LOGGER.info(Thread.currentThread().getName()+ " >>>>>>>> response");
        // 1.设置响应


        FullHttpResponse resp = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                HttpResponseStatus.NOT_FOUND);


        resp.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");
        resp.headers().set(HttpHeaderNames.CONTENT_LENGTH, resp.content().readableBytes());

        // 2.发送
        // 注意必须在使用完之后，close channel
        //ctx.writeAndFlush(resp); //这种方式是找next()，然后进行调用
        ctx.channel().writeAndFlush(resp); //这种方式是从tail开始，从后往前进行调用
    }
}