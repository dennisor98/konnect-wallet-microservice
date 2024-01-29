package net.sasakonnect.wallet.repository.lark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import net.sasakonnect.wallet.domain.LarkDepartment;

public interface DepartmentRepository extends JpaRepository<LarkDepartment, String> {
	
	@Modifying
    @Transactional
    @Query("UPDATE LarkDepartment d SET d.departmentName = :departmentName WHERE d.departmentId = :departmentId")
    int updateExistingDepartment(@Param("departmentId") String departmentId, @Param("departmentName") String departmentName);

    @Modifying
    @Transactional
    @Query("INSERT INTO LarkDepartment (departmentId, departmentName) VALUES (:departmentId, :departmentName)")
    int insertNewDepartment(@Param("departmentId") String departmentId, @Param("departmentName") String departmentName);

    default void upsert(String departmentId, String departmentName) {
        if (updateExistingDepartment(departmentId, departmentName) == 0) {
            insertNewDepartment(departmentId, departmentName);
        }
    }
	 
	

}
