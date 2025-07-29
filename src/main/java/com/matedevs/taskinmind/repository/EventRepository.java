package com.matedevs.taskinmind.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.matedevs.taskinmind.model.Event;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event,Long> {

    @Query("SELECT e FROM Event e WHERE YEAR(e.startDate) = :year AND MONTH(e.startDate) = :month AND e.userId = :userId")
    List<Event> findEventsByYearAndMonth (@Param("year") int year, @Param("month") int month, @Param("userId") Long userId);
}
