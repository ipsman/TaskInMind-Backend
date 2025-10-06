package com.matedevs.taskinmind.service;

import com.matedevs.taskinmind.model.Reminder;
import com.matedevs.taskinmind.repository.ReminderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReminderScheduler {
    @Autowired
    private ReminderRepository reminderRepository;
    @Autowired
    private EmailService emailService;

    @Scheduled(cron = "0 * * * * *")
    public void schedule() {
        List<Reminder> reminders = reminderRepository.findBySendAtBeforeAndSentIsFalse(LocalDateTime.now());

        for (Reminder reminder : reminders) {
            try {
                emailService.sendReminderEmail(reminder, reminder.getEvent());

                reminder.setSent(true);
                reminderRepository.save(reminder);
            }
            catch (Exception e) {
                System.err.println("Message sending fault: " + e.getMessage());
            }
        }

    }

}
