package com.gym.crm.workloadservice.repository;

import com.gym.crm.workloadservice.model.Month;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MonthRepository extends JpaRepository<Month, Long> {

}
