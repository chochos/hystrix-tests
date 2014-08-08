package plain

class RunnableClient implements Runnable {
  Client client
  String result
  void run() {
    result = client.call()
  }
}
