hystrix-tests
=============

Simple tests of the Hystrix library by Netflix, by comparing its usage with two common patterns: thread-per-connection and using a thread pool.

###Running the server

The tests require a simple server to be running. The server's written in Groovy; to run, just go into the `server` directory and type:

`groovy Server.groovy 1234`

(or whatever port you want to run)

###Running the client

You can build and run the client tests with Gradle. Go into the `client` directory and type

`gradle run-client`

The server needs to be running already.

###Playing with the test

The `client.Main` class contains a list of the strings that are sent to the server. The server is designed to simulate three things:

* Normal behavior, just return a response as quickly as possible
* Delayed behavior, simulating a server that's responding slowly (under heavy load or whatever). This happens in response to strings starting with `A`.
* Saturated behavior, when the server doesn't respond (accepts a connection but doesn't respond on time). This happens in response to strings starting with `B`.

And of course if you just _don't_ run the server, you get to test the "server is down" behavior.

You can find Hystrix right here on github: github.com/netflix/hystrix
