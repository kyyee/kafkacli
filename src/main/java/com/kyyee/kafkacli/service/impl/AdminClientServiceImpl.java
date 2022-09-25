package com.kyyee.kafkacli.service.impl;

import com.kyyee.kafkacli.service.AdminClientService;
import org.apache.kafka.clients.admin.*;
import org.apache.kafka.common.KafkaFuture;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.List;
import java.util.Map;
import java.util.Properties;

public class AdminClientServiceImpl implements AdminClientService {

    @Override
    public AdminClient connect(String bootstrapServers) {
        Properties prop = new Properties();
        // 设置kafka的地址,如果是多个使用 逗号 分隔
        prop.setProperty(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        return KafkaAdminClient.create(prop);
    }


}
