package com.matedevs.taskinmind.repository;

import com.matedevs.taskinmind.model.Reminder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReminderRepository extends JpaRepository<Reminder,Long> {

    @Query(value = """
        SELECT 
            * FROM 
            reminder 
        WHERE 
            send_at >= CURDATE() 
            AND send_at < DATE_ADD(CURDATE(), INTERVAL 1 DAY) 
            AND sent = FALSE;
        """,
            nativeQuery = true)
    List<Reminder> findDueRemindersInWindow();
    @Transactional
    void deleteByEventId(Long id);
}