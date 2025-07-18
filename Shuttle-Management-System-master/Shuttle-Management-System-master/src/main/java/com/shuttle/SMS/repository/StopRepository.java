package com.shuttle.SMS.repository;

import com.shuttle.SMS.model.Stop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StopRepository extends JpaRepository<Stop, Long> {
}
