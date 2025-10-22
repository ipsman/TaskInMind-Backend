package com.matedevs.taskinmind.service;

import com.matedevs.taskinmind.model.Event;
import com.matedevs.taskinmind.model.Reminder;
import com.matedevs.taskinmind.repository.EventRepository;
import com.matedevs.taskinmind.repository.ReminderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class EventService {
    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private ReminderRepository reminderRepository;



    @Transactional
    public Event createEvent(Event event){

        Event newEvent = eventRepository.save(event);

        int minutes = event.getReminderTime();

        LocalDateTime reminderTime = newEvent.getStartDate().minusMinutes(minutes);

        Reminder newReminder = new Reminder();

        newReminder.setEvent(newEvent);

        newReminder.setSendAt(reminderTime);

        reminderRepository.save(newReminder);

        return newEvent;
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
        reminderRepository.deleteByEventId(id);
        eventRepository.deleteById(id);
    }
}
