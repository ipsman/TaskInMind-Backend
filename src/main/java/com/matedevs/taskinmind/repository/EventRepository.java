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
    List<Event> findByStartDateBetween(LocalDate startOfMonth, LocalDate endOfMonth);

    @Query("select e from Event e where year(e.startDate) = :year and month(e.startDate) = :month")
    List<Event> findEventsByYearAndMonth (@Param("year") int year, @Param("month") int month);
}
