package iwish;

import client.ClientSocketManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import dto.FriendRequestDTO;
import java.lang.reflect.Type;

import dto.ItemDTO;
import dto.MyContributersDTO;
import dto.MyWishlistItemDTO;
import dto.NotificationDTO;
import dto.UserDTO;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javafx.scene.text.Text;

public class MainPageController {

    public String email;
    private ObservableList<MyWishlistItemDTO> myWishlistItemsData;

    @FXML
    private Label welcomeLabel;

    @FXML
    private TableView<MyWishlistItemDTO> wishlistTable;

    private TableColumn<MyWishlistItemDTO, String> itemNameColumn;

    private TableColumn<MyWishlistItemDTO, Double> itemPriceColumn;

    private TableColumn<MyWishlistItemDTO, Double> friendsContributionColumn;

    @FXML
    private Label notificationsLabel;
    @FXML
    private VBox root;
    @FXML
    private TabPane tabPane;
    @FXML
    private Button logout_btn;
    @FXML
    private Label friendsLabel1;
    @FXML
    private Tab homePage;
    @FXML
    private TableColumn<MyWishlistItemDTO, String> wishlist_itemNameColumn;
    @FXML
    private TableColumn<MyWishlistItemDTO, String> wishlist_itemPriceColumn;
    @FXML
    private TableColumn<MyWishlistItemDTO, String> wishlist_friendsContributionColumn;
    @FXML
    private Button showContributersButton;
    @FXML
    private Tab friendsPage;
    @FXML
    private Tab allItemsPage;
    @FXML
    private Tab connectPage;
    @FXML
    private Tab notificationsPage;
    @FXML
    private Button addToWishlistButton;
    @FXML
    private Label allContactsLabel;
    @FXML
    private TableView<UserDTO> allUsersTable;
    @FXML
    private TextField friendSearchTf;
    @FXML
    private Button rechargeBalanceButton;
    @FXML
    private Label userEmailLabel;
    @FXML
    private TableView<MyContributersDTO> contributers_tableview;
    @FXML
    private TableColumn<MyContributersDTO, String> contributers_email_column;
    @FXML
    private TableColumn<MyContributersDTO, String> contributers_amount_column;

    private ObservableList<MyContributersDTO> myContributersList;
    @FXML
    private Button contribute_to_fiend_button;

    @FXML
    private TableView<NotificationDTO> notifications_tableView;
    @FXML
    private TableColumn<NotificationDTO, String> notification_column;
    @FXML
    private Button clear_wishlist_button;
    @FXML
    private Button removeWishlist_button;
    @FXML
    private Button remove_notification;
    @FXML
    private Button clear_notifications;
    @FXML
    private Button accept;
    @FXML
    private Button cancel;
    @FXML
    private TableView<FriendRequestDTO> friend_request;
    @FXML
    private TableColumn<FriendRequestDTO, String> requestfriendtEmailName_col;
    @FXML
    private TableColumn<FriendRequestDTO, Date> friendrequest_Date_col;
    @FXML
    private Button addFriendButton;
    @FXML
    private TableColumn<UserDTO, String> allUsersEmail_column;
    @FXML
    private TableColumn<UserDTO, String> allusersLastName_column;
    @FXML
    private TableColumn<UserDTO, String> allUsersfirstName_column;
    @FXML
    private TableView<ItemDTO> m_itemsTable;
    @FXML
    private TableColumn<ItemDTO, Integer> m_col_itemid;
    @FXML
    private TableColumn<ItemDTO, String> m_col_itemname;
    @FXML
    private TableColumn<ItemDTO, Double> m_col_itemPrice;
    @FXML
    private TableColumn<ItemDTO, String> m_col_itemcategory;
    private ObservableList<ItemDTO> AllItemsData;
    @FXML
    private Label user_fname_label;
    @FXML
    private Label user_lname_label;
    UserDTO cUser;
    @FXML
    private Label balance_label;
    @FXML
    private TableView<UserDTO> friendstableview;
    @FXML
    private TableColumn<UserDTO, String> friendtable_fristname;
    @FXML
    private TableColumn<UserDTO, String> friendtable_lastname;
    @FXML
    private TableColumn<UserDTO, String> friendtable_email;
    @FXML
    private TableView<ItemDTO> friendwishlisttableview;
    @FXML
    private TableColumn<ItemDTO, String> itemnamecolumn;
    @FXML
    private TableColumn<ItemDTO, Double> itemprice;
    @FXML
    private TableColumn<ItemDTO, Double> totalcontributioncolumn;
    @FXML
    private Button frinendwishlist;
    @FXML
    private TextField conamounttext;

