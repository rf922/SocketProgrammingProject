/*
 * This is the server for the socket programming project
 * it allows clients to send messages to one another, see other users and
 * check received messages
 */
package textserver;

import java.io.*;
import java.net.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TextServer {

    /**
     * port number for the server
     */
    private static final int PORT_NUM = 1212;
    
    /**
     * text prompt and border for server messages
     */
    private static final String BORDER = "===========================";
    private static final String PROMPT = String.format("%s%n%s%n%s%n%s%n%s%n%s%n%s",
        Protocol.MESSAGE + BORDER,
        Protocol.MESSAGE + "0 : Connect to Server",
        Protocol.MESSAGE + "1 : Get the User List",
        Protocol.MESSAGE + "2 : Send a message",
        Protocol.MESSAGE + "3 : Get my Messages",
        Protocol.MESSAGE + "4 : Exit",
        Protocol.MESSAGE + BORDER
    );

    /**
     * hashmaps to store active sessions, the servers users and their messages
     */
    private static final HashMap<Socket, String> sessions = new HashMap<>();
    private static final HashMap<String, String> users = new HashMap<>();
    private static final HashMap<String, ArrayList<Message>> userMessages = new HashMap<>();

    /**
     * Protocol enum to handle different types of messages sent from the server
     * to the client
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
            return code + ":";
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
        /**
         * The serrver users
         */
        users.put("Rf922", "secret-key01");
        users.put("Yetem", "secret-key02");
        users.put("Keev", "secret-key03");
        users.put("Don", "secret-key04");
        users.put("Sean", "secret-key05");
        users.put("Brianna", "secret-key06");

        /**
         * User's messages / inboxes 
         */
        userMessages.put("Rf922", new ArrayList<>());
        userMessages.put("Yetem", new ArrayList<>());
        userMessages.put("Don", new ArrayList<>());
        userMessages.put("Keev", new ArrayList<>());
        userMessages.put("Sean", new ArrayList<>());
        userMessages.put("Brianna", new ArrayList<>());
        
        /**
         * Sample messages
         */

        /* Rf922 */
        userMessages.get("Rf922").add(new Message(LocalDateTime.now(), "Sean", "Down for Math book club ? 4:00 P.M Wednday @ the pub "));
        userMessages.get("Rf922").add(new Message(LocalDateTime.now(), "Yetem", "wyd"));

        /* Yetem */
        userMessages.get("Yetem").add(new Message(LocalDateTime.now(), "Sean", "Down for Math book club ? 4:00 P.M Wednday @ the pub "));



    }

    /**
     * main process for the server.
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        ExecutorService executorService = Executors.newCachedThreadPool();
        try (ServerSocket textServerSocket = new ServerSocket(PORT_NUM)) {
            System.out.println("Server is running...");
            while (true) { //main event loop while server is active
                Socket clientSocket = textServerSocket.accept();
                executorService.submit(() -> handleClient(clientSocket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            executorService.shutdown();
        }
    }

    /**
     * Central processing for connected clients, displays the client with 
     * a prompt of options to choose from then calls the corresponding method
     * depending on the users selection
     * @param socket 
     */
    public static void handleClient(Socket socket) {
        try (PrintWriter out = new PrintWriter(socket.getOutputStream(), true); BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            boolean sessActive = true;
            while (sessActive) {// main loop while server is running 
                out.println(PROMPT);
                String res = in.readLine();
                if (res != null) {
                    try {
                        int selection = Integer.parseInt(res);
                        System.out.println("User's choice is: " + res);
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
                                exitServer(out, socket);
                                sessActive = false;
                            }
                            default ->
                                out.println(Protocol.ERROR+"Please select a valid option");
                        }
                    } catch (NumberFormatException e) {
                        out.println(Protocol.ERROR+"Please select a valid option");
                    }

                }                
                
                
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally { // handles closing and removing closed connections
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
     * @param clientSocket
     * @throws IOException
     */
    public static void accessServer(PrintWriter out, BufferedReader in, Socket clientSocket) throws IOException {
        out.println(Protocol.MESSAGE + "Please enter your user name : ");
        String userName = in.readLine();
        if (userName != null && !userName.isBlank() && users.containsKey(userName)) {
            out.println(Protocol.MESSAGE + "Please enter your password : ");
            String passwd = in.readLine();
            System.out.println("Username = " + userName + " Password = " + passwd);
            if (passwd != null && !passwd.isBlank() && users.get(userName).equalsIgnoreCase(passwd)) {
                out.println(Protocol.MESSAGE + BORDER + "\n" + Protocol.MESSAGE + "Access Granted" + "\n" + Protocol.MESSAGE + BORDER);
                System.out.println("Access Granted");
                sessions.put(clientSocket, userName);
            }else{
                out.println(Protocol.ERROR + "Invalid Password, please try again");
            }
        } else {
            out.println(Protocol.MESSAGE + "User Was not found");
        }
    }

    /**
     * Retrieves a list of each users user name in the servers user list
     *
     * @param out
     */
    public static void getUserList(PrintWriter out) {
        System.out.println("Returning List of Users..");
        out.println(Protocol.MESSAGE + "Returning List of users..");
        users.keySet().forEach(user -> {
            out.println(Protocol.MESSAGE + user);
            System.out.println(user);
        });
    }

    /**
     * Handles the sending of messages from one user to another, storing the 
     * message in the local message map and responding to the client with a 
     * status message
     * @param out
     * @param in
     * @param clientSocket
     * @throws IOException 
     */
    public static void sendMessage(PrintWriter out, BufferedReader in, Socket clientSocket) throws IOException {
        if (sessions.containsKey(clientSocket)) {
            String userName = sessions.get(clientSocket);
            out.println(Protocol.MESSAGE + "Enter a user name you want to send a message to : ");
            String receiver = in.readLine().strip();
            if (receiver != null && !receiver.isBlank() && users.containsKey(receiver)) {
                out.println(Protocol.MESSAGE + "Enter the message you want to send : ");
                String msg = in.readLine();
                if(msg != null && !msg.isBlank()){
                    Message newMsg = new Message(LocalDateTime.now(), userName, msg);
                    userMessages.get(receiver).add(newMsg);                
                    out.println(Protocol.MESSAGE + "Status : Message sent successfully");
                    System.out.println("Received Message for " + receiver);
                }else{
                    out.println(Protocol.ERROR + "Unable to send message please try again.");
                }
            } else {
                out.println(Protocol.ERROR + "Unable to send message please try again.");
            }
        }else{
            out.println(Protocol.ERROR + "Username was not found on server, please try again");          
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
            System.out.println("Retrieving Messages for " + user);
            ArrayList<Message> userInbox = userMessages.get(user);
            if(!userInbox.isEmpty()){
                userInbox.sort(Message::compareTo);
                out.println(String.format("%s%n%s", Protocol.MESSAGE + "Here are your messages : ", Protocol.MESSAGE + BORDER));
                userInbox.stream().map(x -> Protocol.MESSAGE + x.toString()).forEach(out::println);
            }
        } else {
            out.println(Protocol.ERROR + "Unable to retrieve User Messages, please try again");
        }
    }

    /**
     * Handles users exiting from the server by removing their session and 
     * sending the exit protocol signal to the client signaling that the client
     * has exited the server.
     * @param out
     * @param clientSocket
     * @throws IOException 
     */
    public static void exitServer(PrintWriter out, Socket clientSocket) throws IOException {
        out.println(Protocol.EXIT + "Exiting...");
        String userName = sessions.getOrDefault(clientSocket, "client");
        if (sessions.containsKey(clientSocket)) {
            sessions.remove(clientSocket);
        }
        System.out.println(userName + " is Logged out..");        

    }
}

/**
 *
 * questions to consider
 *
 * 1. Should new users be able to register ? 2. Should getMyMessages also get
 * the messages a user has sent outwards ? 3. Are the other options selectable
 * i.e can a user directly pick 3 without having picked 0 , if thats the case
 * can i prompt them for the user name ? Or should i prompt them to select
 * option 0 ?
 *
 * CASE SENSITIVITY ! 
 *
 *
 */
