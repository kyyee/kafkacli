package com.kyyee.kafkacli.ui.dialog;

import com.kyyee.framework.common.exception.BaseErrorCode;
import com.kyyee.framework.common.exception.BaseException;
import com.kyyee.kafkacli.service.impl.AdminClientServiceImpl;
import com.kyyee.kafkacli.ui.form.MainForm;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.ConsumerGroupListing;
import org.apache.kafka.clients.admin.PartitionReassignment;
import org.apache.kafka.clients.admin.TopicListing;
import org.apache.kafka.common.Node;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.helpers.MessageFormatter;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

@Slf4j
public class NewConnListener {

    public static void addListener(NewConnDialog dialog) {
        // call onCancel() when cross is clicked
        dialog.setDefaultCloseOperation(dialog.DO_NOTHING_ON_CLOSE);
        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dialog.dispose();
            }
        });
        // call onCancel() on ESCAPE
        dialog.getContentPane().registerKeyboardAction(e -> dialog.dispose(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        dialog.getButtonCancel().addActionListener(e -> dialog.dispose());

        dialog.getClusterVersionComboBox().addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                log.info("select cluster version:{}", e.getItem());
            }
        });

        dialog.getButtonOK().addActionListener(e -> {

            String bootstrapServers = dialog.getBootstrapServersTextField().getText();

            try (AdminClient adminClient = new AdminClientServiceImpl().connect(bootstrapServers)) {
                buildTree(dialog, adminClient);
            } catch (Exception exception) {
                log.info("connect kafka failed. {}", exception.getMessage());
                JOptionPane.showMessageDialog(dialog, "连接失败！请检查配置\n\n", "失败", JOptionPane.ERROR_MESSAGE);
                throw BaseException.of(BaseErrorCode.CONNECTION_FAILED);
            }
        });

        dialog.getButtonTest().addActionListener(e -> {
            String bootstrapServers = dialog.getBootstrapServersTextField().getText();

            try (AdminClient adminClient = new AdminClientServiceImpl().connect(bootstrapServers)) {
                Collection<Node> nodes = adminClient
                    .describeCluster()
                    .nodes()
                    .get(5, TimeUnit.SECONDS);
                String nodesStr = nodes
                    .stream()
                    .map(node -> node.host() + ":" + node.port())
                    .collect(Collectors.joining("\n"));
                String[] options = {"确定", "取消"};
                int response = JOptionPane.showOptionDialog(dialog, MessageFormatter.arrayFormat("连接成功！\n\n节点数量：{}\n节点列表：\n{}", new Object[]{nodes.size(), nodesStr}).getMessage(),
                    "成功", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
                if (response == 0) {
                    log.info("按下确定按钮!");
                    buildTree(dialog, adminClient);
                }
            } catch (Exception exception) {
                log.info("connect kafka failed. {}", exception.getMessage());
                JOptionPane.showMessageDialog(dialog, "连接失败！\n\n", "失败", JOptionPane.ERROR_MESSAGE);
                throw BaseException.of(BaseErrorCode.CONNECTION_FAILED);
            }
        });

    }

    private static void buildTree(NewConnDialog dialog, AdminClient adminClient) throws InterruptedException, ExecutionException, TimeoutException {
        String clusterName = dialog.getClusterNameTextField().getText();
        String clusterVersion = Objects.requireNonNull(dialog.getClusterVersionComboBox().getSelectedItem()).toString();
        MainForm mainForm = MainForm.getInstance();
        mainForm.setAdminClient(adminClient);
        DefaultTreeModel rootTreeNode = (DefaultTreeModel) mainForm.getClusterTree().getModel();
        MutableTreeNode root = (MutableTreeNode) rootTreeNode.getRoot();
        // 判断同名节点是否存在
        while (root.children().hasMoreElements()) {
            String nodeName = root.children().nextElement().toString();
            if (nodeName.equals(clusterName)) {
                JOptionPane.showMessageDialog(dialog, root + "上已存在同名节点！\n\n", "新建连接失败", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        DefaultMutableTreeNode clientTreeNode = new DefaultMutableTreeNode(clusterName);

        Collection<Node> nodes = adminClient.describeCluster().nodes().get(5, TimeUnit.SECONDS);
        DefaultMutableTreeNode brokersTreeNode = new DefaultMutableTreeNode("brokers");
        for (Node node : nodes) {
            DefaultMutableTreeNode brokerTreeNode = new DefaultMutableTreeNode(node.idString(), false);
            brokersTreeNode.add(brokerTreeNode);
        }
        clientTreeNode.add(brokersTreeNode);

        Collection<TopicListing> topics = adminClient.listTopics().listings().get(5, TimeUnit.SECONDS);
        DefaultMutableTreeNode topicsTreeNode = new DefaultMutableTreeNode("topics");
        for (TopicListing topic : topics) {
            DefaultMutableTreeNode topicTreeNode = new DefaultMutableTreeNode(topic.name());
            topicsTreeNode.add(topicTreeNode);
            Map<TopicPartition, PartitionReassignment> partition = adminClient.listPartitionReassignments().reassignments().get(5, TimeUnit.SECONDS);

        }
        clientTreeNode.add(topicsTreeNode);

        Collection<ConsumerGroupListing> consumerGroups = adminClient.listConsumerGroups().all().get(5, TimeUnit.SECONDS);
        DefaultMutableTreeNode consumerGroupsTreeNode = new DefaultMutableTreeNode("consumerGroups");
        for (ConsumerGroupListing consumerGroup : consumerGroups) {
            DefaultMutableTreeNode consumerGroupTreeNode = new DefaultMutableTreeNode(consumerGroup.groupId(), false);
            consumerGroupsTreeNode.add(consumerGroupTreeNode);
        }
        clientTreeNode.add(consumerGroupsTreeNode);

        rootTreeNode.insertNodeInto(clientTreeNode, root, root.getChildCount());

        dialog.dispose();
    }
}
