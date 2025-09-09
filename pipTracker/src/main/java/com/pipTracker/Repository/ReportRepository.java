package com.pipTracker.Repository;

import com.pipTracker.Entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReportRepository extends JpaRepository <Report,Long> {
    List<Report> findByEmployee_EmployeeId(Long employeeId);
    List<Report> findByEmployee_Name(String name);

}
