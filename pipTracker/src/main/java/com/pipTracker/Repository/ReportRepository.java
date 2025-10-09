/*package com.pipTracker.Repository;

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
*/
package com.pipTracker.Repository;

import com.pipTracker.Entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

    // Fetch all reports for a given Employee ID
    List<Report> findByEmployee_EmployeeId(Long employeeId);

    //  Fetch all reports by Employee Name
    List<Report> findByEmployee_Name(String name);

    //  (Optional but useful) Fetch reports by both employeeId and employeeName
    List<Report> findByEmployee_EmployeeIdAndEmployee_Name(Long employeeId, String name);

    //  (Optional but clean) Find a report directly by Report ID
    Optional<Report> findByReportId(Long reportId);

    //  (Optional) Check if a report exists for an employee
    boolean existsByEmployee_EmployeeId(Long employeeId);
}
