class Conn implements Runnable {

    Socket socket

    void run() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.inputStream))
        PrintStream out = new PrintStream(socket.outputStream)
        String line = reader.readLine()
        if (line.startsWith('A')) {
            //Intentional delay
            Thread.sleep(2000)
            out.println("LATE ${line.reverse()}")
        } else if (line.startsWith('B')) {
            //Don't do anything, let the client timeout
            socket.inputStream.read()
        } else {
            out.println(line.reverse())
        }
        println "Closing ${socket.inetAddress} ${line}"
        socket.close()
    }
}
