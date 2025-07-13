package com.matedevs.taskinmind.service;

import com.matedevs.taskinmind.model.Event;
import com.matedevs.taskinmind.model.Task;
import com.matedevs.taskinmind.repository.EventRepository;
import com.matedevs.taskinmind.repository.TaskRepository;
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

    public Event updateEvent(Long id,Event eventDetails){
        Optional<Event> optionalEvent = eventRepository.findById(id);
        if(optionalEvent.isPresent()){
            Event event = optionalEvent.get();
            event.setTitle(eventDetails.getTitle());
            event.setDescription(eventDetails.getDescription());
            event.setStartDate(eventDetails.getStartDate());
            event.setEndDate(eventDetails.getEndDate());

            return eventRepository.save(event);
        }
        else{
            return null;
        }
    }

    public void deleteEvent(Long id){
        eventRepository.deleteById(id);
    }
}
