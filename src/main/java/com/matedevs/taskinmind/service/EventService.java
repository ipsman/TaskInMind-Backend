package com.matedevs.taskinmind.service;

import com.matedevs.taskinmind.model.Event;
import com.matedevs.taskinmind.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EventService {
    @Autowired
    private EventRepository eventRepository;

    public Event createEvent(Event event){
        return eventRepository.save(event);
    }

    public List<Event> getAllEvents(){
        return eventRepository.findAll();
    }

    public Optional<Event> getEventById(Long id){
        return eventRepository.findById(id);
    }

    public List<Event> getEventsByMonth(Integer Year, Integer Month, Long userId){
        System.out.println("DEBUG: EventService.getEventsByMonthAndUser called with year=" + Year + ", month=" + Month + ", userId=" + userId);
        return eventRepository.findEventsByYearAndMonth(Year, Month , userId);
    }

    public void deleteEvent(Long id){
        eventRepository.deleteById(id);
    }
}
