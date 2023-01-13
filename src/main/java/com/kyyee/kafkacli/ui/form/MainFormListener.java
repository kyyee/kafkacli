package com.kyyee.kafkacli.ui.form;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.thread.ThreadUtil;
import com.intellij.uiDesigner.core.GridConstraints;
import com.kyyee.framework.common.exception.BaseErrorCode;
import com.kyyee.framework.common.exception.BaseException;
import com.kyyee.kafkacli.ui.configs.ClientCache;
import com.kyyee.kafkacli.ui.dialog.CreateTopicDialog;
import com.kyyee.kafkacli.ui.dialog.NewConnDialog;
import com.kyyee.kafkacli.ui.dialog.NewConnListener;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.kafka.clients.admin.*;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.Node;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.config.ConfigResource;
import org.apache.kafka.common.serialization.StringDeserializer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
public class MainFormListener {
    public static final String GROUP_ID = new Snowflake().nextIdStr();
    static GridConstraints gridConstraints = new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false);

    public static void addListener(MainForm mainForm) {
        JPopupMenu clusterTreePopupMenu;
        clusterTreePopupMenu = new JPopupMenu();
        JMenuItem clusterTreeAddNewConnMenuItem = new JMenuItem("新建连接");
        clusterTreePopupMenu.add(clusterTreeAddNewConnMenuItem);
        JMenuItem clusterTreeRenameGroup = new JMenuItem("重命名");
        clusterTreePopupMenu.add(clusterTreeRenameGroup);

        JPopupMenu brokersTreePopupMenu;
        brokersTreePopupMenu = new JPopupMenu();
        JMenuItem brokersRefreshMenuItem = new JMenuItem("刷新");
        brokersTreePopupMenu.add(brokersRefreshMenuItem);

        JPopupMenu topicsTreePopupMenu;
        topicsTreePopupMenu = new JPopupMenu();
        JMenuItem topicsRefreshMenuItem = new JMenuItem("刷新");
        topicsTreePopupMenu.add(topicsRefreshMenuItem);
        JMenuItem topicsCreateMenuItem = new JMenuItem("创建topic");
        topicsTreePopupMenu.add(topicsCreateMenuItem);


        JPopupMenu topicTreePopupMenu;
        topicTreePopupMenu = new JPopupMenu();
        JMenuItem topicDeleteMenuItem = new JMenuItem("删除");
        topicTreePopupMenu.add(topicDeleteMenuItem);

        JPopupMenu consumerGroupsTreePopupMenu;
        consumerGroupsTreePopupMenu = new JPopupMenu();
        JMenuItem consumerGroupsRefreshMenuItem = new JMenuItem("刷新");
        consumerGroupsTreePopupMenu.add(consumerGroupsRefreshMenuItem);

        JPopupMenu consumerGroupTreePopupMenu;
        consumerGroupTreePopupMenu = new JPopupMenu();
        JMenuItem consumerGroupDeleteMenuItem = new JMenuItem("删除");
        consumerGroupTreePopupMenu.add(consumerGroupDeleteMenuItem);


        JTree clusterTree = mainForm.getClusterTree();
        clusterTree.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                DefaultMutableTreeNode selectionNode = (DefaultMutableTreeNode) clusterTree.getLastSelectedPathComponent();
                if (ObjectUtils.isEmpty(selectionNode)) {
                    return;
                }
                Object userObject = selectionNode.getUserObject();
                switch (selectionNode.toString()) {
                    case "cluster" -> {
                        if (e.getButton() == 3) {
                            clusterTreePopupMenu.show(clusterTree, e.getX(), e.getY());
                        }
                    }
                    case "brokers" -> {
                        if (e.getButton() == 3) {
                            brokersTreePopupMenu.show(clusterTree, e.getX(), e.getY());
                        }
                    }
                    case "topics" -> {
                        if (e.getButton() == 3) {
                            topicsTreePopupMenu.show(clusterTree, e.getX(), e.getY());
                        }
                    }
                    case "consumerGroups" -> {
                        if (e.getButton() == 3) {
                            consumerGroupsTreePopupMenu.show(clusterTree, e.getX(), e.getY());
                        }
                    }
                }
                if (ObjectUtils.isEmpty(selectionNode.getParent())) {
                    return;
                }
                if (selectionNode.getParent().toString().equals("topics")) {
                    if (e.getButton() == 3) {
                        topicTreePopupMenu.show(clusterTree, e.getX(), e.getY());
                    }
                }
                if (selectionNode.getParent().toString().equals("consumerGroups")) {
                    if (e.getButton() == 3) {
                        consumerGroupTreePopupMenu.show(clusterTree, e.getX(), e.getY());
                    }
                }

            }
        });

        clusterTree.addTreeSelectionListener(e -> {
            DefaultMutableTreeNode selectionNode = (DefaultMutableTreeNode) clusterTree.getLastSelectedPathComponent();
            if (ObjectUtils.isEmpty(selectionNode)) {
                return;
            }
            TreeNode parent = selectionNode.getParent();
            if (ObjectUtils.isEmpty(parent)) {
                return;
            }
            switch (parent.toString()) {
                case "brokers" -> {
                    String cluster = parent.getParent().toString();
                    AdminClient adminClient = ClientCache.get(cluster);
                    String nodeId = selectionNode.toString();
                    try {
                        Collection<Node> nodes = adminClient.describeCluster().nodes().get(5, TimeUnit.SECONDS);
                        Optional<Node> nodeOptional = nodes.stream().filter(node -> node.idString().equals(nodeId)).findFirst();
                        nodeOptional.ifPresent(node -> {
                            mainForm.getMainPanel().updateUI();
                            BrokerForm brokerForm = BrokerForm.getInstance();
                            mainForm.getDataPanel().removeAll();
                            mainForm.getDataPanel().add(brokerForm.getContentPanel(), gridConstraints);
                            mainForm.getMainPanel().updateUI();
                            ThreadUtil.execute(() -> {
                                brokerForm.getIdTextField().setText(node.idString());
                                brokerForm.getHostTextField().setText(node.host());
                                brokerForm.getPortTextField().setText(String.valueOf(node.port()));
                                brokerForm.getRackTextField().setText(node.rack());
                            });
                        });
                    } catch (InterruptedException | ExecutionException | TimeoutException ex) {
                        throw new RuntimeException(ex);
                    }
                }
                case "topics" -> {
                    String cluster = parent.getParent().toString();
                    AdminClient adminClient = ClientCache.get(cluster);
                    String topicName = selectionNode.toString();
                    try {
                        Collection<TopicListing> topics = adminClient.listTopics().listings().get(5, TimeUnit.SECONDS);
                        Optional<TopicListing> topicOptional = topics.stream().filter(topic -> topic.name().equals(topicName)).findFirst();
                        topicOptional.ifPresent(topic -> {
                            mainForm.getMainPanel().updateUI();
                            TopicForm topicForm = TopicForm.getInstance();
                            mainForm.getDataPanel().removeAll();
                            mainForm.getDataPanel().add(topicForm.getContentPanel(), gridConstraints);
                            mainForm.getMainPanel().updateUI();

                            ThreadUtil.execute(() -> {
                                topicForm.getTopicNameTextField().setText(topic.name());
                                try (KafkaConsumer<String, String> kafkaConsumer = new KafkaConsumer<>(Map.of(
                                    ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, cluster,
                                    ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
                                    ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
                                    ConsumerConfig.GROUP_ID_CONFIG, "kafkacli" + GROUP_ID,
                                    ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest"
                                )) {
                                }) {
                                    record(topicName, topicForm, kafkaConsumer);
                                    partition(topicName, topicForm, kafkaConsumer);
                                }
                                config(topicName, topicForm, adminClient);
                            });

                        });
                    } catch (InterruptedException | ExecutionException | TimeoutException ex) {
                        throw new RuntimeException(ex);
                    }

                }
                case "consumerGroups" -> {
                    String cluster = parent.getParent().toString();
                    AdminClient adminClient = ClientCache.get(cluster);
                    String consumerGroupId = selectionNode.toString();
                    try {
                        Map<String, ConsumerGroupDescription> consumerGroups = adminClient.describeConsumerGroups(List.of(consumerGroupId)).all().get(5, TimeUnit.SECONDS);
                        Optional<ConsumerGroupDescription> consumerGroupDescriptionOptional = consumerGroups.values().stream().filter(consumerGroupDescription -> consumerGroupDescription.groupId().equals(consumerGroupId)).findFirst();
                        consumerGroupDescriptionOptional.ifPresent(consumerGroupDescription -> {
                            mainForm.getMainPanel().updateUI();
                            ConsumerGroupForm consumerGroupForm = ConsumerGroupForm.getInstance();
                            mainForm.getDataPanel().removeAll();
                            mainForm.getDataPanel().add(consumerGroupForm.getContentPanel(), gridConstraints);
                            mainForm.getMainPanel().updateUI();
                            ThreadUtil.execute(() -> {
                                Collection<MemberDescription> members = consumerGroupDescription.members();
                                for (MemberDescription member : members) {
                                    log.info("-----------{}", member.toString());
                                }
                                consumerGroupForm.getIdTextField().setText(consumerGroupDescription.groupId());
                                consumerGroupForm.getStateTextField().setText(consumerGroupDescription.state().toString());
                                consumerGroupForm.getCoordinatorTextField().setText(consumerGroupDescription.coordinator().toString());
                                consumerGroupForm.getPartitionAssignorTextField().setText(consumerGroupDescription.partitionAssignor());
                                consumerGroupForm.getSimpleConsumerGroupTextField().setText(String.valueOf(consumerGroupDescription.isSimpleConsumerGroup()));
                                try (KafkaConsumer<String, String> kafkaConsumer = new KafkaConsumer<>(Map.of(
                                    ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, cluster,
                                    ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
                                    ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
                                    ConsumerConfig.GROUP_ID_CONFIG, "kafkacli" + GROUP_ID,
                                    ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest"
                                ))) {
                                    offset(consumerGroupId, consumerGroupForm, adminClient, kafkaConsumer);
                                }
                            });
                        });
                    } catch (InterruptedException | ExecutionException | TimeoutException ex) {
                        throw new RuntimeException(ex);
                    }

                }
                default -> throw new IllegalStateException("Unexpected value: " + parent.toString());
            }
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
        brokersRefreshMenuItem.addActionListener(e -> {
            DefaultMutableTreeNode selectionNode = (DefaultMutableTreeNode) clusterTree.getLastSelectedPathComponent();
            DefaultMutableTreeNode parent = (DefaultMutableTreeNode) selectionNode.getParent();
            String cluster = parent.toString();
            AdminClient adminClient = ClientCache.get(cluster);
            selectionNode.removeFromParent();
//            DefaultTreeModel rootTreeModel = (DefaultTreeModel) clusterTree.getModel();
//            rootTreeModel.removeNodeFromParent(selectionNode);
            try {
                NewConnListener.buildBrokers(adminClient, parent);
            } catch (Exception exception) {
                log.info("connect kafka failed. {}", exception.getMessage());
                JOptionPane.showMessageDialog(mainForm.getMainPanel(), "连接失败！请检查配置\n\n", "失败", JOptionPane.ERROR_MESSAGE);
                throw BaseException.of(BaseErrorCode.CONNECTION_FAILED);
            }
            clusterTree.updateUI();
        });
        topicsRefreshMenuItem.addActionListener(e -> {
            DefaultMutableTreeNode selectionNode = (DefaultMutableTreeNode) clusterTree.getLastSelectedPathComponent();
            DefaultMutableTreeNode parent = (DefaultMutableTreeNode) selectionNode.getParent();
            String cluster = parent.toString();
            AdminClient adminClient = ClientCache.get(cluster);
            selectionNode.removeFromParent();
//            DefaultTreeModel rootTreeModel = (DefaultTreeModel) clusterTree.getModel();
//            rootTreeModel.removeNodeFromParent(selectionNode);
            try {
                NewConnListener.buildTopics(adminClient, parent);
            } catch (Exception exception) {
                log.info("connect kafka failed. {}", exception.getMessage());
                JOptionPane.showMessageDialog(mainForm.getMainPanel(), "连接失败！请检查配置\n\n", "失败", JOptionPane.ERROR_MESSAGE);
                throw BaseException.of(BaseErrorCode.CONNECTION_FAILED);
            }
            clusterTree.updateUI();
        });
        topicsCreateMenuItem.addActionListener(e -> {
            DefaultMutableTreeNode selectionNode = (DefaultMutableTreeNode) clusterTree.getLastSelectedPathComponent();
            try {
                String cluster = selectionNode.getParent().toString();
                CreateTopicDialog dialog = new CreateTopicDialog(cluster);
                dialog.pack();
                dialog.setVisible(true);
            } catch (Exception exception) {
                log.error("create new topic dialog failed. {}", exception.getMessage());
            }
        });

        topicDeleteMenuItem.addActionListener(e -> {
            DefaultMutableTreeNode selectionNode = (DefaultMutableTreeNode) clusterTree.getLastSelectedPathComponent();
            try {
                int response = JOptionPane.showConfirmDialog(mainForm.getMainPanel(), "即将删除topic，所有数据将丢失！请确认\n\n", "警告", JOptionPane.OK_CANCEL_OPTION);
                if (response == 0) {
                    log.info("按下确定按钮!");
                    String cluster = selectionNode.getParent().toString();
                    AdminClient adminClient = ClientCache.get(cluster);

                    adminClient.deleteTopics(List.of(selectionNode.toString()));
                }

            } catch (Exception exception) {
                log.error("delete topic dialog failed. {}", exception.getMessage());
            }
        });

        consumerGroupsRefreshMenuItem.addActionListener(e -> {
            DefaultMutableTreeNode selectionNode = (DefaultMutableTreeNode) clusterTree.getLastSelectedPathComponent();
            DefaultMutableTreeNode parent = (DefaultMutableTreeNode) selectionNode.getParent();
            String cluster = parent.toString();
            AdminClient adminClient = ClientCache.get(cluster);
            selectionNode.removeFromParent();
//            DefaultTreeModel rootTreeModel = (DefaultTreeModel) clusterTree.getModel();
//            rootTreeModel.removeNodeFromParent(selectionNode);

            try {
                NewConnListener.buildConsumerGroups(adminClient, parent);
            } catch (Exception exception) {
                log.info("connect kafka failed. {}", exception.getMessage());
                JOptionPane.showMessageDialog(mainForm.getMainPanel(), "连接失败！请检查配置\n\n", "失败", JOptionPane.ERROR_MESSAGE);
                throw BaseException.of(BaseErrorCode.CONNECTION_FAILED);
            }
            clusterTree.updateUI();
        });
        consumerGroupDeleteMenuItem.addActionListener(e -> {
            DefaultMutableTreeNode selectionNode = (DefaultMutableTreeNode) clusterTree.getLastSelectedPathComponent();
            try {
                int response = JOptionPane.showConfirmDialog(mainForm.getMainPanel(), "即将删除consumerGroup，所有数据将丢失！请确认\n\n", "警告", JOptionPane.OK_CANCEL_OPTION);
                if (response == 0) {
                    log.info("按下确定按钮!");
                    String cluster = selectionNode.getParent().toString();
                    AdminClient adminClient = ClientCache.get(cluster);

                    adminClient.deleteConsumerGroups(List.of(selectionNode.toString()));
                }

            } catch (Exception exception) {
                log.error("delete topic dialog failed. {}", exception.getMessage());
            }
        });

    }

    private static void offset(String consumerGroupId, ConsumerGroupForm consumerGroupForm, AdminClient adminClient, KafkaConsumer<String, String> kafkaConsumer) {
        String[] headers = {"index", "topic", "partition", "start", "end", "offset", "lag", "last commit timestamp"};
        DefaultTableModel model = new DefaultTableModel(null, headers);
        JTable offsetTable = consumerGroupForm.getOffsetTable();
        offsetTable.setModel(model);
        Object[] data = new Object[8];
        try {
            Map<TopicPartition, OffsetAndMetadata> topicPartitionOffsets = adminClient.listConsumerGroupOffsets(consumerGroupId).partitionsToOffsetAndMetadata().get(5, TimeUnit.SECONDS);
            int i = 0;
            for (Map.Entry<TopicPartition, OffsetAndMetadata> topicPartitionOffsetAndMetadataEntry : topicPartitionOffsets.entrySet()) {
                String topic = topicPartitionOffsetAndMetadataEntry.getKey().topic();
                int partition = topicPartitionOffsetAndMetadataEntry.getKey().partition();
                long offset = topicPartitionOffsetAndMetadataEntry.getValue().offset();
                Integer leaderEpoch = topicPartitionOffsetAndMetadataEntry.getValue().leaderEpoch().orElse(null);

                data[0] = i;
                data[1] = topic;
                data[2] = partition;
                Long start = kafkaConsumer.beginningOffsets(List.of(topicPartitionOffsetAndMetadataEntry.getKey())).values().stream().findFirst().orElseThrow();
                Long end = kafkaConsumer.endOffsets(List.of(topicPartitionOffsetAndMetadataEntry.getKey())).values().stream().findFirst().orElseThrow();
                Long lag;
                try {
                    lag = kafkaConsumer.currentLag(topicPartitionOffsetAndMetadataEntry.getKey()).isPresent() ? kafkaConsumer.currentLag(topicPartitionOffsetAndMetadataEntry.getKey()).getAsLong() : null;
                } catch (IllegalStateException ex) {
                    lag = null;
                }
                if (ObjectUtils.isEmpty(lag)) {
                    lag = end - offset;
                }
                Long position;
                try {
                    position = kafkaConsumer.position(topicPartitionOffsetAndMetadataEntry.getKey());
                } catch (Exception ex) {
                    position = null;
                }
                kafkaConsumer.committed(Set.of(topicPartitionOffsetAndMetadataEntry.getKey()));

                data[3] = start;
                data[4] = end;
                data[5] = offset;
                data[6] = lag;
                data[7] = position;
                model.addRow(data);
                i++;
            }
        } catch (InterruptedException | ExecutionException | TimeoutException ignored) {
        }

    }

    private static void config(String topicName, TopicForm topicForm, AdminClient adminClient) {
        String[] headers = {"index", "key", "value"};
        DefaultTableModel model = new DefaultTableModel(null, headers);
        JTable configTable = topicForm.getConfigTable();
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
