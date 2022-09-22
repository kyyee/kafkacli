package com.kyyee.kafkacli.ui.form;

import com.kyyee.kafkacli.ui.dialog.NewConnDialog;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.tree.TreePath;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

@Slf4j
public class MainFormListener {

    public static void addListener(MainForm mainForm) {
        JPopupMenu clusterTreePopupMenu;
        clusterTreePopupMenu = new JPopupMenu();
        JMenuItem addNewConnectionItem = new JMenuItem("新建连接");
        JMenuItem renameGroup = new JMenuItem("重命名");
        clusterTreePopupMenu.add(addNewConnectionItem);
        clusterTreePopupMenu.add(renameGroup);

        JTree clusterTree = mainForm.getClusterTree();
        clusterTree.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {
                TreePath path = clusterTree.getPathForLocation(e.getX(), e.getY());
                if (path == null) {
                    return;
                }

                clusterTree.setSelectionPath(path);

                if (e.getButton() == 3) {
                    clusterTreePopupMenu.show(clusterTree, e.getX(), e.getY());
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });

        addNewConnectionItem.addActionListener(e -> {
            try {
                NewConnDialog dialog = new NewConnDialog();
                dialog.pack();
                dialog.setVisible(true);
            } catch (Exception exception) {
                log.error("create new connection dialog failed. {}", exception.getMessage());
            }
        });
        mainForm.getCreateConnectButton().addActionListener(e -> {
            try {
                NewConnDialog dialog = new NewConnDialog();
                dialog.pack();
                dialog.setVisible(true);
            } catch (Exception exception) {
                log.error("create new connection dialog failed. {}", exception.getMessage());
            }
        });

    }
}
