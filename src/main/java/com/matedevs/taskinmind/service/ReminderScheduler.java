package com.matedevs.taskinmind.service;

import com.matedevs.taskinmind.model.Reminder;
import com.matedevs.taskinmind.repository.ReminderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

import static com.fasterxml.jackson.databind.type.LogicalType.DateTime;

@Service
public class ReminderScheduler {
    @Autowired
    private ReminderRepository reminderRepository;
    @Autowired
    private EmailService emailService;

    @Transactional
    @Scheduled(cron = "* * * * * *")
    public void schedule() {

        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(LocalTime.MAX);

        List<Reminder> dueReminders = reminderRepository.findDueRemindersInWindow();


        if (!dueReminders.isEmpty()) {
            LocalDateTime firstSendAt = dueReminders.getFirst().getSendAt();
            System.out.println("First Reminder SendAt Time (DB): " + firstSendAt);
            System.out.println(LocalDateTime.now());
            System.out.println("Is it DUE? (DB time < Now): " + firstSendAt.isBefore(startOfDay));
        } else {
            System.out.println("Nincsenek Reminderek az adatbázisban.");
            return;
        }

        for (Reminder reminder : dueReminders) {
            try {
                if(reminder.getSendAt().isAfter(LocalDateTime.now().minusMinutes(1)) &&reminder.getSendAt().isBefore(LocalDateTime.now().plusMinutes(2))){
                    emailService.sendReminderEmail(reminder, reminder.getEvent());
                    System.out.println("Reminder title:" + reminder.getEvent().getTitle());
                    reminder.setSent(true);
                    reminderRepository.save(reminder);
                    System.out.println("Message sent to " + reminder.getEvent().getTitle());
                }
            }
            catch (Exception e) {
                System.err.println("Message sending fault: " + e.getMessage());
            }
        }

    }

}
