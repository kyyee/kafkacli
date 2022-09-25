package com.kyyee.kafkacli.common.component.server;

import com.kyyee.kafkacli.ui.Ui;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;

import java.io.Closeable;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

@Slf4j
public class SocketServer implements Closeable {
    public static int port = 60000;

    private ServerSocket server;
    private static SocketServer socketServer;


    public static SocketServer getInstance() {
        if (socketServer == null) {
            socketServer = new SocketServer();
        }
        return socketServer;
    }

    public final void run() {
        //查找没有占用的端口
        while (port < 65535) {
            try {
                server = new ServerSocket(port);
                log.info("server start");
            } catch (Exception ignored) {
                port++;
            }
            break;
        }

        while (true) {
            //监听客户端是否有连接
            try (Socket socket = server.accept()) {
                //窗口在任务栏闪动
                log.info("has start server");
                Ui.showMainFrame();

//                if (App.mainFrame.getExtendedState() == Frame.ICONIFIED) {
//                    App.mainFrame.setExtendedState(Frame.NORMAL);
//                } else {
//                    App.mainFrame.toFront();
//                    App.mainFrame.requestFocus();
//                    App.mainFrame.repaint();
//                }
            } catch (Exception ignored) {
            }
        }
    }

    public void close() {
        try {
            if (ObjectUtils.isNotEmpty(server)) {
                server.close();
            }
        } catch (IOException ignored) {
        }
    }
}
