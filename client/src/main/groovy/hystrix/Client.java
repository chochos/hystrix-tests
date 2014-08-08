package hystrix;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;

import java.net.Socket;
import java.io.PrintStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Client extends HystrixCommand<String> {

    private final plain.Client worker;

    public Client(plain.Client c) {
        super(HystrixCommandGroupKey.Factory.asKey("test"));
        this.worker=c;
    }

    protected String run() throws Exception {
        return worker.call();
    }
    protected String getFallback() {
        return "ERROR";
    }

}
