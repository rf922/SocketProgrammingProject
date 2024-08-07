# TextServer for Socket Programming Project

## Project Overview
This project implements a text messaging server using Java socket programming. It manages multiple client connections, allowing authenticated users to send messages, see online users, and retrieve their messages. This project takes advantage of java Executors
to handle concurrent processes cleanly and efficiently. The server uses a cached thread pull to avoid the overhead of needing to create and manage new threads. The client relies on a single thread to handle processing server messages and sending client responses to said server.


## Requirements
- Java JDK 11 or higher
- Network access for TCP/IP communication

## Installation and Running the Server
To get the server up and running:

1. **Compile the server**:
   Navigate to the project directory and run the following command:

```bash
   javac TextServer.java TextClient.java
```

2. **Run the server**:
After compiling, start the server using:
```
java TextServer
```
2. **Run the client**:
After starting the server, start the client using:
```
java TextClient
```
## Features
- **User Authentication**: Users can log in with a username and password. Authentication is required to access other functionalities.
- **Get User List**: Authenticated users can request a list of all connected users.
- **Send and Receive Messages**: Users can send messages to other users and retrieve messages sent to them.
- **Session Management**: The server handles sessions, ensuring that each user's session is managed properly, including during login and logout processes.

## Usage
After starting the server, clients can connect by specifying the host IP and port number `1212`. Once connected, users will be prompted to log in or select from the available options.

## Testing
For testing purposes, use the following credentials:
- Username: `Rf922`, Password: `secret-key01`
- Username: `Yetem`, Password: `secret-key02`

Start two instances of the client program to simulate interactions between multiple users.

## Troubleshooting
- **Connection Issues**: Ensure that no firewall is blocking the TCP port 1212.
- **Login Issues**: Double-check the username and password. Make sure the server is running before attempting to connect.

## Contributing
Contributions to the project are welcome. Please fork the repository, make changes, and submit a pull request.

## License
This project is released under the MIT License.
