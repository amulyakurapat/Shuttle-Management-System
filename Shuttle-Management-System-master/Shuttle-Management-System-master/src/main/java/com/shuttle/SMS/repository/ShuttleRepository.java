package com.shuttle.SMS.repository;

import com.shuttle.SMS.model.Shuttle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShuttleRepository extends JpaRepository<Shuttle, Long> {
}