    public double balance;

    ObservableList<UserDTO> showfriendlistData;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    @FXML
    private TextField Searchitem;
    @FXML
    private Label friendRequests_label;
    @FXML
    private Label marketLabel;
    @FXML
    private ImageView logo;
    @FXML
    private Text aboutUsLabel1;
    @FXML
    private Tab AboutPage;
    @FXML
    private Button updateBtn;

    public void startUpdatingTasks() {
        // Schedule the tasks to run every N seconds (adjust the delay and period as needed)
        scheduler.scheduleAtFixedRate(this::updateTasks, 0, 3, TimeUnit.SECONDS);
    }

    @FXML
    public void updateTasks() {
        Platform.runLater(() -> {
            setCurrentUser();
            showMywishlistItemsData();
            initializeNotificationsListView();
            showAllItemsData();
            initializeconnectPage();
            initializefriendrequstPage();
            showfriendlistData();
            getBalance();
        });
    }

    public void stopUpdatingTasks() {
        scheduler.shutdownNow();
    }

    public void initialize() {
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        setUserEmail(email);  // Set the email first

    }

    public void setUserEmail(String email) {
        userEmailLabel.setText(email);
        System.out.println("Received data in MainPageController: " + email);
        showMywishlistItemsData();
        initializeNotificationsListView();
        showAllItemsData();
        initializeconnectPage();
        initializefriendrequstPage();
        showfriendlistData();
        getBalance();
        Platform.runLater(() -> {
            setCurrentUser();

        });
    }

    public UserDTO getCurrentUserData(String email) {
        UserDTO currentUser = null;

        try {
            ClientSocketManager.initializeSocket();
            ClientSocketManager.getOutputStream().println("getCurrentUserData"); // request type
            ClientSocketManager.getOutputStream().println(email); // user email

            String jsonCurrentUser = ClientSocketManager.getInputStream().readLine();
            Gson gson = new Gson();

            if (jsonCurrentUser != null) {
                currentUser = gson.fromJson(jsonCurrentUser, UserDTO.class);

                if (currentUser != null) {
                    System.out.println("User data retrieved successfully:");
                    System.out.println("First Name: " + currentUser.getFirstName());
                    System.out.println("Last Name: " + currentUser.getLastName());
                    // Add more fields if needed
                } else {
                    showAlert("User data is null. Failed to retrieve current user data.");
                }
            } else {
                showAlert("Received null JSON for user data.");
            }

        } catch (IOException ex) {
            ex.printStackTrace();
            showAlert("An error occurred while fetching current user data.");
        } catch (RuntimeException it) {
            it.printStackTrace();
            showAlert("An unexpected error occurred.");
        }

        return currentUser;
    }

    public void setCurrentUser() {
        cUser = getCurrentUserData(email);
        user_lname_label.setText(cUser.getLastName());
        user_fname_label.setText(cUser.getFirstName());
        balance_label.setText(String.valueOf(balance));
        //balance = cUser.getBalance();

    }

    @FXML
    public void logout() {
        Alert confirmLogout = new Alert(Alert.AlertType.CONFIRMATION);
        confirmLogout.setTitle("Confirm Logout");
        confirmLogout.setHeaderText(null);
        confirmLogout.setContentText("Are you sure you want to logout?");

        Optional<ButtonType> result = confirmLogout.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            // User clicked "OK," perform logout
            Stage currentStage = (Stage) logout_btn.getScene().getWindow();
            currentStage.hide();

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("LoginRegisterFXMLDocument.fxml"));
                Parent root = loader.load();
                Stage stage = new Stage();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.setTitle("I-Wish App");

                // Set the close request handler
                stage.setOnCloseRequest(event -> {
                    Platform.exit(); // Close the JavaFX application
                    System.exit(0);  // Terminate the Java Virtual Machine
                });

