package com.kyyee.kafkacli;

import com.kyyee.kafkacli.ui.Ui;
import com.kyyee.kafkacli.ui.frame.MainFrame;

import java.awt.*;

public class App {
    public static MainFrame mainFrame;
    public static SystemTray systemTray;
    public static TrayIcon trayIcon;
    public static PopupMenu popupMenu;
    public static void main(String[] args) {
        Ui.initApp();
    }
}
