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
    private static final String HOST = "localhost";
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String message;
        Scanner sc = new Scanner(System.in);
        try(Socket textClientSocket = new Socket(HOST, PORT_NUM);){            
            System.out.println("Connected to Server ");
            Scanner scn = new Scanner(textClientSocket.getInputStream());
            PrintWriter pw = new PrintWriter(textClientSocket.getOutputStream(), true);
            while(true){
                System.out.println("Enter a message: ");
                message = sc.nextLine();
                pw.println(message);
                System.out.println(scn.nextLine());
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    
}
