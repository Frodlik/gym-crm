package com.gym.crm.workloadservice.repository;

import com.gym.crm.workloadservice.model.Year;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface YearRepository extends JpaRepository<Year, Long> {

}
