/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.restaurant;

import com.restaurant.view.ProduitView;
import javax.swing.SwingUtilities;

/**
 *
 * @author ASUS
 */



public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ProduitView view = new ProduitView();
            view.setVisible(true);
        });
    }
}