package com.restaurant;

import com.restaurant.controller.LoginController;
import com.restaurant.utils.DatabaseUpdater;
import com.restaurant.view.SplashScreen;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {

    public static void main(String[] args) {
        DatabaseUpdater.updateSchema();
        setupLookAndFeel();
        SwingUtilities.invokeLater(() -> {
            SplashScreen splash = new SplashScreen();
            splash.afficherEtAnimer(2000, () -> new LoginController().afficherLogin());
        });
    }

    private static void setupLookAndFeel() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (javax.swing.UnsupportedLookAndFeelException | ClassNotFoundException
                | InstantiationException | IllegalAccessException e) {
        }
    }
}