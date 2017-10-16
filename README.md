# ComputerNetworks
Progras to sync data over the network to multiple clients concurrently.

Application to sync Music files(Practically all directory contents) between the Server and multiple Clients.

The Client -
A single application that can be invoked a number of times to connect to the Multithreaded Server.

The Server -
A Multithreaded program to connect to all the clients that request for a connection.

The ClientWorker - 
A ClientWorker program contains most of the Server code and helps the server to create the multithreaded environment to connect to several Clients and Sync concurrently. 
