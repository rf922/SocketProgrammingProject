/*
 * 
 * 
 * 
 */
package textserver;

import java.io.*;
import java.net.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TextServer {

    private static final HashMap<String, String> users = new HashMap<>();
    private static final HashMap<String, ArrayList<Message>> userMessages = new HashMap<>();
    
    private static class Message implements Comparable<Message> {
        private static final String border = "===========================";
        private LocalDateTime date;
        private String sender;
        private String message;
        
        public Message(LocalDateTime date, String sender, String message){
            this.date = date;
            this.sender = sender;
            this.message = message;
        }

        @Override
        public int compareTo(Message o) {
            return this.date.compareTo(o.date);
        }

        @Override
        public String toString() {
            String msg = String.format("%n%s : %s%n",
                sender, message);
            return msg;
        }
        
 
        
        
    }
    
    static {
        users.put("Rf922", "secret-key01");
        users.put("Yetem", "secret-key02");
        users.put("Keev", "secret-key03");
        users.put("Don", "secret-key04");
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        ExecutorService executorService = Executors.newCachedThreadPool();
        try(ServerSocket textServerSocket = new ServerSocket(1212)){
            System.out.println("Server is running...");
            while(true){
                Socket clientSocket = textServerSocket.accept();
                executorService.submit(() -> handleClient(clientSocket));                
            }
        }catch(IOException e){
            e.printStackTrace();
        }finally {
        executorService.shutdown();}
    }
    
    public static void handleClient(Socket socket){
        try(PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))
            ){
            
            String prompt = String.format("");
            out.println("");
        }catch(IOException e) {
            
            }
    }

}
