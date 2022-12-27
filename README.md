# Real-time-Location-Communication

This Project is intended to showcase Asynchronous communication between Python Server and Android Kotlin Application

## Location Share Server

This is a server for sharing location between multiple clients. The server is implemented using the asyncio module in Python.

-   **Prerequisites**

    Python 3.7+asyncio module

-   **Usage**

To run the server, execute the following command:

    python ./server/server.py

-   **Explanation**

The server will start listening on the host and port specified in the LocationShareServer class. In the current implementation, the host is set to '', which means the server will listen on all available interfaces. The port is set to 5555.

The server uses a logger to log events and errors to a file location*share_server.log in the logs directory. The logger logs messages with the following format: *`[timestamp] - level - message`\_
where timestamp is the time at which the log message was generated, level is the log level (e.g. DEBUG, INFO, WARNING, ERROR) and message is the log message.

The server maintains a list of connected clients, stored in the _`_clients dictionary`_. The keys of the dictionary are the client identifiers, which are in the format _`address:port`_. The values are tuples containing the _`reader and writer objects`_ for each client.

When a client connects to the server, the **_`handle_client`_** method is called with the reader and writer objects as arguments. The method adds the client to the \_clients dictionary and starts a task to receive location updates from the client using the _`recieve_location`_ method.

The **_`recieve_location`_** method reads data from the reader object in chunks of 1024 bytes and decodes it to a string. If the received data is empty, the method breaks out of the loop and closes the connection with the client. If the received data is not empty, the method broadcasts the location update to all other clients using the _`broadcast_location`_ method.

The **_`broadcast_location`_** method sends the **_`location`_** update to all clients in the \_clients dictionary, except for the sender. The location update is sent as a string in the format sender -- location, where sender is the client identifier and location is the location update received from the client.

## Android Client App

The server is intended to be used with any client, here Kotlin Android client app that uses AsynchronousSocketChannel with coroutine is utilized and Mapbox for maps. The app sends location updates to the server using the **_`java.nio`_** library and receives updates from other clients using the same library. The app displays the received location updates on a Mapbox mapview.

    dependencies {
        // Other dependencies required
        implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.3'
        implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.6.3'
        implementation "androidx.lifecycle:lifecycle-runtime-ktx:2.5.1"
        implementation "com.google.android.gms:play-services-location:21.0.1"
        implementation 'com.mapbox.maps:android:10.10.0'
    }

## Firewall Configuration

The server requires the following ports to be open:
To open the required ports in the firewall on a Red Hat-based system, such as CentOS, Fedora, Oracle Linux, use the following commands:

Add port 5555 to the firewall

    firewall-cmd --zone=public --permanent --add-port=5555/tcp

Reload firewall rules

    firewall-cmd --reload

To remove the ports from the firewall, use the following commands:

    firewall-cmd --zone=public --permanent --remove-port=5555/tcp

## TODO

-   [ ] Add instructions for Debian based systems
-   [ ] Add instruction for Systemd Service
