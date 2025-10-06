package com.matedevs.taskinmind.repository;

import com.matedevs.taskinmind.model.Reminder;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;
import com.matedevs.taskinmind.model.Event;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReminderRepository extends JpaRepository<Reminder,Long> {

    List<Reminder> findBySendAtBeforeAndSentIsFalse(LocalDateTime now);

    List<Reminder> findByEventId(Long eventId);
}