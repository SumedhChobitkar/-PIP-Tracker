package com.pipTracker.Repository;

import com.pipTracker.Entity.FeedBack;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeedBackRepository extends JpaRepository<FeedBack,Long>
{
    List<FeedBack> findByEmployeeEmployeeId(Long employeeId);

}
