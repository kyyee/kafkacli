package com.kyyee.kafkacli.ui.dialog;

import com.kyyee.framework.common.exception.BaseErrorCode;
import com.kyyee.framework.common.exception.BaseException;
import com.kyyee.kafkacli.service.impl.AdminClientServiceImpl;
import com.kyyee.kafkacli.ui.configs.ClientCache;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class CreateTopicListener {

    public static void addListener(CreateTopicDialog dialog) {
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

        dialog.getButtonOK().addActionListener(e -> {

            String name = dialog.getNameTextField().getText();
            Integer partition = Integer.parseInt(dialog.getPartitionSpinner().getValue().toString());
            Short replica = Short.parseShort(dialog.getReplicaSpinner().getValue().toString());
            JTable configTable = dialog.getConfigTable();
            Map<String, String> configs = new HashMap<>();
            for (int i = 0, rowCount = configTable.getRowCount(); i < rowCount; i++) {
                String key = configTable.getModel().getValueAt(i, 1).toString();
                String value = configTable.getModel().getValueAt(i, 2).toString();
                if (StringUtils.isNotBlank(key) && StringUtils.isNotBlank(value)) {
                    configs.put(key, value);
                }
            }

            try  {
                AdminClient adminClient = ClientCache.get(dialog.getClusterName());
                NewTopic newTopic = new NewTopic(name, Optional.of(partition), Optional.of(replica));
                newTopic.configs(configs);
                adminClient.createTopics(List.of(newTopic));

                dialog.dispose();
            } catch (Exception exception) {
                log.info("create topic failed. {}", exception.getMessage());
                JOptionPane.showMessageDialog(dialog, "创建topic失败！请检查配置\n\n", "失败", JOptionPane.ERROR_MESSAGE);
                throw BaseException.of(BaseErrorCode.CONNECTION_FAILED);
            }
        });

        dialog.getButtonAdd().addActionListener(e -> {
            JTable configTable = dialog.getConfigTable();
            Object[] data = new Object[3];
            data[0] = configTable.getRowCount();
            data[1] = "";
            data[2] = "";
            DefaultTableModel model = (DefaultTableModel) configTable.getModel();
            model.addRow(data);
            dialog.getButtonDelete().setEnabled(configTable.getSelectedRowCount() > 0);
        });
        dialog.getButtonDelete().addActionListener(e -> {
            JTable configTable = dialog.getConfigTable();

            DefaultTableModel model = (DefaultTableModel) configTable.getModel();
            model.removeRow(configTable.getSelectedRow());
            for (int i = 0, rowCount = configTable.getRowCount(); i < rowCount; i++) {
                configTable.getModel().setValueAt(i, i, 0);
            }
            dialog.getButtonDelete().setEnabled(configTable.getSelectedRowCount() > 0);
        });

        dialog.getConfigTable().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                dialog.getButtonDelete().setEnabled(dialog.getConfigTable().getSelectedRowCount() > 0);
            }
        });
        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowActivated(WindowEvent e) {
                super.windowActivated(e);
                dialog.getButtonDelete().setEnabled(dialog.getConfigTable().getSelectedRowCount() > 0);
            }
        });
    }

}
