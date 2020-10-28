package network.springboot.override;

import network.springboot.netty.HttpRequestDispatcherHandler;
import network.springboot.netty.HttpResponseHandler;
import network.springboot.netty.NettyServer;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.SpringApplication;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.ResourceLoader;


/**
 * @Des:
 * @Author: jiangchuan
 * <p>
 * @Date: 20-10-28
 */
public class SpringApplicationWithNetty extends SpringApplication {


    public SpringApplicationWithNetty(Class<?>... primarySources) {
        super(primarySources);
    }

    public SpringApplicationWithNetty(ResourceLoader resourceLoader, Class<?>... primarySources) {
        super(resourceLoader, primarySources);
    }

    private ConfigurableApplicationContext springContext;
    /**
     * Called after the context has been refreshed.
     * @param context the application context
     * @param args the application arguments
     */
    @Override
    protected void afterRefresh(ConfigurableApplicationContext context, ApplicationArguments args) {
        this.springContext = context;
        try {
            startWebServer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startWebServer() throws Exception{
        NettyServer webServer = new NettyServer(1,10);
        HttpRequestDispatcherHandler httpRequestDispatcherHandler = new HttpRequestDispatcherHandler(springContext);
        HttpResponseHandler httpResponseHandler = new HttpResponseHandler();
        webServer.appendLastInboundHandler(httpRequestDispatcherHandler);
        webServer.appendLastOutboundHandler(httpResponseHandler);

        webServer.start(8080);
    }
}
