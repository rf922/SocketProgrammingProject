/*
 * 
 * 
 * 
 */
package textclient;

import java.io.*;
import java.net.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TextClient {

    private static final int PORT_NUM = 1212;
    private static final String BORDER = "=========================";
    private static final String HOST = "localhost";

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        try (Socket textClientSocket = new Socket(HOST, PORT_NUM); 
            BufferedReader clientInput = new BufferedReader(new InputStreamReader(System.in)); 
            BufferedReader serverResponse = new BufferedReader(new InputStreamReader(textClientSocket.getInputStream())); 
            PrintWriter pw = new PrintWriter(textClientSocket.getOutputStream(), true);) {
            
            Runnable serverMessageHandler = () -> {
                try {
                    String serverMessage;
                    while ((serverMessage = serverResponse.readLine()) != null) {
                        System.out.println(serverMessage);
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
