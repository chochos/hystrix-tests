/** A very simple server, no additional libs, just accept connections and reply something. */
int port = (args[0]?:1234) as int
ServerSocket ss = new ServerSocket(port)
println "Listening on port ${port}"
while (true) {
  Socket sock = ss.accept()
  println "New Connection from ${sock.inetAddress}"
  new Thread(new Conn(socket:sock)).start()
}
