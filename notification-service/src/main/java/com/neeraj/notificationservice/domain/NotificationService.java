package com.neeraj.notificationservice.domain;

import com.neeraj.notificationservice.ApplicationProperties;
import com.neeraj.notificationservice.domain.models.OrderCancelledEvent;
import com.neeraj.notificationservice.domain.models.OrderCreatedEvent;
import com.neeraj.notificationservice.domain.models.OrderDeliveredEvent;
import com.neeraj.notificationservice.domain.models.OrderErrorEvent;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class NotificationService {

    private final JavaMailSender emailSender;
    private final ApplicationProperties properties;

    public void sendOrderCreatedNotification(OrderCreatedEvent event) {
        String message =
                """
                        =======================================================
                        Order Created Notification                        -------------------------------------------------------
                        Dear %s,
                        Your order with order number: %s has been created successfully.

                        Thanks for shopping with us!
                        Bookstore Team
                        =======================================================
                        """
                        .formatted(event.customer().name(), event.orderNumber());
        log.info("Sending Order Created Notification for event: {}", event);
        sendEmail(event.customer().email(), "Order Created Notification", message);
    }

    public void sendOrderDeliveredNotification(OrderDeliveredEvent event) {
        String message =
                """
                        =======================================================
                        Order Delivered Notification                        -------------------------------------------------------
                        Dear %s,
                        Your order with order number: %s has been delivered successfully.

                        Thanks for shopping with us!
                        Bookstore Team
                        =======================================================
                        """
                        .formatted(event.customer().name(), event.orderNumber());
        log.info("Sending Order Delivered Notification for event: {}", event);
        sendEmail(event.customer().email(), "Order Delivered Notification", message);
    }

    public void sendOrderCancelledNotification(OrderCancelledEvent event) {
        String message =
                """
                        =======================================================
                        Order Cancellation Notification                        -------------------------------------------------------
                        Dear %s,
                        Your order with order number: %s has been cancelled.
                        Reason for cancellation: %s

                        Thanks for shopping with us!
                        Bookstore Team
                        =======================================================
                        """
                        .formatted(event.customer().name(), event.orderNumber(), event.reason());
        log.info("Sending Order Cancellation Notification for event: {}", event);
        sendEmail(event.customer().email(), "Order Cancellation Notification", message);
    }

    public void sendOrderErrorNotification(OrderErrorEvent event) {
        String message =
                """
                        =======================================================
                        Order Processing Failure Notification                        -------------------------------------------------------
                        Dear %s,
                        Order with order number: %s has encountered an error.
                        Reason for error: %s

                        Thanks for shopping with us!
                        Bookstore Team
                        =======================================================
                        """
                        .formatted(properties.supportEmail(), event.orderNumber(), event.reason());
        log.info("Sending Order Processing Failure Notification for event: {}", event);
        sendEmail(properties.supportEmail(), "Order Processing Failure Notification", message);
    }

    private void sendEmail(String recipient, String subject, String content) {
        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");
            helper.setFrom(properties.supportEmail());
            helper.setTo(recipient);
            helper.setSubject(subject);
            helper.setText(content);
            emailSender.send(message);
            log.info("Email sent successfully to: {}, subject: {}", recipient, subject);
        } catch (Exception e) {
            log.error("Failed to send email to: {}, subject: {}, error: {}", recipient, subject, e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }
}
