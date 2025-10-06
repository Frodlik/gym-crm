package com.gym.crm.integrationtests.hook;

import io.cucumber.java.Before;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;

@RequiredArgsConstructor
public class DatabaseHooks {
    private final MongoTemplate mongoTemplate;

    @Before("@workload")
    public void setUp() {
        mongoTemplate.getDb().drop();
    }
}
