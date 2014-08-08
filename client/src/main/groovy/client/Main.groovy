package client;

import java.util.concurrent.Future
import java.util.concurrent.TimeUnit

class Main {

    static final Random rng = new Random(System.currentTimeMillis())
    static final List<String> names = ['Charles', 'Dave', 'Eve', 'Fiona', 'Gavin', 'Hilary', 'Whatever', 'Alice', 'Bob']
    static final int RUNS = 100

    static void printResults(String strategy, Closure closure) {
        println "********** RUNNING $strategy *************"
        long t0 = System.currentTimeMillis()
        List<String> results = closure()
        long t1 = System.currentTimeMillis()
        List<String> good = results.findAll { it && 'ERROR' != it }
        println "$strategy DONE in ${t1-t0} millis"
        println "$RUNS total"
        println "${good.size()} OK: ${good}"
        println "${RUNS-good.size()} errors"
    }

    static List<String> hystrix(String host, int port, List<String> data) {
        List<hystrix.Client> clients = data.collect {
            new hystrix.Client(new plain.Client(it, host, port))
        }
        List<Future<String>> results = clients.collect { it.queue() }
        while (results.find { !it.done }?.get()) {
            println "${RUNS-results.count{it.done}} pending..."
        }
        return results.collect { it.get() }
    }

    static List<String> plain(String host, int port, List<String> data) {
        List<plain.RunnableClient> clients = data.collect {
            new plain.RunnableClient(client:new plain.Client(it, host, port))
        }
        List<Thread> threads = clients.collect { new Thread(it) }
        threads.each { it.start() }
        while (threads.find { it.alive }) {
            println "${RUNS-threads.count{it.alive}} pending..."
            threads.find { it.alive }?.join()
        }
        return clients.collect { it.result }
    }

    static List<String> threadpool(String host, int port, List<String> data) {
        List<plain.Client> clients = data.collect {
            new plain.Client(it, host, port)
        }
        java.util.concurrent.ExecutorService tpool = java.util.concurrent.Executors.newFixedThreadPool(16)
        List<Future<String>> results = clients.collect { tpool.submit(it) }
        tpool.shutdown()
        while (!tpool.terminated) {
            println "${RUNS-results.count{it.done}} pending..."
            tpool.awaitTermination(1000,TimeUnit.MILLISECONDS)
        }
        if (results.find{!it.done})println "WTF?????????????????"
        return results.collect {
          try {
            it.get()
          } catch (Exception ex) {
            "ERROR"
          }
        }
    }

    static void main(String... args) {
        String host = args.length > 0 ? args[0] : 'localhost'
        int port = args.length > 1 ? args[1] as int : 1234
        new plain.RunnableClient(client:new plain.Client('warmup', host, port)).run()
        new hystrix.Client(new plain.Client('warmup',host,port)).execute()
        List<String> data = (1..RUNS).collect { names[rng.nextInt(names.size())] }
        printResults("Thread per connection"){plain(host, port, data)}
        printResults("Thread pool"){threadpool(host, port, data)}
        printResults("Hystrix"){hystrix(host, port, data)}
        System.exit(0)
    }
}
