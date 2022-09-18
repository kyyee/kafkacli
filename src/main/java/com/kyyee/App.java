package com.kyyee;

import com.kyyee.ui.frame.MainFrame;

import javax.swing.*;

public class App {
    public static MainFrame mainFrame;

    public static void main(String[] args) {

        mainFrame = new MainFrame();
        mainFrame.init();
        mainFrame.pack();
        mainFrame.setVisible(true);
        mainFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    }
}
