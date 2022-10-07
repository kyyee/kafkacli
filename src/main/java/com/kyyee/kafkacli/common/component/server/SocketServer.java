package com.kyyee.kafkacli.common.component.server;

import com.kyyee.kafkacli.ui.frame.MainFrame;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;

import java.io.Closeable;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

@Slf4j
@Getter
public class SocketServer implements Closeable {
    private int port = 60000;

    private ServerSocket server;
    private static SocketServer instance;


    public static SocketServer getInstance() {
        if (instance == null) {
            synchronized (SocketServer.class) {
                instance = new SocketServer();
            }
        }
        return instance;
    }

    public final void run() {
        //查找没有占用的端口
        while (port < 65535) {
            try {
                server = new ServerSocket(port);
                log.info("KafkaCli initialized with port(s): {} (socket)", port);
                break;
            } catch (Exception ignored) {
                port++;
            }
        }

        while (true) {
            //监听客户端是否有连接
            try (Socket ignored = server.accept()) {
                //窗口在任务栏闪动
                log.info("KafkaCli has started, show App mainFrame");
                MainFrame.showMainFrame();
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
