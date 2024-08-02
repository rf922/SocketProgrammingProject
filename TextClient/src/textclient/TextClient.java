/*
 * This is the text client for the socket programming project
 * This client is meant to connect to the textserver and allows the 
 * user to interact with and query the server
 */
package textclient;

import java.io.*;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TextClient {

    /**
     * The port number and host configuration for the server
     */
    private static final int PORT_NUM = 1212;
    private static final String HOST = "localhost";

    /**
     * Private enum for protocol codes from the server,
     */
    private static enum Protocol {
        /**
         * Codes for regular msg, error and exit
         */
        MESSAGE(94132),
        ERROR(1),
        EXIT(922002234);

        private final int code;

        Protocol(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }

        public static Protocol fromCode(int code) {
            for (Protocol protocol : Protocol.values()) {
                if (protocol.getCode() == code) {
                    return protocol;
                }
            }
            throw new IllegalArgumentException("Unknown protocol code: " + code);
        }
    }

    /**
     * Main entry point for the client program
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        try (Socket textClientSocket = new Socket(HOST, PORT_NUM); BufferedReader clientInput = new BufferedReader(new InputStreamReader(System.in)); BufferedReader serverResponse = new BufferedReader(new InputStreamReader(textClientSocket.getInputStream())); PrintWriter pw = new PrintWriter(textClientSocket.getOutputStream(), true);) {
            Runnable serverMessageHandler = () -> {//client side logic 
                try {
                    String serverMessage;
                    while ((serverMessage = serverResponse.readLine()) != null ) {// main client loop
                        //System.out.println("[ SERVER MESSAGE ]"+serverMessage);
                        String[] segments = serverMessage.split(":", 2);
                        int protocolCode = Integer.parseInt(segments[0]);
                        String serverMsg = segments.length > 1 ? segments[1] : "";
                        Protocol protocol = Protocol.fromCode(protocolCode);
                        switch (protocol) {
                            case MESSAGE -> // default message reponses from server
                                System.out.println(serverMsg);
                            case ERROR ->   // error messages from the server
                                System.out.println("Error Message From Server : " + serverMsg);
                            case EXIT -> {  // handles signal to exit the server
                                System.out.println(serverMsg);
                                executorService.shutdown();
                                textClientSocket.close();
                                System.exit(0);
                            }
                            default ->
                                System.out.println("Unknown protocol code received : " + protocolCode);
                        }

                    }
                } catch (IOException e) {
                    System.out.println("Error reading from server: " + e.getMessage());
                }
            };

            executorService.submit(serverMessageHandler);

            while (!executorService.isShutdown()) {
                String userInput = clientInput.readLine();
                if (userInput != null) {
                    pw.println(userInput);
                }
            }
        }catch (ConnectException e){
            System.out.println("Connection refused. Please ensure the server is running and reachable.");
        } catch (IOException e) {
            System.out.println("Network error occurred: " + e.getMessage());
            //e.printStackTrace();
        
        } catch (Exception e) {
            System.out.println("An unexpected error occurred: " + e.getMessage());
            //e.printStackTrace();
        }finally {
            executorService.shutdown();
        }
    }
    

}
