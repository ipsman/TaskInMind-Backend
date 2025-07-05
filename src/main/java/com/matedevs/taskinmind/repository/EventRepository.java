package com.matedevs.taskinmind.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.matedevs.taskinmind.model.Event;

@Repository
public interface EventRepository extends JpaRepository<Event,Long> {

}
