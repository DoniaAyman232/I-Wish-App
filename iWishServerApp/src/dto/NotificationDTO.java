package dto;



public class NotificationDTO {
    private int notification_id;
    private String user_email;
    private int from_user_id;
    private String notification_text;
    private boolean is_notified;
    private int wish_id;

    // Constructors, getters, and setters

    public NotificationDTO(int notification_id, String user_email, int from_user_id, String notification_text,
                            boolean is_notified, int wish_id) {
        this.notification_id = notification_id;
        this.user_email = user_email;
        this.from_user_id = from_user_id;
        this.notification_text = notification_text;
        this.is_notified = is_notified;
        this.wish_id = wish_id;
    }

    public NotificationDTO(int notificationId, String userEmail, int fromUserId, String notificationText, boolean notified) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public int getNotification_id() {
        return notification_id;
    }

    public void setNotification_id(int notification_id) {
        this.notification_id = notification_id;
    }

    public String getUser_email() {
        return user_email;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

    public int getFrom_user_id() {
        return from_user_id;
    }

    public void setFrom_user_id(int from_user_id) {
        this.from_user_id = from_user_id;
    }

    public String getNotification_text() {
        return notification_text;
    }

    public void setNotification_text(String notification_text) {
        this.notification_text = notification_text;
    }

    public boolean isIs_notified() {
        return is_notified;
    }

    public void setIs_notified(boolean is_notified) {
        this.is_notified = is_notified;
    }

    public int getWish_id() {
        return wish_id;
    }

    public void setWish_id(int wish_id) {
        this.wish_id = wish_id;
    }
}
