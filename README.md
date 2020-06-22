Push-Pull Proxy
===============

Introduction
------------

This project is the PoC implementation of a Push-Pull Proxy (PPP). The basic idea is to divide up a classical proxy server into two parts:

* push: placed in a DMZ, it accepts incoming connections from the internet and from the intranet.
* pull: placed in the intranet, it connects to the push proxy and forwards data to a server in the intranet.

After setting up the two servers, it basically works as a normal reverse proxy server with the difference being, that it can tunnel data into a safe zone from a DMZ, without initiating a connection into it.

The implementation
------------------

The project consists of two basic Java projects: `PPP_Push` and `PPP_Pull`. Both projects are implemented by using the standard Java library of version 1.8. No Java 8-specific features were used, so theoretically the code should be fine with earlier versions as well.

Both projects contain a `Main` class, which currently hard codes the settings (hosts, ports). Both servers operate on localhost.

The Push server listens on the ports 8080 and 8081. It is able to accept a greater amount of connections. For every connection it creates a thread, pushes the request into a threadsafe queue and waits for the response. Another thread consumes the messages and forwards them to the Pull server. There is only one request forwarded at a time. Rest of the requests are stopped, until the previous one is answered.

The Pull server connects to the ports 8081 and to 9998. It reads the request, and forwards it. Upon receiving the response, it writes the data back to the Push server.

```
     Internet     [Firewall]      DMZ      [Firewall]                    Intranet
                      |                        |                                                       |
                      |                        |                                                       |
 [Remote machine] ----|----> [Push Proxy] <----|---- [Pull Proxy] --------> [Intranet Server/Service]  |
                      |                        |                                                       |
                      |                        |                                                       |
```

Performance and latency
-----------------------

Since this project is just a PoC, it wasn't optimized for performance. It could however be used to test for minimal latency of such a system.

A simple KeyValue Store was written in Rapidoid with a latency between 1ms and 2ms.

When connecting via the PPP, the latency was aroudn 3ms to 6ms. This was probably due to the fact, that the internal Queue in the Push server is checked once every 1ms, if it is empty.

When testing for throughput, there wasn't any real difference in regard to the throughput. The main difference is however, that both the Push and the Pull server caches the data in memory. No real forwarding is currently performed. The results:

```
real	0m0,034s
user	0m0,003s
sys	0m0,013s



real	0m0,030s
user	0m0,015s
sys	0m0,005s
```
