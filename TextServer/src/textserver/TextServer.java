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
    
    private static final int PORT_NUM = 1212;
    private static final String PROMPT = String.format("", "");
    
    private static final HashMap<String, String> users = new HashMap<>();
    private static final HashMap<String, ArrayList<Message>> userMessages = new HashMap<>();

    /**
     * Message class to handle user to user messages
     */
    private static class Message implements Comparable<Message> {

        private static final String border = "===========================";
        private LocalDateTime date;
        private String sender;
        private String message;

        public Message(LocalDateTime date, String sender, String message) {
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

    /**
     * initializer block to populate user hash map & userMessages Hashmap
     */
    static {
        users.put("Rf922", "secret-key01");
        users.put("Yetem", "secret-key02");
        users.put("Keev", "secret-key03");
        users.put("Don", "secret-key04");
        users.put("sean", "secret-key05");

        userMessages.put("Rf922", new ArrayList<>());
        userMessages.get("Rf922").add(new Message(LocalDateTime.now(), "Sean", "Down for Math book club ? 4:00 P.M Wednday @ the pub "));
        userMessages.get("Rf922").add(new Message(LocalDateTime.now(), "Yetem", "wyd"));

        userMessages.put("Yetem", new ArrayList<>());
        userMessages.get("Yetem").add(new Message(LocalDateTime.now(), "Sean", "Down for Math book club ? 4:00 P.M Wednday @ the pub "));

    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        ExecutorService executorService = Executors.newCachedThreadPool();
        try (ServerSocket textServerSocket = new ServerSocket(PORT_NUM)) {
            System.out.println("Server is running...");
            while (true) {
                Socket clientSocket = textServerSocket.accept();
                executorService.submit(() -> handleClient(clientSocket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            executorService.shutdown();
        }
    }

    public static void handleClient(Socket socket) {
        try (PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            String prompt = String.format("");
            out.println("Prompt here");
            String res = in.readLine();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
