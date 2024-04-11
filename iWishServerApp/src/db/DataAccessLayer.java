package db;

import dto.FriendRequestDTO;
import dto.ItemDTO;
import dto.MyContributersDTO;
import dto.UserDTO;
import dto.MyWishlistItemDTO;
import dto.NotificationDTO;

import java.sql.*;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DataAccessLayer {

    private Connection connect;
    private PreparedStatement prepare;
    private ResultSet result;

    public Connection ConnectDb() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connect = DriverManager.getConnection("jdbc:mysql://localhost:3306/iwish2", "root", "root");
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(DataAccessLayer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return connect;
    }

    public boolean login(String email, String password) {
        try {
            connect = ConnectDb();
            String sql = "SELECT * FROM users WHERE email = ? AND password = ?";
            try {
                prepare = connect.prepareStatement(sql);
                prepare.setString(1, email);
                prepare.setString(2, password);
                result = prepare.executeQuery();
                return result.next();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        } finally {
            closeDatabaseResources();
        }
        return false;
    }

    public boolean register(String username, String password, String email, String firstName, String lastName, String balance) {
        try {
            connect = ConnectDb();

            String checkUserExists = "SELECT * FROM users WHERE username = ? OR email = ?";
            String insertUser = "INSERT INTO users (username, password, email, firstName, lastName, balance) VALUES (?, ?, ?, ?, ?, ?)";

            try {
                prepare = connect.prepareStatement(checkUserExists);
                prepare.setString(1, username);
                prepare.setString(2, email);
                result = prepare.executeQuery();

                if (result.next()) {
                    return false;
                }

                prepare = connect.prepareStatement(insertUser);
                prepare.setString(1, username);
                prepare.setString(2, password);
                prepare.setString(3, email);
                prepare.setString(4, firstName);
                prepare.setString(5, lastName);
                prepare.setString(6, balance);
                int rowsAffected = prepare.executeUpdate();

                return rowsAffected > 0;

            } catch (SQLException e) {
                e.printStackTrace();
            }

        } catch (NullPointerException e) {
            e.printStackTrace();
        } finally {
            closeDatabaseResources();
        }
        return false;
    }

    public UserDTO getCurrentUserData(String email) {
        UserDTO currentUser = null;

        try {
            connect = ConnectDb();
            String getCurrentUserDataQuery = "SELECT * FROM users WHERE email = ?";

            try (PreparedStatement preparedStatement = connect.prepareStatement(getCurrentUserDataQuery)) {
                preparedStatement.setString(1, email);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        String username = resultSet.getString("username");
                        String password = resultSet.getString("password");
                        String firstName = resultSet.getString("firstName");
                        String lastName = resultSet.getString("lastName");
                        int balance = resultSet.getInt("balance");

                        currentUser = new UserDTO(username, password, email, firstName, lastName, balance);
                    }
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            Logger.getLogger(DataAccessLayer.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (connect != null) {
                closeDatabaseResources();
            }
        }

        return currentUser;
    }

    public int rechargeBalance(String user, String amount) {
        try {
            connect = ConnectDb();
            String updateBalance = "UPDATE users SET balance = balance + ?\n"
                    + "WHERE email = ?;";
            prepare = connect.prepareStatement(updateBalance);
            prepare.setString(1, amount);
            prepare.setString(2, user);
            prepare.executeUpdate();
            closeDatabaseResources();
            return 1;

        } catch (SQLException ex) {
            Logger.getLogger(DataAccessLayer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public void closeDatabaseResources() {
        try {
            if (result != null) {
                result.close();
            }
            if (prepare != null) {
                prepare.close();
            }
            if (connect != null) {
                connect.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(DataAccessLayer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Vector<UserDTO> retrieveallusers() {
        connect = ConnectDb();
        Vector<UserDTO> alluser = null;
        try {
            alluser = new Vector<UserDTO>();
            prepare = connect.prepareStatement("select * from friend_request where sender_email = ? ");
            result = prepare.executeQuery();

            while (result.next()) {
                UserDTO udto = new UserDTO();
                udto.setFirstName(result.getString(1));

                udto.setLastName(result.getString(2));

                udto.setEmail(result.getString(3));
                alluser.add(udto);
            }
        } catch (SQLException ex) {
            Logger.getLogger(DataAccessLayer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return alluser;

    }

    public Vector<ItemDTO> getAllItems() {
        Vector<ItemDTO> allItemList = new Vector<>();
        try {
            connect = ConnectDb();
            prepare = connect.prepareStatement("SELECT * FROM items;");
            result = prepare.executeQuery();

            while (result.next()) {
                int itemId = result.getInt("item_id");
                String itemName = result.getString("item_name");
                double itemPrice = result.getDouble("item_price");
                String itemCategory = result.getString("item_category");

                ItemDTO item = new ItemDTO(itemId, itemName, itemPrice, itemCategory);
                allItemList.add(item);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            Logger.getLogger(DataAccessLayer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return allItemList;
    }

    public boolean addToWishlist(String id, String userEmail) {
        try {
            System.out.println("emaill" + userEmail);
            System.out.println("iddd" + id);
            connect = ConnectDb();
            String checkStatement = ("SELECT * FROM wish_list WHERE item_id = ? "
                    + "and user_id =(SELECT user_id FROM users WHERE email = ?) ");

            String insertStatement = "INSERT INTO wish_list ( item_id, user_id) VALUES "
                    + "(?, "
                    + "(SELECT user_id FROM users WHERE email = ?))";

            try {
                prepare = connect.prepareStatement(checkStatement);
                prepare.setString(1, id);
                prepare.setString(2, userEmail);
                result = prepare.executeQuery();
                if (result.next()) {
                    return false;
                }
                prepare = connect.prepareStatement(insertStatement);
                prepare.setString(1, id);
                prepare.setString(2, userEmail);
                int rowsAffected = prepare.executeUpdate();

                return rowsAffected > 0;
            } catch (SQLException ex) {
                ex.printStackTrace();

            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Vector<MyWishlistItemDTO> getUserWishlist(String email) {
        Vector<MyWishlistItemDTO> wishlistItems = new Vector<>();
        try {
            connect = ConnectDb();
            String getWishlist = "SELECT\n"
                    + "    w.wish_id,\n"
                    + "    i.item_name,\n"
                    + "    i.item_price,\n"
                    + "    COALESCE(SUM(c.contribution_amount), 0) AS total_friends_contribution\n"
                    + "FROM\n"
                    + "    wish_list w\n"
                    + "JOIN\n"
                    + "    items i ON w.item_id = i.item_id\n"
                    + "LEFT JOIN\n"
                    + "    contributions c ON w.wish_id = c.wish_id\n"
                    + "WHERE\n"
                    + "    w.user_id = (SELECT user_id FROM users WHERE email = ?)\n"
                    + "GROUP BY\n"
                    + "    w.wish_id,\n"
                    + "    i.item_name,\n"
                    + "    i.item_price;;";
            try (PreparedStatement preparedStatement = connect.prepareStatement(getWishlist)) {
                preparedStatement.setString(1, email);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        String item_id = resultSet.getString("wish_id");
                        String itemName = resultSet.getString("item_name");
                        String itemPrice = resultSet.getString("item_price");
                        double totalContribution = resultSet.getDouble("total_friends_contribution");

                        MyWishlistItemDTO wishlistItem = new MyWishlistItemDTO(item_id, itemName, itemPrice, totalContribution);
                        wishlistItems.add(wishlistItem);
                    }
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace(); // Print the exception details
            Logger.getLogger(DataAccessLayer.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            // Close resources (connect) here if needed
            if (connect != null) {
                closeDatabaseResources();
            }
        }

        return wishlistItems;
    }

    public Vector<MyContributersDTO> getContributerslist(String email, String itemName) {
        Vector<MyContributersDTO> contributersList = new Vector<>();
        try {
            connect = ConnectDb();
            String getContributersList = "SELECT\n"
                    + "    u.firstName AS contributor_first_name,\n"
                    + "    u.lastName AS contributor_last_name,\n"
                    + "    u.email AS contributor_email,\n"
                    + "    c.contribution_amount\n"
                    + "FROM\n"
                    + "    users u\n"
                    + "JOIN\n"
                    + "    contributions c ON u.user_id = c.contributor_id\n"
                    + "JOIN\n"
                    + "    wish_list w ON c.wish_id = w.wish_id\n"
                    + "JOIN\n"
                    + "    items i ON w.item_id = i.item_id\n"
                    + "WHERE\n"
                    + "    w.user_id = (SELECT user_id FROM users WHERE email = ?) \n"
                    + "    AND i.item_name = ?;";

            try (PreparedStatement preparedStatement = connect.prepareStatement(getContributersList)) {
                preparedStatement.setString(1, email);
                preparedStatement.setString(2, itemName);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        String contributor_first_name = resultSet.getString("contributor_first_name");
                        String contributor_last_name = resultSet.getString("contributor_last_name");
                        String contributor_email = resultSet.getString("contributor_email");
                        double contribution_amount = resultSet.getDouble("contribution_amount");

                        MyContributersDTO contributer = new MyContributersDTO(contributor_first_name, contributor_last_name,
                                contributor_email, contribution_amount);
                        contributersList.add(contributer);
                    }
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            Logger.getLogger(DataAccessLayer.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (connect != null) {
                closeDatabaseResources();
            }
        }
        return contributersList;
    }

    public boolean clearUserWishlist(String email) {
        try {
            connect = ConnectDb();

            // Delete contributions associated with the user's wishlist
            String deleteContributions = "DELETE FROM contributions\n"
                    + "WHERE wish_id IN (SELECT wish_id FROM wish_list WHERE user_id = (SELECT user_id FROM users WHERE email = ?))";

            prepare = connect.prepareStatement(deleteContributions);
            prepare.setString(1, email);
            prepare.executeUpdate();

            // Delete wishlist items
            String deleteWishlist = "DELETE FROM wish_list WHERE user_id = (SELECT user_id FROM users WHERE email = ?)";

            prepare = connect.prepareStatement(deleteWishlist);
            prepare.setString(1, email);
            prepare.executeUpdate();

            return true;
        } catch (SQLException ex) {
            ex.printStackTrace();
            Logger.getLogger(DataAccessLayer.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            closeDatabaseResources();
        }
        return false;
    }

    public boolean removeItemFromWishlist(String email, String itemId) {
        try {
            connect = ConnectDb();

            // Delete contributions associated with the item
            String deleteContributions = "DELETE FROM contributions\n"
                    + "WHERE wish_id = ?";

            prepare = connect.prepareStatement(deleteContributions);
            prepare.setString(1, itemId);
            prepare.executeUpdate();

            // Delete the item from the wishlist
            String deleteWishlistItem = "DELETE FROM wish_list WHERE user_id = (SELECT user_id FROM users WHERE email = ?)\n"
                    + "AND wish_id = ?";

            prepare = connect.prepareStatement(deleteWishlistItem);
            prepare.setString(1, email);
            prepare.setString(2, itemId);
            prepare.executeUpdate();

            return true;
        } catch (SQLException ex) {
            ex.printStackTrace();
            Logger.getLogger(DataAccessLayer.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            closeDatabaseResources();
        }
        return false;
    }

    public Vector<NotificationDTO> getUserNotifications(String userEmail) {
        Vector<NotificationDTO> notifications = new Vector<>();

        try {
            connect = ConnectDb();

            String getNotifications = "SELECT * FROM notifications WHERE user_email = ?";

            try (PreparedStatement preparedStatement = connect.prepareStatement(getNotifications)) {
                preparedStatement.setString(1, userEmail);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        int notificationId = resultSet.getInt("notification_id");
                        int fromUserId = resultSet.getInt("from_user_id");
                        String notificationText = resultSet.getString("notification_text");
                        boolean isNotified = resultSet.getBoolean("is_notified");
                        int wishId = resultSet.getInt("wish_id");

                        NotificationDTO notification = new NotificationDTO(
                                notificationId, userEmail, fromUserId, notificationText, isNotified, wishId);

                        notifications.add(notification);
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            Logger.getLogger(DataAccessLayer.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            closeDatabaseResources();
        }

        return notifications;
    }

    public boolean removeNotification(int notificationId) {
        try {
            connect = ConnectDb();

            // Delete the specified notification
            String deleteNotification = "DELETE FROM notifications WHERE notification_id = ?";

            prepare = connect.prepareStatement(deleteNotification);
            prepare.setInt(1, notificationId);
            int rowsAffected = prepare.executeUpdate();

            return rowsAffected > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            Logger.getLogger(DataAccessLayer.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            closeDatabaseResources();
        }
        return false;
    }

    public boolean clearUserNotifications(String userEmail) {
        try {
            connect = ConnectDb();

            // Delete all notifications for the specified user
            String deleteNotifications = "DELETE FROM notifications WHERE user_email = ?";

            prepare = connect.prepareStatement(deleteNotifications);
            prepare.setString(1, userEmail);
            prepare.executeUpdate();

            return true;
        } catch (SQLException ex) {
            ex.printStackTrace();
            Logger.getLogger(DataAccessLayer.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            closeDatabaseResources();
        }
        return false;
    }

    //////////////////friend_request
    public Vector<UserDTO> retrieveallusers(String cuemail) {
        connect = ConnectDb();
        Vector<UserDTO> alluser = null;
        try {
            alluser = new Vector<UserDTO>();
            prepare = connect.prepareStatement("SELECT *\n"
                    + "   FROM users\n"
                    + "   WHERE email NOT IN (\n"
                    + "   SELECT ?  \n"
                    + "   UNION\n"
                    + "  SELECT receiver_email FROM friend_request WHERE sender_email = ?\n"
                    + "  UNION\n"
                    + "  SELECT sender_email FROM friend_request WHERE receiver_email = ? \n"
                    + "  );");
            prepare.setString(1, cuemail);
            prepare.setString(2, cuemail);
            prepare.setString(3, cuemail);
            result = prepare.executeQuery();

            while (result.next()) {

                UserDTO udto = new UserDTO();
                udto.setFirstName(result.getString("firstName"));

                udto.setLastName(result.getString("lastName"));

                udto.setEmail(result.getString("email"));
                alluser.add(udto);
            }
        } catch (SQLException ex) {
            Logger.getLogger(DataAccessLayer.class.getName()).log(Level.SEVERE, null, ex);
        }

        return alluser;

    }

    public boolean addfriend(String from_email, String to_email) {
        connect = ConnectDb();
        try {
            prepare = connect.prepareStatement("insert into friend_request (sender_email, receiver_email"
                    + ",request_date) "
                    + " values(?,?,?)");

            prepare.setString(1, from_email);
            prepare.setString(2, to_email);
            long millis = System.currentTimeMillis();
            java.sql.Date date = new java.sql.Date(millis);

            prepare.setDate(3, date);

            prepare.executeUpdate();
            closeDatabaseResources();
            return true;

        } catch (SQLException ex) {
            Logger.getLogger(DataAccessLayer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public Vector<FriendRequestDTO> AllfriendRequest(String cuemail) {
        connect = ConnectDb();
        Vector<FriendRequestDTO> allFriendRequest = null;
        allFriendRequest = new Vector<FriendRequestDTO>();
        // System.out.println("cuemail     ??????"+cuemail);
        try {
            prepare = connect.prepareStatement("select * from friend_request where receiver_email = ?"
                    + "and is_accepted IS NULL ");
            prepare.setString(1, cuemail);

            result = prepare.executeQuery();
            while (result.next()) {
                FriendRequestDTO friendrequestdto = new FriendRequestDTO();
                friendrequestdto.setSender_email(result.getString(2));

                friendrequestdto.setRequestDate(result.getDate(5));
                allFriendRequest.add(friendrequestdto);

            }

        } catch (SQLException ex) {
            Logger.getLogger(DataAccessLayer.class.getName()).log(Level.SEVERE, null, ex);
        }

        return allFriendRequest;
    }

    public boolean cancelfriendRequest(String fromUserEmail, String toUserEmail) {
        connect = ConnectDb();
        try {
            prepare = connect.prepareStatement("DELETE FROM friend_request "
                    + "WHERE sender_email = ? AND receiver_email = ? And is_accepted IS NULL ");
            /* System.out.println("DDDDDDDDDDD");
          
            System.out.println(fromUserEmail);
            System.out.println(toUserEmail);*/

            prepare.setString(1, fromUserEmail);
            prepare.setString(2, toUserEmail);
            prepare.executeUpdate();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(DataAccessLayer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public boolean AcceptfriendRequest(String fromUserEmail, String toUserEmail) {
        connect = ConnectDb();
        try {
            prepare = connect.prepareStatement("UPDATE friend_request SET is_accepted = TRUE "
                    + "WHERE sender_email = ? AND receiver_email = ? ");
            prepare.setString(1, fromUserEmail);
            prepare.setString(2, toUserEmail);
            prepare.executeUpdate();
            return true;

        } catch (SQLException ex) {
            Logger.getLogger(DataAccessLayer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;

    }

    public Vector<UserDTO> SearchUsers(String cuemail, String emaillike) {
        connect = ConnectDb();
        Vector<UserDTO> allusersearch = null;
        try {
            System.out.println("SSSSSSSSSSSSSSS");
            allusersearch = new Vector<UserDTO>();
            prepare = connect.prepareStatement("SELECT * FROM users WHERE email like ?  and email  NOT IN (\n"
                    + "   SELECT ?  UNION \n"
                    + "    SELECT receiver_email FROM friend_request WHERE sender_email = ?  \n"
                    + "   UNION\n"
                    + "   SELECT sender_email FROM friend_request WHERE receiver_email = ? );");
            prepare.setString(1, emaillike + "%");

            prepare.setString(2, cuemail);
            prepare.setString(3, cuemail);
            prepare.setString(4, cuemail);
            result = prepare.executeQuery();

            while (result.next()) {

                UserDTO udto = new UserDTO();
                udto.setFirstName(result.getString("firstName"));

                udto.setLastName(result.getString("lastName"));

                udto.setEmail(result.getString("email"));
                allusersearch.add(udto);
            }
        } catch (SQLException ex) {
            Logger.getLogger(DataAccessLayer.class.getName()).log(Level.SEVERE, null, ex);
        }

        return allusersearch;
    }

    public Vector<UserDTO> getFriends(String email) {

        Vector<UserDTO> myfrindlist = new Vector<>();

        try {
            connect = ConnectDb();
            String getfriendlist = "select firstname, lastname, email"
                    + " from friend_request ,users where (email = sender_email OR email = receiver_email)"
                    + " and (sender_email = ? OR receiver_email =?) "
                    + "and is_accepted = 1 and email <> ?";
            try (PreparedStatement preparedStatement = connect.prepareStatement(getfriendlist)) {
                preparedStatement.setString(1, email);
                preparedStatement.setString(2, email);
                preparedStatement.setString(3, email);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        String first_name = resultSet.getString("firstname");
                        String last_name = resultSet.getString("lastname");
                        String useremail = resultSet.getString("email");

                        UserDTO frindldto = new UserDTO(first_name, last_name, useremail);
                        myfrindlist.add(frindldto);
                    }
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace(); // Print the exception details
            Logger.getLogger(DataAccessLayer.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            // Close resources (connect) here if needed
            if (connect != null) {
                closeDatabaseResources();
            }
        }

        return myfrindlist;
    }

    public Vector<ItemDTO> getFriendwhislist(String femail) {
        Vector<ItemDTO> friendwhislist = new Vector<>();
        try {
            System.out.println(femail);
            connect = ConnectDb();
            String getFriendWishlistQuery = "SELECT i.item_name, i.item_price, SUM(c.contribution_amount) AS Total_Contribution "
                    + "FROM items i JOIN wish_list w ON i.item_id = w.item_id "
                    + "LEFT JOIN contributions c ON w.wish_id = c.wish_id "
                    + "WHERE w.user_id = (SELECT user_id FROM users WHERE email = ? ) "
                    + "GROUP BY i.item_name, i.item_price;";

            try (PreparedStatement preparedStatement = connect.prepareStatement(getFriendWishlistQuery)) {
                preparedStatement.setString(1, femail); // using email instead of femail for the first placeholder

                try (ResultSet resultSet = preparedStatement.executeQuery()) {

                    while (resultSet.next()) {
                        String itemname = resultSet.getString("item_Name");
                        double itemprice = resultSet.getInt("item_Price");
                        double totalcontribution = resultSet.getInt("Total_Contribution");

                        ItemDTO fwishlist = new ItemDTO(itemname, itemprice, totalcontribution);
                        friendwhislist.add(fwishlist);

                    }
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            Logger.getLogger(DataAccessLayer.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (connect != null) {
                closeDatabaseResources();
            }
        }
        return friendwhislist;
    }

    public int contributiontofriend(String email, String femail, String itemname, String contribution_amount) {
        try {
            connect = ConnectDb();
            String contributiontofriend = "INSERT INTO contributions (contributor_id, wish_id, contribution_amount)"
                    + " VALUES ((SELECT user_id FROM users WHERE email = ?),"
                    + " (SELECT wish_id FROM wish_list w, users u, items i WHERE w.user_id = "
                    + " (SELECT user_id FROM users WHERE email = ?) AND "
                    + " w.item_id = (SELECT item_id FROM items WHERE item_name = ?) AND"
                    + " w.item_id = i.item_id AND w.user_id = u.user_id),?);";

            // Subtract the contribution amount from the user's balance
            String updateBalance = "UPDATE users SET balance = balance - ? WHERE email = ?;";

            // Use a transaction to ensure both queries are executed or none
            connect.setAutoCommit(false);

            try (PreparedStatement contributionStatement = connect.prepareStatement(contributiontofriend);
                    PreparedStatement updateBalanceStatement = connect.prepareStatement(updateBalance)) {

                contributionStatement.setString(1, email);
                contributionStatement.setString(2, femail);
                contributionStatement.setString(3, itemname);
                contributionStatement.setString(4, contribution_amount);
                contributionStatement.executeUpdate();

                updateBalanceStatement.setString(1, contribution_amount);
                updateBalanceStatement.setString(2, email);
                updateBalanceStatement.executeUpdate();

                connect.commit();
                return 1;

            } catch (SQLException ex) {
                connect.rollback(); // Rollback the transaction in case of an exception
                Logger.getLogger(DataAccessLayer.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                connect.setAutoCommit(true); // Reset auto-commit mode
            }
        } catch (SQLException ex) {
            Logger.getLogger(DataAccessLayer.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (connect != null) {
                closeDatabaseResources();
            }
        }
        return 0;
    }

    public int removeFriend(String email, String femail) {
        try {
            connect = ConnectDb();
            System.out.println(femail);
            String deleteFriendQuery = "DELETE FROM friend_request "
                    + "WHERE "
                    + "    (sender_email = ? AND receiver_email = ? AND is_accepted = 1) "
                    + "    OR "
                    + "    (sender_email = ? AND receiver_email = ? AND is_accepted = 1);";
            try (PreparedStatement preparedStatement = connect.prepareStatement(deleteFriendQuery)) {
                preparedStatement.setString(1, email);
                preparedStatement.setString(2, femail);
                preparedStatement.setString(3, femail);
                preparedStatement.setString(4, email);
                preparedStatement.executeUpdate();
                return 1;
            }
        } catch (SQLException ex) {
            Logger.getLogger(DataAccessLayer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public Vector<UserDTO> getBalance(String email) {

        Vector<UserDTO> userBalance = new Vector<>();

        try {
            connect = ConnectDb();
            String getuserBalance = "select balance from users where email = ?;";
            try (PreparedStatement preparedStatement = connect.prepareStatement(getuserBalance)) {
                preparedStatement.setString(1, email);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        double balance = resultSet.getDouble("balance");

                        UserDTO userbalance2 = new UserDTO(balance);
                        userBalance.add(userbalance2);
                    }
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace(); // Print the exception details
            Logger.getLogger(DataAccessLayer.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            // Close resources (connect) here if needed
            if (connect != null) {
                closeDatabaseResources();
            }
        }

        return userBalance;
    }

    public Vector<ItemDTO> SearchItems(String itemlike) {
        Vector<ItemDTO> allItemList = new Vector<>();
        try {
            connect = ConnectDb();
            prepare = connect.prepareStatement("SELECT * FROM items where item_name like ? ;");
            prepare.setString(1, itemlike + "%");

            result = prepare.executeQuery();

            while (result.next()) {
                int itemId = result.getInt("item_id");
                String itemName = result.getString("item_name");
                double itemPrice = result.getDouble("item_price");
                String itemCategory = result.getString("item_category");

                ItemDTO item = new ItemDTO(itemId, itemName, itemPrice, itemCategory);
                allItemList.add(item);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            Logger.getLogger(DataAccessLayer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return allItemList;
    }

}
