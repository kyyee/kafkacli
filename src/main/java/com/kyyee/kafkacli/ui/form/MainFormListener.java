package com.kyyee.kafkacli.ui.form;

import cn.hutool.core.lang.Snowflake;
import com.intellij.uiDesigner.core.GridConstraints;
import com.kyyee.kafkacli.ui.configs.ClientCache;
import com.kyyee.kafkacli.ui.dialog.NewConnDialog;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.Config;
import org.apache.kafka.clients.admin.ConfigEntry;
import org.apache.kafka.clients.admin.TopicListing;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.KafkaFuture;
import org.apache.kafka.common.Node;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.config.ConfigResource;
import org.apache.kafka.common.serialization.StringDeserializer;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
public class MainFormListener {
    static GridConstraints gridConstraints = new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(200, 200), null, 0, false);

    public static void addListener(MainForm mainForm) {
        JPopupMenu clusterTreePopupMenu;
        clusterTreePopupMenu = new JPopupMenu();
        JMenuItem clusterTreeAddNewConnMenuItem = new JMenuItem("新建连接");
        clusterTreePopupMenu.add(clusterTreeAddNewConnMenuItem);
        JMenuItem clusterTreeRenameGroup = new JMenuItem("重命名");
        clusterTreePopupMenu.add(clusterTreeRenameGroup);

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
//            if (selectionNode.isLeaf()) {
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
                            Optional<TopicListing> topicOptional = topics.stream().filter(topic -> topic.name().equals(topicName)).findFirst();
                            if (topicOptional.isPresent()) {
                                TopicForm topicForm = TopicForm.getInstance();
                                topicForm.getTopicNameTextField().setText(topicOptional.get().name());
                                // todo 替换"192.168.21.247:9092"
                                try (KafkaConsumer<String, String> kafkaConsumer = new KafkaConsumer<>(Map.of(
                                    ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.3.200:9092",
                                    ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
                                    ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
                                    ConsumerConfig.GROUP_ID_CONFIG, new Snowflake().nextIdStr(),
                                    ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,"earliest"
                                ))) {
                                    record(topicName, topicForm, kafkaConsumer);
                                    partition(topicName, topicForm, kafkaConsumer);
                                }
                                config(topicName, topicForm, adminClient);


                                MainForm.getInstance().getDataPanel().add(topicForm.getContentPanel(), gridConstraints);
                                MainForm.getInstance().getDataPanel().updateUI();
                            }
                        } catch (InterruptedException | ExecutionException | TimeoutException ex) {
                            throw new RuntimeException(ex);
                        }

                    }
                    case "consumerGroups" -> {

                    }
                    default -> throw new IllegalStateException("Unexpected value: " + parent.toString());
                }
//            }
        });

        clusterTreeAddNewConnMenuItem.addActionListener(e -> {
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

    private static void config(String topicName, TopicForm topicForm, AdminClient adminClient) {
        String[] headers = {"index", "key", "value"};
        DefaultTableModel model = new DefaultTableModel(null, headers);
        JTable configTable = topicForm.getConfigTable();

        configTable.getTableHeader().setVisible(true);
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setPreferredSize(new Dimension(configTable.getWidth(), 10));
        configTable.getTableHeader().setDefaultRenderer(renderer);

        configTable.setModel(model);
        Object[] data = new Object[3];
        try {
            Map<ConfigResource, Config> values = adminClient.describeConfigs(List.of(new ConfigResource(ConfigResource.Type.TOPIC, topicName))).all().get(5, TimeUnit.SECONDS);
            int i = 0;
            for (Config config : values.values()) {
                for (ConfigEntry configEntry : config.entries()) {
                    data[0] = i;
                    data[1] = configEntry.name();
                    data[2] = configEntry.value();
                    model.addRow(data);
                    i++;
                }
            }
        } catch (InterruptedException | ExecutionException | TimeoutException ignored) {
        }
    }

    private static void partition(String topicName, TopicForm topicForm, KafkaConsumer<String, String> kafkaConsumer) {
        String[] headers = {"index", "id", "leader"};
        DefaultTableModel model = new DefaultTableModel(null, headers);
        JTable partitionTable = topicForm.getPartitionTable();

        partitionTable.getTableHeader().setVisible(true);
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setPreferredSize(new Dimension(partitionTable.getWidth(), 10));
        partitionTable.getTableHeader().setDefaultRenderer(renderer);

        partitionTable.setModel(model);
        Object[] data = new Object[3];
        List<PartitionInfo> partitionInfos = kafkaConsumer.partitionsFor(topicName, Duration.of(5, ChronoUnit.SECONDS));
        int i = 0;
        for (PartitionInfo partitionInfo : partitionInfos) {
            data[0] = i;
            data[1] = partitionInfo.partition();
            data[2] = ObjectUtils.isEmpty(partitionInfo.leader()) ? "" : partitionInfo.leader().host() + ":" + partitionInfo.leader().port();
            model.addRow(data);
            i++;
        }
    }

    private static void record(String topicName, TopicForm topicForm, KafkaConsumer<String, String> kafkaConsumer) {
        kafkaConsumer.subscribe(List.of(topicName));
        String[] headers = {"index", "partition", "offset", "key", "value", "timestamp"};
        DefaultTableModel model = new DefaultTableModel(null, headers);
        JTable dataTable = topicForm.getDataTable();

        dataTable.getTableHeader().setVisible(true);
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setPreferredSize(new Dimension(dataTable.getWidth(), 10));
        dataTable.getTableHeader().setDefaultRenderer(renderer);

        dataTable.setModel(model);
        Object[] data = new Object[6];
        ConsumerRecords<String, String> records = kafkaConsumer.poll(Duration.of(5, ChronoUnit.SECONDS));
        int i = 0;
        for (ConsumerRecord<String, String> record : records) {
            data[0] = i;
            data[1] = record.partition();
            data[2] = record.offset();
            data[3] = record.key();
            data[4] = record.value();
            data[5] = record.timestamp();
            model.addRow(data);
            i++;
        }
    }
}
