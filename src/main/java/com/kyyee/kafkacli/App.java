package com.kyyee.kafkacli;

import com.kyyee.kafkacli.common.component.server.SocketServer;
import com.kyyee.kafkacli.ui.Ui;
import com.kyyee.kafkacli.ui.frame.MainFrame;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.net.Socket;

@Slf4j
public class App {

    public static MainFrame mainFrame;
    public static SystemTray systemTray;
    public static TrayIcon trayIcon;
    public static PopupMenu popupMenu;
    public static void main(String[] args) {
        App.mainFrame = new MainFrame();
        try {
            new Socket("localhost", SocketServer.port).close();
            Ui.shutdown();
        } catch (Exception ignored) {}
        Ui.initApp();

        SocketServer.getInstance().run();
    }
}
