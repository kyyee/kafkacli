package com.kyyee.kafkacli.service.impl;

import com.kyyee.framework.common.exception.BaseErrorCode;
import com.kyyee.framework.common.exception.BaseException;
import com.kyyee.kafkacli.service.TopicService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.ListTopicsResult;

import java.util.Set;

@Slf4j
public class TopicServiceImpl implements TopicService {

    @Override
    public Set<String> list(AdminClient connect) {

        Set<String> topicNames;
        try {
            ListTopicsResult topics = connect.listTopics();
            topicNames = topics.names().get();
        } catch (Exception e) {
            log.info("query kafka topics failed. {}", e.getMessage());
            throw BaseException.of(BaseErrorCode.DATA_EMPTY_ERROR);
        }
        return topicNames;
    }

    public static void main(String[] args) {
        AdminClientServiceImpl adminClientService = new AdminClientServiceImpl();
        try (AdminClient connect = adminClientService.connect("192.168.21.247:9092")) {

            TopicServiceImpl topicService = new TopicServiceImpl();
            Set<String> list = topicService.list(connect);
            log.info("{}", list);
        }

    }
}
