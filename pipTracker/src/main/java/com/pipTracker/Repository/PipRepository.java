package com.pipTracker.Repository;

import com.pipTracker.Entity.Pip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PipRepository extends JpaRepository<Pip, Long> {
}