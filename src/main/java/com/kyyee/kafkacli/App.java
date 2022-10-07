package com.kyyee.kafkacli;

import com.kyyee.kafkacli.common.component.server.SocketServer;
import com.kyyee.kafkacli.ui.frame.MainFrame;
import lombok.extern.slf4j.Slf4j;

import java.net.Socket;

@Slf4j
public class App {

    public static void main(String[] args) {
        try (Socket ignored = new Socket("localhost", SocketServer.getInstance().getPort())) {
            MainFrame.shutdown();
        } catch (Exception ignored) {
        }
        MainFrame.initTheme();
        MainFrame.getInstance().init();

        SocketServer.getInstance().run();
    }
}
