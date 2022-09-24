package com.kyyee.kafkacli.ui.form;

import com.kyyee.kafkacli.ui.dialog.NewConnDialog;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.tree.TreePath;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

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
        clusterTree.addMouseListener(new MouseAdapter() {
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
        mainForm.getButtonCreateConn().addActionListener(e -> {
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
