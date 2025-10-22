package com.matedevs.taskinmind.service;

import com.matedevs.taskinmind.model.Event;
import com.matedevs.taskinmind.model.Reminder;
import com.matedevs.taskinmind.model.User;
import com.matedevs.taskinmind.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;
    @Autowired
    private UserRepository userRepository;

    public void sendReminderEmail(Reminder reminder, Event event) throws MessagingException {

        Optional<User> user = userRepository.findById(event.getUserId());

        String recipientEmail = user.get().getEmail();

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setFrom("taskinmind.noreply@gmail.com");
        helper.setTo(recipientEmail);
        helper.setSubject("Reminder: " + reminder.getEvent().getTitle());

        Context context = new Context();
        context.setVariable("eventTitle", reminder.getEvent().getTitle());


        LocalDateTime utcDateTime = event.getStartDate();

        String formattedLocalTime = utcDateTime.format(DateTimeFormatter.ofPattern("yyyy. MM. dd. HH:mm"));

        context.setVariable("eventStartDate", formattedLocalTime);
        context.setVariable("location", reminder.getEvent().getLocation());

        String htmlContent = templateEngine.process("reminder_template", context);

        helper.setText(htmlContent, true);

        mailSender.send(message);
    }
}
