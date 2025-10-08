package com.gym.crm.integrationtests.hook;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.jdbc.core.JdbcTemplate;

@RequiredArgsConstructor
public class DatabaseHooks {
    private final MongoTemplate mongoTemplate;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @After
    public void cleanMySqlDatabase() {
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 0");
        jdbcTemplate.queryForList("SHOW TABLES", String.class)
                .forEach(table -> jdbcTemplate.execute("TRUNCATE TABLE " + table));
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 1");
    }

    @Before
    public void setUp() {
        mongoTemplate.getDb().drop();
    }
}
