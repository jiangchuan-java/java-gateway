package network.springboot.netty;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Des:
 * @Author: jiangchuan
 * <p>
 * @Date: 20-9-2
 */
@ChannelHandler.Sharable
public class HttpResponseHandler extends ChannelOutboundHandlerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpRequestDispatcherHandler.class);
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        LOGGER.info(Thread.currentThread().getName() + " >>>>>>>> pipeline write");
        ctx.write(msg, promise);
    }

    @Override
    public void flush(ChannelHandlerContext ctx) throws Exception {
        LOGGER.info(Thread.currentThread().getName() + " >>>>>>>> pipeline flush");
        ctx.flush();
    }

}
