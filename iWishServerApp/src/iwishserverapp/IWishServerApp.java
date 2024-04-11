
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import dto.FriendRequestDTO;
import dto.ItemDTO;
import dto.MyContributersDTO;
import dto.MyWishlistItemDTO;
import dto.NotificationDTO;
import dto.UserDTO;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;
import javafx.application.Application;
import javafx.stage.Stage;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

public class IWishServerApp extends Application {

    private ServerSocket server;
    private Vector<ClientHandler> clientsVector;
    private boolean serverRunning;
    private ServerTask serverTask;

    private JFrame frame;
    private JButton startButton;
    private JButton stopButton;
    String cuemail;

    public IWishServerApp() {
        clientsVector = new Vector<>();
        serverRunning = false;

        // Create the GUI
        frame = new JFrame("IWish Server");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 150);
        frame.setLayout(new FlowLayout());

        startButton = new JButton("Start Server");
        stopButton = new JButton("Stop Server");

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startServer();
            }

        });

        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stopServer();
            }
        });

        frame.add(startButton);
        frame.add(stopButton);

        frame.setVisible(true);
    }

    public void startServer() {
        if (!serverRunning) {
            serverTask = new ServerTask();
            serverTask.execute();
        }
    }

    public void stopServer() {
        if (serverRunning) {
            try {
                if (serverRunning) {
                    serverTask.cancel(true);
                    server.close();
                    serverRunning = false;

                    // Enable start button and disable stop button
                    startButton.setEnabled(true);
                    stopButton.setEnabled(false);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

    }

    private class ServerTask extends SwingWorker<Void, Void> {

        @Override
        protected Void doInBackground() {
            try {
                server = new ServerSocket(5005);
                serverRunning = true;

                SwingUtilities.invokeLater(() -> {
                    // Disable start button and enable stop button
                    startButton.setEnabled(false);
                    stopButton.setEnabled(true);
                });

                while (!isCancelled()) {
                    Socket clientSocket = server.accept();
                    ClientHandler clientHandler = new ClientHandler(clientSocket);
                    clientsVector.add(clientHandler);
                    clientHandler.start();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            } finally {
                SwingUtilities.invokeLater(() -> {
                    // Enable start button and disable stop button
                    startButton.setEnabled(true);
                    stopButton.setEnabled(false);
                });
            }
            return null;
        }
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new IWishServerApp();
            }
        });
    }

    private class ClientHandler extends Thread {

        private Socket clientSocket;
        private BufferedReader reader; // ear 
        private DataInputStream dis;
        private PrintStream ps; // mouth

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;

            try {
                reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                ps = new PrintStream(clientSocket.getOutputStream());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                while (true) {
                    String str = reader.readLine();

                    if (str == null || str.equals("close")) {
                        closeClient();
                        break;
                    } else if (str.equals("loginRequest")) {

                        Gson gson = new Gson();
                        // Read the JSON object string from the client
                        String jsonUser = reader.readLine();
                        // Convert the JSON string to a UserDTO object
                        UserDTO userDTO = gson.fromJson(jsonUser, UserDTO.class);
                        cuemail = userDTO.getEmail();

                        boolean auth = new db.DataAccessLayer().login(userDTO.getEmail(), userDTO.getPassword());
                        if (auth) {
                            sendMessage("succeed");
                        } else {
                            sendMessage("failed");
                        }
                    } else if (str.equals("registerRequest")) {
                        try {
                            Gson gson = new Gson();
                            UserDTO newUser = gson.fromJson(reader.readLine(), UserDTO.class);

                            boolean isRegistered = new db.DataAccessLayer().register(
                                    newUser.getUsername(), newUser.getPassword(), newUser.getEmail(),
                                    newUser.getFirstName(), newUser.getLastName(), String.valueOf(newUser.getBalance()));
                            if (isRegistered) {
                                sendMessage("succeed");
                            } else {
                                sendMessage("failed");
                            }
                        } catch (JsonSyntaxException e) {
                            e.printStackTrace();
                            sendMessage("failed");
                        }
                    } else if (str.equals("getCurrentUserData")) {
                        String userEmail = reader.readLine();
                        System.out.println(userEmail);
                        UserDTO currentUser = new db.DataAccessLayer().getCurrentUserData(userEmail);
                        Gson gson = new Gson();
                        String jsonCurrentUser = gson.toJson(currentUser);
                        sendMessage(jsonCurrentUser);
                    } else if (str.equals("Recharge")) { // email , amount
                        int addedBalance = new db.DataAccessLayer().rechargeBalance(reader.readLine(), reader.readLine());
                        if (addedBalance == 1) {
                            System.out.println("Balance Recharge Done");
                        } else {
                            System.out.println("Balance Recharge is not done");
                        }
                    } else if (str.equals("getUserWishlist")) {
                        try {
                            Gson gson = new Gson();
                            String userEmail = reader.readLine();
                            //System.out.println("User Email: " + userEmail);

                            Vector<MyWishlistItemDTO> wishlistItems = new db.DataAccessLayer().getUserWishlist(userEmail);

                            System.out.println("Wishlist Items: " + wishlistItems);

                            String jsonWishlist = gson.toJson(wishlistItems);
                            sendMessage(jsonWishlist);
                        } catch (JsonSyntaxException e) {
                            e.printStackTrace();
                            sendMessage("failed");
                        }
                    } else if (str.equals("ShowContributers")) {
                        Gson gson = new Gson();
                        String userEmail = reader.readLine();
                        String itemName = reader.readLine();
                        Vector<MyContributersDTO> contributersList
                                = new db.DataAccessLayer().getContributerslist(userEmail, itemName);
                        String jsonWishlist = gson.toJson(contributersList);
                        sendMessage(jsonWishlist);

                    } else if (str.equals("clearUserWishlist")) {
                        try {
                            String userEmail = reader.readLine();
                            boolean success = new db.DataAccessLayer().clearUserWishlist(userEmail);
                            if (success) {
                                sendMessage("success");
                            } else {
                                sendMessage("failed");
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            sendMessage("failed");
                        }
                    } else if (str.equals("removeItemFromWishlist")) {
                        try {
                            String userEmail = reader.readLine();
                            String itemId = reader.readLine();
                            boolean success = new db.DataAccessLayer().removeItemFromWishlist(userEmail, itemId);
                            if (success) {
                                sendMessage("success");
                            } else {
                                sendMessage("failed");
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            sendMessage("failed");
                        }
                    } else if (str.equals("getUserNotifications")) {
                        try {
                            Gson gson = new Gson();
                            String userEmail = reader.readLine();
                            System.out.println("User Email: " + userEmail);

                            Vector<NotificationDTO> notifications = new db.DataAccessLayer().getUserNotifications(userEmail);
                            for (NotificationDTO not : notifications) {
                                System.out.println("Notifications: " + not.getNotification_text());
                            }
                            System.out.println("Notifications: " + notifications);

                            String jsonNotifications = gson.toJson(notifications);
                            sendMessage(jsonNotifications);
                        } catch (JsonSyntaxException e) {
                            e.printStackTrace();
                            sendMessage("failed");
                        }
                    } else if (str.equals("removeNotification")) {
                        try {
                            // Read the notification ID from the client
                            int notificationId = Integer.parseInt(reader.readLine());

                            // Remove the specified notification
                            boolean removed = new db.DataAccessLayer().removeNotification(notificationId);
                            if (removed) {
                                sendMessage("success");
                            } else {
                                sendMessage("failed");
                            }
                        } catch (NumberFormatException | IOException e) {
                            e.printStackTrace();
                            sendMessage("failed");
                        }
                    } else if (str.equals("clearNotifications")) {
                        try {
                            // Read the user email from the client
                            String userEmail = reader.readLine();

                            // Clear all notifications for the specified user
                            boolean cleared = new db.DataAccessLayer().clearUserNotifications(userEmail);
                            if (cleared) {
                                sendMessage("success");
                            } else {
                                sendMessage("failed");
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            sendMessage("failed");
                        }
                    } else if (str.equals("getAllItems")) {
                        Gson gson = new Gson();
                        Vector<ItemDTO> allitemlist = new db.DataAccessLayer().getAllItems();
                        System.out.println(allitemlist);
                        String jsonAllItems = gson.toJson(allitemlist);
                        sendMessage(jsonAllItems);

                    } else if (str.equals("addToWishlist")) {
                        String id = reader.readLine();
                        String userEmail = reader.readLine();

                        System.out.println("Received id Item: " + id);
                        System.out.println("Received userEmail:" + userEmail);

                        boolean isItemAdded = new db.DataAccessLayer().addToWishlist(id, userEmail);

                        if (isItemAdded) {
                            System.out.println("succeed");
                            sendMessage("succeed");

                        } else {
                            System.out.println("failed");
                            sendMessage("failed");
                        }
                    } else if (str.equals("getallusersRequest")) {
                        Vector<UserDTO> users = new db.DataAccessLayer().retrieveallusers(cuemail);
                        Gson gson = new Gson();

                        String json = gson.toJson(users);
                        sendMessage(json);

                    } else if (str.equals("addfriendRequest")) {
                        String toUserEmail = reader.readLine();
                        boolean resultaddfriend = new db.DataAccessLayer().addfriend(cuemail,
                                toUserEmail);
                        if (resultaddfriend) {
                            sendMessage("succeed");
                        } else {
                            sendMessage("failed");
                        }

                    } else if (str.equals("AllfriendRequest")) {
                        Vector<FriendRequestDTO> allFriendRequest = new db.DataAccessLayer().AllfriendRequest(cuemail);
                        Gson gson = new Gson();

                        String json = gson.toJson(allFriendRequest);
                        System.out.println(json);
                        sendMessage(json);

                    } else if (str.equals("cancelfriendRequest")) {
                        String fromUserEmail = reader.readLine();

                        boolean cancelfriendRequest = new db.DataAccessLayer().cancelfriendRequest(
                                fromUserEmail, cuemail);
                        if (cancelfriendRequest) {
                            sendMessage("succeed");
                        } else {
                            sendMessage("failed");
                        }

                    } else if (str.equals("AcceptfriendRequest")) {
                        String fromUserEmail = reader.readLine();

                        boolean AcceptfriendRequest = new db.DataAccessLayer().AcceptfriendRequest(
                                fromUserEmail, cuemail);
                        if (AcceptfriendRequest) {
                            sendMessage("succeed");
                        } else {
                            sendMessage("failed");
                        }

                    } else if (str.equals("SearchUsers")) {
                        String emaillike = reader.readLine();

                        Vector<UserDTO> SearchUsers = new db.DataAccessLayer().SearchUsers(
                                cuemail, emaillike);
                        Gson gson = new Gson();
                        System.out.println("emaillike  " + emaillike);
                        String json = gson.toJson(SearchUsers);
                        System.out.println(json);
                        sendMessage(json);

                    } else if (str.equals("getUserfriendlist")) {
                        try {
                            Gson gson = new Gson();
                            String userEmail = reader.readLine();
                            System.out.println("User Email: " + userEmail);

                            Vector<UserDTO> friendlist = new db.DataAccessLayer().getFriends(userEmail);

                            System.out.println("friendlist: " + friendlist);

                            String jsonfriendlist = gson.toJson(friendlist);
                            sendMessage(jsonfriendlist);
                        } catch (JsonSyntaxException e) {
                            e.printStackTrace();
                            sendMessage("failed");
                        }
                    } else if (str.equals("getUserFriendWishlist")) {

                        Gson gson = new Gson();

                        String femail = reader.readLine();

                        Vector<ItemDTO> friendwishlist = new db.DataAccessLayer().getFriendwhislist(femail);

                        String jsonfriendwishlist = gson.toJson(friendwishlist);
                        sendMessage(jsonfriendwishlist);

                    } else if (str.equals("ContributetoFriend")) { // email , amount
                        int contributionResult = new db.DataAccessLayer().contributiontofriend(reader.readLine(),
                                reader.readLine(), reader.readLine(), reader.readLine());

                        if (contributionResult == 1) {
                            sendMessage("succeed");
                        } else {
                            sendMessage("failed");
                        }
                    } else if (str.equals("RemoveFriend")) { // email , amount

                        cuemail = reader.readLine();
                        String femail = reader.readLine();
                        new db.DataAccessLayer().removeFriend(cuemail, femail);
                    } else if (str.equals("getUserbalance")) {
                        try {
                            Gson gson = new Gson();
                            String userEmail = reader.readLine();

                            Vector<UserDTO> userbalance = new db.DataAccessLayer().getBalance(userEmail);

                            double balance = 0.0; // Default value in case no balance is found
                            if (!userbalance.isEmpty()) {
                                balance = userbalance.get(0).getBalance(); // Assuming there's only one UserDTO in the list
                            }

                            System.out.println("userbalance: " + balance);

                            sendMessage(gson.toJson(balance));
                        } catch (JsonSyntaxException e) {
                            e.printStackTrace();
                            sendMessage("failed");
                        }
                    } else if (str.equals("SearchItems")) {
                        String itemlike = reader.readLine();
                        Gson gson = new Gson();

                        Vector<ItemDTO> allitemlist = new db.DataAccessLayer().SearchItems(itemlike
                        );
                        String jsonAllItems = gson.toJson(allitemlist);

                        System.out.println(jsonAllItems);

                        sendMessage(jsonAllItems);

                    }

                }
            } catch (IOException ex) {
                // Handle SocketException: Connection reset
                System.err.println("Client disconnected abruptly: " + ex.getMessage());
            } finally {
                closeClient();
            }
        }

        private void sendMessage(String message) {
            ps.println(message);
        }

        private void closeClient() {
            try {
                if (reader != null) {
                    reader.close();
                }
                if (ps != null) {
                    ps.close();
                }
                clientsVector.remove(this);
                clientSocket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
