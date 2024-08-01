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
import java.util.stream.Collector;

public class TextServer {

    private static final int PORT_NUM = 1212;
    private static final String BORDER = "===========================";
    private static final String PROMPT = String.format("%s%n%s%n%s%n%s%n%s%n%s%n%s",
       Protocol.MESSAGE+ BORDER,
       Protocol.MESSAGE+ "0 : Connect to Server",
       Protocol.MESSAGE+ "1 : Get the User List",
       Protocol.MESSAGE+ "2 : Send a message",
       Protocol.MESSAGE+ "3 : Get my Messages",
       Protocol.MESSAGE+ "4 : Exit",
       Protocol.MESSAGE+ BORDER
    );

    private static final HashMap<Socket, String> sessions = new HashMap<>();
    private static final HashMap<String, String> users = new HashMap<>();
    private static final HashMap<String, ArrayList<Message>> userMessages = new HashMap<>();

    /**
     * Protocol enum to handle different types of messages sent from the server to the 
     * client
     */
    private static enum Protocol {
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

        @Override
        public String toString() {
            return code+":";
        }
    
    
}
    
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
            String msg = String.format("%s : %s",
                sender, message);
            return msg;
        }

    }

    /**
     * Initializer block to populate user hash map & userMessages Hashmap
     */
    static {
        users.put("Rf922", "secret-key01");
        users.put("Yetem", "secret-key02");
        users.put("Keev", "secret-key03");
        users.put("Don", "secret-key04");
        users.put("sean", "secret-key05");
        users.put("Brianna", "secret-key06");

        userMessages.put("Rf922", new ArrayList<>());
        userMessages.get("Rf922").add(new Message(LocalDateTime.now(), "Sean", "Down for Math book club ? 4:00 P.M Wednday @ the pub "));
        userMessages.get("Rf922").add(new Message(LocalDateTime.now(), "Yetem", "wyd"));

        userMessages.put("Yetem", new ArrayList<>());
        userMessages.get("Yetem").add(new Message(LocalDateTime.now(), "Sean", "Down for Math book club ? 4:00 P.M Wednday @ the pub "));

        userMessages.put("Don", new ArrayList<>());
        userMessages.put("Keev", new ArrayList<>());
        userMessages.put("Sean", new ArrayList<>());
        userMessages.put("Brianna", new ArrayList<>());

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
                    System.out.println("User's choice is: " + res);
                    int selection = Integer.parseInt(res);
                    switch (selection) {
                        case 0 ->
                            accessServer(out, in, socket);
                        case 1 -> 
                            getUserList(out); 
                        case 2 ->
                            sendMessage(out, in, socket);
                        case 3 ->
                            getUserMessages(out, in, socket);
                        case 4 -> {
                            try (socket) {
                                out.println(Protocol.EXIT+"Exiting...");
                                String userName = sessions.get(socket);
                                if (sessions.containsKey(socket)) {
                                    sessions.remove(socket);
                                }
                                sessActive = false;
                                System.out.println(userName+" is Logged out..");
                            }
                        }

                        default ->
                            out.println(Protocol.MESSAGE+"Invalid option. please try again");
                    };
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (sessions.containsKey(socket)) {
                sessions.remove(socket);
            }
            try {
                socket.close();
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
     * Prompts the user for their user name and password. Upon success informs
     * the user by printing an acknowledgement message
     *
     * @param out
     * @param in
     * @throws IOException
     */
    public static void accessServer(PrintWriter out, BufferedReader in, Socket clientSocket) throws IOException {
        out.println(Protocol.MESSAGE+"Please enter your user name : ");
        String userName = in.readLine();
        if (userName != null && users.containsKey(userName)) {
            out.println(Protocol.MESSAGE+"Please enter your password : ");
            String passwd = in.readLine();
            System.out.println("Username = "+userName + " Password = "+passwd);
            if (users.get(userName).equalsIgnoreCase(passwd)) {
                out.println(Protocol.MESSAGE+BORDER+"\n"+Protocol.MESSAGE+"Access Granted"+"\n" +Protocol.MESSAGE+ BORDER);
                System.out.println("Access Granted");
                sessions.put(clientSocket, userName);
            }
        } else {
            out.println(Protocol.MESSAGE+"User Was not found");
        }
    }

    /**
     * Retrieves a list of each users user name in the servers user list
     *
     * @param out
     */
    public static void getUserList(PrintWriter out) {
        System.out.println("Returning List of Users..");
        out.println(Protocol.MESSAGE+"Returning List of users..");
        users.keySet().forEach(user -> {
            out.println(Protocol.MESSAGE+user);
            System.out.println(user);
        });
    }

    public static void sendMessage(PrintWriter out, BufferedReader in, Socket clientSocket) throws IOException {
        if (sessions.containsKey(clientSocket)) {
            String userName = sessions.get(clientSocket);
            out.println(Protocol.MESSAGE+"Enter a user name you want to send a message to : ");
            String receiver = in.readLine();
            if (receiver != null && users.containsKey(receiver)) {
                out.println(Protocol.MESSAGE+"Enter the message you want to send : ");
                String msg = in.readLine();
                Message newMsg = new Message(LocalDateTime.now(), userName, msg);
                userMessages.get(receiver).add(newMsg);
                out.println(Protocol.MESSAGE+"Status : Message sent successfully");
                System.out.println("Received Message for "+receiver);
            } else {
                out.println(Protocol.MESSAGE+"Unable to send message please try again.");
            }
        }
    }

    /**
     * Retrieves a users messages stored on the server
     *
     * @param out
     * @param in
     * @param clientSocket
     */
    public static void getUserMessages(PrintWriter out, BufferedReader in, Socket clientSocket) {
        if (sessions.get(clientSocket) != null) {
            String user = sessions.get(clientSocket);
            ArrayList<Message> userInbox = userMessages.get(user);
            userInbox.sort(Message::compareTo);
            System.out.println("Retrieving Messages for "+user);
            out.println(String.format("%s%n%s", Protocol.MESSAGE+"Here are your messages : ", Protocol.MESSAGE+BORDER));
            userInbox.stream().map(x -> Protocol.MESSAGE+x.toString()).forEach(out::println);
        }else{
            out.println(Protocol.MESSAGE+"Unable to retrievee User Messages, please try again");
        }
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
