package com.gym.crm.workloadservice.repository;

import com.github.database.rider.core.api.configuration.DBUnit;
import com.github.database.rider.junit5.api.DBRider;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@DBRider
@DBUnit(cacheConnection = false, leakHunter = true, schema = "public")
public abstract class BaseIntegrationTest {
}
