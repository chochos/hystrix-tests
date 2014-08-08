package plain;

import java.net.Socket;
import java.io.PrintStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Client implements java.util.concurrent.Callable<String> {

    private final String line;
    private final String host;
    private final int port;
    private long t0;
    private long t1;
    private boolean ok;

    public Client(String line, String host, int port) {
        this.line = line;
        this.host = host;
        this.port = port;
    }

    public String call() throws Exception {
        t0 = System.currentTimeMillis();
        try (Socket sock = new Socket(host, port);
             PrintStream pout = new PrintStream(sock.getOutputStream());
             BufferedReader reader = new BufferedReader(new InputStreamReader(sock.getInputStream()))) {
            sock.setSoTimeout(3000);
            pout.println(line);
            String result=reader.readLine();
            ok=true;
            return result;
        } finally {
            t1 = System.currentTimeMillis();
        }
    }

}
