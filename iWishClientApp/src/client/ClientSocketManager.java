/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

/**
 * @author ahmed
 */
public class ClientSocketManager {

    private static Socket socket;
    private static DataInputStream dis; // ear
    private static PrintStream ps; // mouth

    public static void initializeSocket() {
        try {
            socket = new Socket("127.0.0.1", 5005);
            dis = new DataInputStream(socket.getInputStream());
            ps = new PrintStream(socket.getOutputStream());
        } catch (java.net.ConnectException e) {
            // Handle connection refused exception with an alert
            Platform.runLater(() -> {
                showAlert("Connection Error", "Could not connect to the server. Make sure the server is running.");
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static DataInputStream getInputStream() {
        return dis;
    }

    public static PrintStream getOutputStream() {
        return ps;
    }

    public static void closeSocket() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void showAlert(String title, String content) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
