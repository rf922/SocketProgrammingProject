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
    private static final String BORDER = "===========================";
    private static final String PROMPT = String.format("%s%n%s%n%s%n%s%n%s%n%s",
        BORDER,
        "0 : Connect to Server",
        "1 : Get the User List",
        "2 : Send a message",
        "3 : Get my Messages",
        "4 : Exit",
        BORDER
    );

    private static final HashMap<Socket, String> sessions = new HashMap<>();
    private static final HashMap<String, String> users = new HashMap<>();
    private static final HashMap<String, ArrayList<Message>> userMessages = new HashMap<>();

    /**
     * Message class to handle user to user messages
     */
    private static class Message implements Comparable<Message> {

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
        try (PrintWriter out = new PrintWriter(socket.getOutputStream(), true); BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            boolean sessActive = true;

            while (sessActive) {
                out.println(PROMPT);
                String res = in.readLine();
                if (res != null) {
                    int selection = Integer.parseInt(res);
                    switch (selection) {
                        case 0 -> accessServer(out, in, socket);
                        case 1 -> getUserList(out);
                        case 2 -> {

                        }
                        case 3 -> {
                            
                        }
                        case 4 -> {
                            out.println("Exiting...");
                            if(sessions.containsKey(socket)){
                                sessions.remove(socket);
                            }
                            sessActive = false;
                        }
                        default ->
                            out.println("Invalid option. please try again");
                    };
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        if (sessions.containsKey(socket)) {
            sessions.remove(socket); // Clean up session.
        }
        try {
            socket.close(); // Ensure socket is closed.
        } catch (IOException e) {
            System.out.println("Error closing socket.");
        }
    }
    }

    /**
     * 
     * Methods to handle processing each option 0 - 4
     * 
     */
    
    /**
     * prompts the user for their user name and password. Upon success informs the 
     * user by printing an acknowledgement message
     * @param out
     * @param in
     * @throws IOException 
     */
    public static void accessServer(PrintWriter out, BufferedReader in, Socket clientSocket) throws IOException {
        out.println("Please enter your user name : ");
        String userName = in.readLine();
        if (userName != null && users.containsKey(userName)) {
            out.println("Please enter your password : ");
            String passwd = in.readLine();
            if (users.get(userName).equalsIgnoreCase(passwd)) {
                out.println(BORDER + "\nAccess Granted\n" + BORDER);
                sessions.put(clientSocket, userName);
            }
        } else {
            out.println("User Was not found");
        }
    }
    
    /**
     * Retrieves a list of each users user name in the servers user list
     * @param out 
     */
    public static void getUserList(PrintWriter out){
        out.println("Returning List of users..");
        users.keySet().forEach(user -> out.println(user));
    }
    
    public static void sendMessage(PrintWriter out, BufferedReader in, Socket clientSocket) throws IOException{
        if(sessions.containsKey(clientSocket)){
            String userName = sessions.get(clientSocket);
            out.println("Enter a user name you want to send a message to : ");
            String receiver = in.readLine();
            if(receiver != null && users.containsKey(receiver)){
                out.println("Enter the message you want to send : ");
                String msg = in.readLine();
                Message newMsg = new Message(LocalDateTime.now(), userName, msg);
                userMessages.get(receiver).add(newMsg);
            }else{
                out.println("Unable to send message please try again.");
            }
        }
    }
    
    /**
     * Retrieves a users messages stored on the server
     * @param out
     * @param in 
     * @param clientSocket 
     */
    public static void getMyMessages(PrintWriter out, BufferedReader in, Socket clientSocket){
        
    }
    
    
}




/**
 * 
 *  questions to consider
 * 
 * 1. Should new users be able to register ?
 * 2. Should getMyMessages also get the messages a user has sent outwards ?
 * 3. Are the other options selectable i.e can a user directly pick 3 without 
 * having picked 0 , if thats the case can i prompt them for the user name ? Or should i 
 * prompt them to select option 0 ?
 * 
 * 
 * 
 */