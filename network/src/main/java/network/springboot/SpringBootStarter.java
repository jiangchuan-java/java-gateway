package network.springboot;

import network.springboot.override.SpringApplicationWithNetty;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @Des:
 * @Author: jiangchuan
 * <p>
 * @Date: 20-10-28
 */
@SpringBootApplication
public class SpringBootStarter {

    public static void main(String[] args) {

        new SpringApplicationWithNetty(SpringBootStarter.class).run(args);
    }
}
