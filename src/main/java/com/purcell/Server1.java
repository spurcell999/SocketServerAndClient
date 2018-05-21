package com.purcell;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server1 {

    public static void main(String args[]) {
        int port = 6789;
        Server1 server = new Server1(port);
        server.startServer();
    }

    ServerSocket echoServer = null;
    Socket clientSocket = null;
    int port;

    public Server1(int port) {
        this.port = port;
    }

    public void stopServer() {
        System.out.println("Server cleaning up");
        System.exit(0);
    }

    public void startServer() {
        try {
            echoServer = new ServerSocket(port);
        } catch (IOException e) {
            System.out.println(e);
        }
        System.out.println("Waiting for connections. Only one connection is allowed.");

        while ( true ) {
            try {
                clientSocket = echoServer.accept();
                Server1Connection oneconnection = new Server1Connection(clientSocket, this);
                oneconnection.run();
            }
            catch (IOException e) {
                System.out.println(e);
            }
        }
    }
}
class Server1Connection {
    BufferedReader is;
    PrintStream os;
    Socket clientSocket;
    Server1 server;

    public Server1Connection(Socket clientSocket, Server1 server) {
        this.clientSocket = clientSocket;
        this.server = server;
        System.out.println( "Connection established with: " + clientSocket );
        try {
            is = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            os = new PrintStream(clientSocket.getOutputStream());
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public void run() {
        String line;
        try {
            boolean serverStop = false;

            while (true) {
                line = is.readLine();
                System.out.println( "Received " + line );
                int n = Integer.parseInt(line);
                if ( n == -1 ) {
                    serverStop = true;
                    break;
                }
                if ( n == 0 ) break;
                os.println("" + n*n );
            }

            System.out.println( "Connection closed." );
            is.close();
            os.close();
            clientSocket.close();

            if ( serverStop ) server.stopServer();
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}
