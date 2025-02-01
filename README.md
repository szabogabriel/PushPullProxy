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

Since this project is just a PoC, it wasn't optimized for performance. It could however be used to test for minimal latency of such a system and for some theoretical throughputs.

A simple KeyValue Store was written in Rapidoid with a latency between 1ms and 2ms to test this application.

When connecting via the PPP, the latency was around 3ms to 6ms. This was besides the obvious latency of pushing through two additional servers also caused by the fact, that the internal Queue in the Push server is checked once every 1ms, if it was empty.

When testing for performance, there wasn't any real difference in regard to the throughput. The main difference is however, that both the Push and the Pull server caches the data in memory. No real forwarding is currently performed. The results are a bit confusing, but at least visible, that a more complex system can produce bigger delays, as two, rather small reverse proxies.

```
Direct access:
real	0m0,034s
user	0m0,003s
sys	0m0,013s


PPP access:
real	0m0,030s
user	0m0,015s
sys	0m0,005s
```

Alternatives
------------

If you have SSH connection to the DMZ machine, you can use the SSH protocol insteads. Meaning, you can log in by using the `-R` switch instead which will result in a port forward as specified. For example:

```Bash
ssh -R 8080:localhost:80 my_user@my_server.com
```

will port forward requests from the remote machines 8080 port to your local port 80. The only thing you have to do is to configure a reverse proxy forwarding requests to the 8080 port. For example in Nginx:

```
server {
    listen 80;
    server_name your-dns-name.com;
    
    location / {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

