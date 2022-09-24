package com.kyyee.kafkacli.ui.form;

import com.kyyee.kafkacli.App;
import lombok.Getter;
import org.apache.kafka.clients.admin.AdminClient;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;

@Getter
public class MainForm {
    private JPanel mainPanel;
    private JTabbedPane mainTabbedPane;
    private JToolBar mainToolBar;
    private JButton clearButton;
    private JProgressBar loadProgressBar;
    private JLabel cache;
    private JTree clusterTree;
    private JSplitPane browserSplitPane;
    private JPanel contentPanel;
    private JScrollPane clusterTreeScrollPane;
    private JScrollPane dataScrollPane;
    private JPanel dataPanel;
    private JButton buttonCreateConn;
    private static MainForm mainForm;

    private AdminClient adminClient;

    public void setAdminClient(AdminClient adminClient) {
        this.adminClient = adminClient;
    }

    public static MainForm getInstance() {
        if (mainForm == null) {
            mainForm = new MainForm();
        }
        return mainForm;
    }

    public void init() {
        getMainPanel().setVisible(true);
        getBrowserSplitPane().setDividerLocation(App.mainFrame.getWidth() / 5);
        //创建树节点

        DefaultTreeModel rootTreeModel = new DefaultTreeModel(new DefaultMutableTreeNode("cluster"), true);
        getClusterTree().setModel(rootTreeModel);
        getClusterTree().setEditable(true);

        getBrowserSplitPane().setLeftComponent(clusterTree);
        // 树形最小尺寸
        mainForm.getClusterTreeScrollPane().setMinimumSize(new Dimension(400, 400));

        MainFormListener.addListener(this);

    }

}
