package com.ckrey.ckreycodemother;

import org.junit.jupiter.api.Test;
import redis.clients.jedis.Jedis;

import java.net.URI;


public class test {
    public static void main(String[] args) {
        Jedis jedis = new Jedis(URI.create("rediss://default:ARr0AAImcDFiYzY2YTM2NDA3M2I0MzU4OTMxMDA4YmFiMTBhNDQ4ZHAxNjkwMA@safe-griffon-6900.upstash.io:6379"));
        jedis.set("foo", "bar");
        String value = jedis.get("foo");
    }
}
