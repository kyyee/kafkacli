package com.kyyee.kafkacli.service;

import org.apache.kafka.clients.admin.AdminClient;

public interface AdminClientService {
     AdminClient connect(String bootstrapServers);
}
