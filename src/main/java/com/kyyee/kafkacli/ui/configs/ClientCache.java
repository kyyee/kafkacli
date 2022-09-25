package com.kyyee.kafkacli.ui.configs;

import org.apache.kafka.clients.admin.AdminClient;

import java.util.concurrent.ConcurrentHashMap;

public class ClientCache {
    public static final ConcurrentHashMap<String, AdminClient> cache = new ConcurrentHashMap<>();

    public static AdminClient get(String clusterName) {
        return cache.getOrDefault(clusterName, null);
    }

    public static void put(String clusterName, AdminClient client) {
        cache.putIfAbsent(clusterName, client);
    }
}