                stage.show();
            } catch (IOException ex) {
                Logger.getLogger(MainPageController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            // User clicked "Cancel" or closed the dialog, do nothing
        }
    }

    @FXML
    public void openRechargeWindow() {

        try {
            FXMLLoader rechargeSceneLoader = new FXMLLoader(getClass().getResource("RechargeScene.fxml"));
            Parent rechargeSceneRoot = rechargeSceneLoader.load();
            RechargeSceneController rechargeSceneController = rechargeSceneLoader.getController();
            rechargeSceneController.setEmail(userEmailLabel.getText());

            Stage stage = new Stage();
            Scene scene = new Scene(rechargeSceneRoot);
            stage.setScene(scene);
            stage.setTitle("Recharge Balance");

            stage.show();
        } catch (IOException ex) {
            Logger.getLogger(MainPageController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void updateBalance(double newBalance) {
        balance_label.setText(String.format("Balance: $%.2f", newBalance));
    }

    private ObservableList<MyWishlistItemDTO> initializeUserWishlist(String email) {
        ObservableList<MyWishlistItemDTO> uiWishlistItems = FXCollections.observableArrayList();

        ClientSocketManager.getOutputStream().println("getUserWishlist");// request type
        ClientSocketManager.getOutputStream().println(email); // email
        try {
            String jsonWishlist = ClientSocketManager.getInputStream().readLine();
            Gson gson = new Gson();
            Type listType = new TypeToken<List<MyWishlistItemDTO>>() {
            }.getType();

            List<MyWishlistItemDTO> wishlistItems = gson.fromJson(jsonWishlist, listType);

            // Process the received wishlistItems
            if (wishlistItems != null && !wishlistItems.isEmpty()) {
                uiWishlistItems.addAll(wishlistItems);

            } else {
                System.out.println("Wishlist is empty or couldn't be retrieved.");
            }

        } catch (IOException ex) {
            Logger.getLogger(MainPageController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return uiWishlistItems;
    }

    public void showMywishlistItemsData() {
        myWishlistItemsData = initializeUserWishlist(email);
        wishlist_itemNameColumn.setCellValueFactory(new PropertyValueFactory<>("item_name"));
        wishlist_itemPriceColumn.setCellValueFactory(new PropertyValueFactory<>("item_price"));
        wishlist_friendsContributionColumn.setCellValueFactory(new PropertyValueFactory<>("total_contribution"));
        wishlistTable.setItems(myWishlistItemsData);
    }

    @FXML
    public void openContributersWindow() {
        ObservableList<MyContributersDTO> myContributersList = FXCollections.observableArrayList();

        MyWishlistItemDTO myWishlistItemDTO = wishlistTable.getSelectionModel().getSelectedItem();

        // Check if an item is selected
        if (myWishlistItemDTO == null) {
            // Handle the case when nothing is selected, e.g., show an alert
            showAlert("Please select an item from the wishlist.");
            return;
        }

        try {
            String itemName = myWishlistItemDTO.getItem_name();
            ClientSocketManager.initializeSocket();
            ClientSocketManager.getOutputStream().println("ShowContributers"); // request type
            ClientSocketManager.getOutputStream().println(email); // user email
            ClientSocketManager.getOutputStream().println(itemName); // item id

            String jsonContributerslist = ClientSocketManager.getInputStream().readLine();
            Gson gson = new Gson();
            Type listType = new TypeToken<List<MyContributersDTO>>() {
            }.getType();

            List<MyContributersDTO> contributersList = gson.fromJson(jsonContributerslist, listType);
            System.out.println(contributersList);
            for (MyContributersDTO c : contributersList) {
                System.out.println("contributor_first_name: " + c.getContributor_first_name());
                System.out.println("contributor_last_name: " + c.getContributor_last_name());
                System.out.println("contributor_email: " + c.getContributor_email());
                System.out.println("contribution_amount: " + c.getContribution_amount());
            }
            myContributersList.addAll(contributersList);

        } catch (IOException ex) {
            ex.printStackTrace();
            // Handle IOException, e.g., show an alert
            showAlert("An error occurred while fetching contributors.");
        } catch (RuntimeException it) {
            it.printStackTrace();
            // Handle RuntimeException, e.g., show an alert
            showAlert("An unexpected error occurred.");
        }
        contributers_email_column.setCellValueFactory(new PropertyValueFactory<>("contributor_email"));
        contributers_amount_column.setCellValueFactory(new PropertyValueFactory<>("contribution_amount"));
        contributers_tableview.setItems(myContributersList);
    }

    public ObservableList<NotificationDTO> initializeNotifications(String email) {
        ObservableList<NotificationDTO> notifications = FXCollections.observableArrayList();

        try {
            ClientSocketManager.getOutputStream().println("getUserNotifications");
            ClientSocketManager.getOutputStream().println(email); // user email

            String jsonNotificationsList = ClientSocketManager.getInputStream().readLine();

            Gson gson = new Gson();
            Type listType = new TypeToken<List<NotificationDTO>>() {
            }.getType();

            List<NotificationDTO> notificationsList = gson.fromJson(jsonNotificationsList, listType);

            if (notificationsList != null && !notificationsList.isEmpty()) {
                notifications.addAll(notificationsList);
            } else {
                System.out.println("Notifications list is empty or couldn't be retrieved.");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            showAlert("An error occurred while fetching notifications.");
        } catch (RuntimeException it) {
            it.printStackTrace();
            showAlert("An unexpected error occurred.");
        }

        return notifications;
    }

    private void updateNotificationsTableView() {
        ObservableList<NotificationDTO> notifications = initializeNotifications(email);
        notification_column.setCellValueFactory(new PropertyValueFactory<>("notification_text"));
        notifications_tableView.setItems(notifications);
    }

    private void initializeNotificationsListView() {
        ObservableList<NotificationDTO> notifications = initializeNotifications(email);
        notification_column.setCellValueFactory(new PropertyValueFactory<>("notification_text"));
        notifications_tableView.setItems(notifications);
    }

    @FXML
    public void clearNotifications() {
        // Check if there are notifications to clear
        if (notifications_tableView.getItems().isEmpty()) {
            showAlert("No notifications to clear.");
            return;
        }

        try {
            ClientSocketManager.initializeSocket();
            ClientSocketManager.getOutputStream().println("clearNotifications"); // request type
            ClientSocketManager.getOutputStream().println(email); // user email

            String response = ClientSocketManager.getInputStream().readLine();
            if (response.equals("success")) {
                showAlert("Notifications cleared successfully.");
            } else {
                showAlert("Failed to clear notifications.");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            showAlert("An error occurred.");
        } finally {
            // Refresh the notifications after clearing all notifications
            initializeNotificationsListView();
        }
    }

    @FXML
    public void removeNotification() {
        NotificationDTO selectedNotification = notifications_tableView.getSelectionModel().getSelectedItem();
        // Check if a notification is selected
        if (selectedNotification == null) {
            // Handle the case when nothing is selected, e.g., show an alert
            showAlert("Please select a notification to remove.");
            return;
        }

        try {
            ClientSocketManager.initializeSocket();
            ClientSocketManager.getOutputStream().println("removeNotification"); // request type
            //ClientSocketManager.getOutputStream().println(email); // user email
            ClientSocketManager.getOutputStream().println(selectedNotification.getNotification_id()); // notification id

            String response = ClientSocketManager.getInputStream().readLine();
            if (response.equals("success")) {
                showAlert("Notification removed successfully.");
            } else {
                showAlert("Failed to remove notification.");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            showAlert("An error occurred.");
        } finally {
            // Refresh the notifications after removing a notification
            initializeNotificationsListView();
        }
    }

    @FXML
    public void clearUserWishlist() {
        try {
            ClientSocketManager.initializeSocket();
            ClientSocketManager.getOutputStream().println("clearUserWishlist"); // request type
            ClientSocketManager.getOutputStream().println(email); // user email

            String response = ClientSocketManager.getInputStream().readLine();
            if (response.equals("success")) {
                showAlert("Wishlist cleared successfully.");
            } else {
                showAlert("Failed to clear wishlist.");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            showAlert("An error occurred.");
        } finally {
            showMywishlistItemsData();
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    public void handleRemoveWishlistItemButton() {
        MyWishlistItemDTO selectedWishlistItem = wishlistTable.getSelectionModel().getSelectedItem();
        if (selectedWishlistItem != null) {
            String itemId = selectedWishlistItem.getId();
            removeWishlistItem(email, itemId);
        } else {
            showAlert("Please select an item from the wishlist.");
        }
    }

    public void removeWishlistItem(String email, String itemId) {
        try {
            ClientSocketManager.initializeSocket();
            ClientSocketManager.getOutputStream().println("removeItemFromWishlist"); // request type
            ClientSocketManager.getOutputStream().println(email); // user email
            ClientSocketManager.getOutputStream().println(itemId); // item id

            String response = ClientSocketManager.getInputStream().readLine();
            if (response.equals("success")) {
                showAlert(Alert.AlertType.INFORMATION, "Information Message", "Item removed from wishlist successfully.");
            } else {
                showAlert("Failed to remove item from wishlist.");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            showAlert("An error occurred.");
        } finally {
            // Refresh the wishlist items after removing an item
            showMywishlistItemsData();
        }
    }

    private void initializeconnectPage() {
        ClientSocketManager.getOutputStream().println("getallusersRequest");// request type

        try {
            String response = ClientSocketManager.getInputStream().readLine();
            // System.out.println("email   "+email);
            Type listType = new TypeToken<List<UserDTO>>() {
            }.getType();
            Gson gson = new Gson();

            List<UserDTO> alluserlist = gson.fromJson(response, listType);

            ObservableList<UserDTO> alluserui = FXCollections.observableArrayList();
            alluserui.addAll(alluserlist);
            allUsersEmail_column.setCellValueFactory(new PropertyValueFactory<>("email"));
            allUsersfirstName_column.setCellValueFactory(new PropertyValueFactory<>("firstName"));
            allusersLastName_column.setCellValueFactory(new PropertyValueFactory<>("lastName"));
            allUsersTable.setItems(alluserui);

        } catch (IOException ex) {
            Logger.getLogger(MainPageController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @FXML
    private void addfreindaction(MouseEvent event) {
        if (allUsersTable.getSelectionModel().getSelectedItem() == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Please select a user! ", ButtonType.OK);
            alert.show();
        } else {
            UserDTO userDTO = allUsersTable.getSelectionModel().getSelectedItem();
            String toemail = userDTO.getEmail();
            System.out.println(toemail + " toemail ");

            ClientSocketManager.initializeSocket();

            ClientSocketManager.getOutputStream().println("addfriendRequest");// request type

            System.out.println("addfriend   " + toemail);
            ClientSocketManager.getOutputStream().println(toemail);
            try {
                String response = ClientSocketManager.getInputStream().readLine();
                System.out.println("response  " + response);
            } catch (IOException ex) {
                Logger.getLogger(MainPageController.class.getName()).log(Level.SEVERE, null, ex);
            }
            allUsersTable.getItems().remove(userDTO);
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Add friend Success! ", ButtonType.OK);
            alert.show();

        }
    }

    private void initializefriendrequstPage() {

        ClientSocketManager.getOutputStream().println("AllfriendRequest");// request type
        String response = null;
        try {
            response = ClientSocketManager.getInputStream().readLine();
        } catch (IOException ex) {
            Logger.getLogger(MainPageController.class.getName()).log(Level.SEVERE, null, ex);
        }
        // System.out.println("email   "+email);
        Type listType = new TypeToken<List<FriendRequestDTO>>() {
        }.getType();
        Gson gson = new Gson();
        System.out.println(response);

        List<FriendRequestDTO> allfriendRequest = gson.fromJson(response, listType);

        ObservableList<FriendRequestDTO> allfriendRequestui = FXCollections.observableArrayList();
        allfriendRequestui.addAll(allfriendRequest);
        requestfriendtEmailName_col.setCellValueFactory(new PropertyValueFactory<>("sender_email"));
        friendrequest_Date_col.setCellValueFactory(new PropertyValueFactory<>("requestDate"));
        friend_request.setItems(allfriendRequestui);

    }

    @FXML
    private void Accept_friend_action(MouseEvent event) {
        if (friend_request.getSelectionModel().getSelectedItem() == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Please select a friend request! ", ButtonType.OK);
            alert.show();
        } else {

            ClientSocketManager.initializeSocket();
            FriendRequestDTO friendRequestDTO = friend_request.getSelectionModel().getSelectedItem();
            String fromemail = friendRequestDTO.getSender_email();

            ClientSocketManager.getOutputStream().println("AcceptfriendRequest");// request type
            ClientSocketManager.getOutputStream().println(fromemail);
            String response;
            try {
                response = ClientSocketManager.getInputStream().readLine();
                System.out.println("AcceptfriendRequest  " + response);
                friend_request.getItems().remove(friendRequestDTO);

            } catch (IOException ex) {
                Logger.getLogger(MainPageController.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

    @FXML
    private void Cancel_friend_action(MouseEvent event) {
        if (friend_request.getSelectionModel().getSelectedItem() == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Please select a friend request! ", ButtonType.OK);
            alert.show();
        } else {

            FriendRequestDTO friendRequestDTO = friend_request.getSelectionModel().getSelectedItem();
            String fromemail = friendRequestDTO.getSender_email();

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Cancel freind request!");

            alert.setContentText("Are you sure cancel the freind request ?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                ClientSocketManager.initializeSocket();

                ClientSocketManager.getOutputStream().println("cancelfriendRequest");// request type
                ClientSocketManager.getOutputStream().println(fromemail);
                String response;
                try {

                    response = ClientSocketManager.getInputStream().readLine();
                    System.out.println("del  " + response);

                    friend_request.getItems().remove(friendRequestDTO);

                } catch (IOException ex) {
                    Logger.getLogger(MainPageController.class.getName()).log(Level.SEVERE, null, ex);
                }

            }

        }
    }

    private ObservableList<ItemDTO> inializeAllItems() {
        ObservableList<ItemDTO> uiAllItemsList = FXCollections.observableArrayList();

        try {
            ClientSocketManager.initializeSocket();
            ClientSocketManager.getOutputStream().println("getAllItems");

            String jsonAllItems = ClientSocketManager.getInputStream().readLine();

            Gson gson = new Gson();
            Type listType = new TypeToken<List<ItemDTO>>() {
            }.getType();
            List<ItemDTO> allitemsList = gson.fromJson(jsonAllItems, listType);

            Platform.runLater(() -> {
                uiAllItemsList.addAll(allitemsList);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        return uiAllItemsList;
    }

    public void showAllItemsData() {
        AllItemsData = inializeAllItems();
        m_col_itemid.setCellValueFactory(new PropertyValueFactory<>("itemId"));
        m_col_itemname.setCellValueFactory(new PropertyValueFactory<>("itemName"));
        m_col_itemPrice.setCellValueFactory(new PropertyValueFactory<>("itemPrice"));
        m_col_itemcategory.setCellValueFactory(new PropertyValueFactory<>("itemCategory"));

        m_itemsTable.setItems(AllItemsData);

    }

    @FXML
    public void addToWishlistBtn() {
        ItemDTO item = m_itemsTable.getSelectionModel().getSelectedItem();
        String id = String.valueOf(item.getItemId());
        System.out.println(id);
        try {
            ClientSocketManager.initializeSocket();
            ClientSocketManager.getOutputStream().println("addToWishlist");
            ClientSocketManager.getOutputStream().println(id);
            ClientSocketManager.getOutputStream().println(email);
            String response = ClientSocketManager.getInputStream().readLine();
            if ("succeed".equals(response)) {
                showAlert(Alert.AlertType.INFORMATION, "Information Message", "Item added to wishlist successfully!");
            } else {
                showAlert(Alert.AlertType.ERROR, "Error Message", "Failed to add item to wishlist");
            }

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Error Message", "Invalid input. Please enter valid numbers.");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            showMywishlistItemsData();
        }
    }

    public void showAlert(Alert.AlertType alertType, String title, String content) {
        Platform.runLater(() -> {
            Alert alert = new Alert(alertType);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(content);
            alert.showAndWait();
        });
    }

    @FXML
    private void Searchuser(KeyEvent event) {
        ClientSocketManager.initializeSocket();

        ClientSocketManager.getOutputStream().println("SearchUsers");// request type
        ClientSocketManager.getOutputStream().println(friendSearchTf.getText());
        String response;

        try {
            response = ClientSocketManager.getInputStream().readLine();
            System.out.println("SearchUsers  " + response);
            Type listType = new TypeToken<List<UserDTO>>() {
            }.getType();
            Gson gson = new Gson();

            List<UserDTO> alluserlist = gson.fromJson(response, listType);

            ObservableList<UserDTO> alluserui = FXCollections.observableArrayList();
            alluserui.addAll(alluserlist);
            allUsersEmail_column.setCellValueFactory(new PropertyValueFactory<>("email"));
            allUsersfirstName_column.setCellValueFactory(new PropertyValueFactory<>("firstName"));
            allusersLastName_column.setCellValueFactory(new PropertyValueFactory<>("lastName"));
            allUsersTable.setItems(alluserui);

        } catch (IOException ex) {
            Logger.getLogger(MainPageController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
////////////////////////////Abdelrahman:

    private ObservableList<UserDTO> initializeUserfriendlist(String email) {
        ObservableList<UserDTO> uifriendlist = FXCollections.observableArrayList();

        ClientSocketManager.getOutputStream().println("getUserfriendlist");// request type
        ClientSocketManager.getOutputStream().println(email); // email
        try {
            String jsonfriendlist = ClientSocketManager.getInputStream().readLine();
            Gson gson = new Gson();
            Type listType = new TypeToken<List<UserDTO>>() {
            }.getType();

            List<UserDTO> friendlistItems = gson.fromJson(jsonfriendlist, listType);

            // Process the received wishlistItems
            if (friendlistItems != null && !friendlistItems.isEmpty()) {
                uifriendlist.addAll(friendlistItems);

            } else {
                System.out.println("friendlist is empty or couldn't be retrieved.");
            }

        } catch (IOException ex) {
            Logger.getLogger(MainPageController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return uifriendlist;
    }

    @FXML
    public void showfriendlistData() {
        showfriendlistData = initializeUserfriendlist(email);

        friendtable_fristname.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        friendtable_lastname.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        friendtable_email.setCellValueFactory(new PropertyValueFactory<>("email"));
        // last 
        friendstableview.setItems(showfriendlistData);
    }

    @FXML
    public void openFriendWishlist() {
        ObservableList<ItemDTO> uiFriendWishlist = FXCollections.observableArrayList();

        // Assuming friendstableview contains UserDTO items
        UserDTO selectedFriend = friendstableview.getSelectionModel().getSelectedItem();

        if (selectedFriend != null) {
            try {
                String friendEmail = selectedFriend.getEmail();
                System.out.println(friendEmail);

                ClientSocketManager.initializeSocket();
                ClientSocketManager.getOutputStream().println("getUserFriendWishlist"); // Request type
                ClientSocketManager.getOutputStream().println(friendEmail); // Friend's email

                String jsonFriendWishlist = ClientSocketManager.getInputStream().readLine();

                Gson gson = new Gson();
                Type listType = new TypeToken<List<ItemDTO>>() {
                }.getType();

                List<ItemDTO> friendWishlistItems = gson.fromJson(jsonFriendWishlist, listType);

                System.out.println(friendWishlistItems);
                // Process the received wishlistItems
                if (friendWishlistItems != null && !friendWishlistItems.isEmpty()) {
                    uiFriendWishlist.addAll(friendWishlistItems);
                } else {
                    System.out.println("Friend's wishlist is empty or couldn't be retrieved.");
                }

            } catch (IOException ex) {
                Logger.getLogger(MainPageController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            System.out.println("No friend selected.");
        }

        itemnamecolumn.setCellValueFactory(new PropertyValueFactory<>("itemName"));
        itemprice.setCellValueFactory(new PropertyValueFactory<>("itemPrice"));
        totalcontributioncolumn.setCellValueFactory(new PropertyValueFactory<>("total_contribution"));
        friendwishlisttableview.setItems(uiFriendWishlist);

    }

    @FXML
    public void getBalance() {
        ObservableList<UserDTO> user_balance = FXCollections.observableArrayList();

        ClientSocketManager.getOutputStream().println("getUserbalance");// request type
        ClientSocketManager.getOutputStream().println(email); // email
        try {
            String jsonResponse = ClientSocketManager.getInputStream().readLine();
            Gson gson = new Gson();

            // Assuming the response is a double value
            balance = gson.fromJson(jsonResponse, Double.class);

            // Now you can use the 'balance' variable as needed in your client code
            System.out.println("Balance: " + balance);

        } catch (IOException ex) {
            Logger.getLogger(MainPageController.class.getName()).log(Level.SEVERE, null, ex);
        }
        balance_label.setText(String.valueOf(balance));
    }

    @FXML
    public void ContributeToFriendController() {
        // Get the entered amount from the text field
        String amount = conamounttext.getText();

        // Check if an item is selected
        ItemDTO selectedItem = friendwishlisttableview.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Contribution not completed");
            alert.setHeaderText("No item selected");
            alert.setContentText("Please select an item from the wishlist.");
            alert.show();
            return;  // Exit the method if no item is selected
        }

        // Check if the amount is empty
        if (amount.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Contribution not completed");
            alert.setHeaderText("Amount is required");
            alert.setContentText("Please enter the contribution amount.");
            alert.show();
            return;  // Exit the method if the amount is empty
        }

        try {
            double s_amount = Double.parseDouble(amount);

            // Check if the amount is non-negative
            if (s_amount < 0) {

                showAlert(Alert.AlertType.ERROR, "Contribution not completed", "Please enter a non-negative contribution amount.");
                return;  // Exit the method if the amount is negative
            }

            double item_price = selectedItem.getItemPrice();
            double item_contribution = selectedItem.getTotal_contribution();

            if (balance >= s_amount) {
                if (s_amount > (item_price - item_contribution)) {
                    Alert alert_contr = new Alert(Alert.AlertType.ERROR);
                    alert_contr.setTitle("Contribution not completed");
                    alert_contr.setHeaderText("Amount more than required");
                    String s = "The amount needed to help your friend is just " + (item_price - item_contribution);
                    alert_contr.setContentText(s);
                    alert_contr.show();
                    //showAlert(Alert.AlertType.ERROR,"Contribution not completed", "Amount more than required");
                } else {
                    UserDTO selectedFriend = friendstableview.getSelectionModel().getSelectedItem();
                    String friendEmail = selectedFriend.getEmail();
                    String itemname = selectedItem.getItemName();

                    // Send the contribution details to the server
                    ClientSocketManager.getOutputStream().println("ContributetoFriend"); // Request type
                    ClientSocketManager.getOutputStream().println(email); // user's email
                    ClientSocketManager.getOutputStream().println(friendEmail); // Friend's email
                    ClientSocketManager.getOutputStream().println(itemname); // item's name
                    ClientSocketManager.getOutputStream().println(amount);
                    // Receive the response from the server
                    String contributionResult = ClientSocketManager.getInputStream().readLine();

                    if (contributionResult.equals("succeed")) {
                        showAlert(Alert.AlertType.INFORMATION, "Contribution Successful", "Contribution completed", "Your contribution has been successfully added.");
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Contribution not completed", "Failed to contribute", "Unable to process your contribution. Please try again later.");
                    }
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Contribution not completed");
                alert.setHeaderText("Your Balance is not enough");
                String s = "You only have " + balance + " $ in your balance \nYou need to recharge your balance";
                System.out.println(balance);
                alert.setContentText(s);
                alert.show();
            }

        } catch (NumberFormatException e) {
            // Handle the case where the entered amount is not a valid number
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid Amount");
            alert.setHeaderText("Please enter a valid number for the contribution amount.");
            alert.show();
        } catch (IOException ex) {
            Logger.getLogger(MainPageController.class.getName()).log(Level.SEVERE, null, ex);
        }
        openFriendWishlist();
        getBalance();
        setCurrentUser();
    }

    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.show();
    }

    @FXML
    public void RemoveFriend() {
        UserDTO selectedFriend = friendstableview.getSelectionModel().getSelectedItem();
        String friendEmail = selectedFriend.getEmail();

        ClientSocketManager.initializeSocket();
        ClientSocketManager.getOutputStream().println("RemoveFriend"); // Request type
        ClientSocketManager.getOutputStream().println(email);// user's email
        ClientSocketManager.getOutputStream().println(friendEmail);// Friend's email
        showfriendlistData();
    }
///////////////////////////////////////////////////////////

    @FXML
    private void Searchitemsaction(KeyEvent event) {
        ObservableList<ItemDTO> uiAllItemsList = FXCollections.observableArrayList();
        ClientSocketManager.initializeSocket();
        ClientSocketManager.getOutputStream().println("SearchItems");
        ClientSocketManager.getOutputStream().println(Searchitem.getText());

        try {

            String jsonAllItems = ClientSocketManager.getInputStream().readLine();

            Gson gson = new Gson();
            Type listType = new TypeToken<List<ItemDTO>>() {
            }.getType();
            List<ItemDTO> allitemsList = gson.fromJson(jsonAllItems, listType);

            uiAllItemsList.addAll(allitemsList);

        } catch (Exception e) {
            e.printStackTrace();
        }

        m_col_itemid.setCellValueFactory(new PropertyValueFactory<>("itemId"));
        m_col_itemname.setCellValueFactory(new PropertyValueFactory<>("itemName"));
        m_col_itemPrice.setCellValueFactory(new PropertyValueFactory<>("itemPrice"));
        m_col_itemcategory.setCellValueFactory(new PropertyValueFactory<>("itemCategory"));

        m_itemsTable.setItems(uiAllItemsList);

    }
}
