package com.kyyee.kafkacli.service;

import org.apache.kafka.clients.admin.AdminClient;

import java.util.Set;

public interface TopicService {
    Set<String> list(AdminClient connect);
}
