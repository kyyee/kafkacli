package com.kyyee.kafkacli.ui.form;

import com.intellij.uiDesigner.core.GridConstraints;
import com.kyyee.kafkacli.ui.configs.ClientCache;
import com.kyyee.kafkacli.ui.dialog.NewConnDialog;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.TopicListing;
import org.apache.kafka.common.Node;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
public class MainFormListener {

    public static void addListener(MainForm mainForm) {
        JPopupMenu clusterTreePopupMenu;
        clusterTreePopupMenu = new JPopupMenu();
        JMenuItem addNewConnectionItem = new JMenuItem("新建连接");
        clusterTreePopupMenu.add(addNewConnectionItem);
        JMenuItem renameGroup = new JMenuItem("重命名");
        clusterTreePopupMenu.add(renameGroup);

        JPopupMenu brokerTreePopupMenu;
        brokerTreePopupMenu = new JPopupMenu();
        JMenuItem brokerRefreshMenuItem = new JMenuItem("刷新");
        brokerTreePopupMenu.add(brokerRefreshMenuItem);

        JPopupMenu topicTreePopupMenu;
        topicTreePopupMenu = new JPopupMenu();
        JMenuItem topicRefreshMenuItem = new JMenuItem("刷新");
        topicTreePopupMenu.add(topicRefreshMenuItem);
        JMenuItem topicCreateMenuItem = new JMenuItem("创建topic");
        topicTreePopupMenu.add(topicCreateMenuItem);

        JPopupMenu consumerGroupTreePopupMenu;
        consumerGroupTreePopupMenu = new JPopupMenu();
        JMenuItem consumerGroupRefreshMenuItem = new JMenuItem("刷新");
        consumerGroupTreePopupMenu.add(consumerGroupRefreshMenuItem);


        JTree clusterTree = mainForm.getClusterTree();
        clusterTree.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                DefaultMutableTreeNode selectionNode = (DefaultMutableTreeNode) clusterTree.getLastSelectedPathComponent();
                Object userObject = selectionNode.getUserObject();
                switch (selectionNode.toString()) {
                    case "cluster" -> {
                        if (e.getButton() == 3) {
                            clusterTreePopupMenu.show(clusterTree, e.getX(), e.getY());
                        }
                    }
                    case "brokers" -> {
                        if (e.getButton() == 3) {
                            brokerTreePopupMenu.show(clusterTree, e.getX(), e.getY());
                        }
                    }
                    case "topics" -> {
                        if (e.getButton() == 3) {
                            topicTreePopupMenu.show(clusterTree, e.getX(), e.getY());
                        }
                    }
                    case "consumerGroups" -> {
                        if (e.getButton() == 3) {
                            consumerGroupTreePopupMenu.show(clusterTree, e.getX(), e.getY());
                        }
                    }
                }

            }
        });

        clusterTree.addTreeSelectionListener(e -> {
            DefaultMutableTreeNode selectionNode = (DefaultMutableTreeNode) clusterTree.getLastSelectedPathComponent();
            if (selectionNode.isLeaf()) {
                // 叶子节点
                TreeNode parent = selectionNode.getParent();
                if (!ObjectUtils.isNotEmpty(parent)) {
                    return;
                }
                switch (parent.toString()) {
                    case "brokers" -> {
                        String clusterName = parent.getParent().toString();
                        AdminClient adminClient = ClientCache.get(clusterName);
                        String nodeId = selectionNode.toString();
                        try {
                            Collection<Node> nodes = adminClient.describeCluster().nodes().get(5, TimeUnit.SECONDS);
                            Optional<Node> nodeOptional = nodes.stream().filter(node -> node.idString().equals(nodeId)).findFirst();
                            if (nodeOptional.isPresent()) {
                                BrokerForm brokerForm = BrokerForm.getInstance();
                                brokerForm.getIdTextField().setText(nodeOptional.get().idString());
                                brokerForm.getHostTextField().setText(nodeOptional.get().host());
                                brokerForm.getPortTextField().setText(String.valueOf(nodeOptional.get().port()));
                                brokerForm.getRackTextField().setText(nodeOptional.get().rack());
                                GridConstraints gridConstraints = new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(200, 200), null, 0, false);
                                MainForm.getInstance().getDataPanel().add(brokerForm.getContentPanel(), gridConstraints);
                            }
                        } catch (InterruptedException | ExecutionException | TimeoutException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                    case "topics" -> {
                        String clusterName = parent.getParent().toString();
                        AdminClient adminClient = ClientCache.get(clusterName);
                        String topicName = selectionNode.toString();
                        try {
                            Collection<TopicListing> topics = adminClient.listTopics().listings().get(5, TimeUnit.SECONDS);
                            Optional<TopicListing> nodeOptional = topics.stream().filter(topic -> topic.name().equals(topicName)).findFirst();
                            if (nodeOptional.isPresent()) {
                                //todo
                            }
                        } catch (InterruptedException | ExecutionException | TimeoutException ex) {
                            throw new RuntimeException(ex);
                        }

                    }
                    case "consumerGroups" -> {

                    }
                    default -> throw new IllegalStateException("Unexpected value: " + parent.toString());
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

//        mainForm.getButtonCreateConn().addActionListener(e -> {
//            try {
//                NewConnDialog dialog = new NewConnDialog();
//                dialog.pack();
//                dialog.setVisible(true);
//            } catch (Exception exception) {
//                log.error("create new connection dialog failed. {}", exception.getMessage());
//            }
//        });

    }
}
