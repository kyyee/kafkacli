package com.kyyee.kafkacli.ui.configs;

import org.apache.kafka.clients.admin.AdminClient;

import java.util.concurrent.ConcurrentHashMap;

public class ClientCache {
    public static final ConcurrentHashMap<String, AdminClient> cache = new ConcurrentHashMap<>();

    public static AdminClient get(String cluster) {
        return cache.getOrDefault(cluster, null);
    }

    public static void put(String cluster, AdminClient client) {
        cache.putIfAbsent(cluster, client);
    }
}
