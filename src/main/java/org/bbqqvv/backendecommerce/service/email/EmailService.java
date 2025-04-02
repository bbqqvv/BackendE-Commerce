package org.bbqqvv.backendecommerce.service.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.bbqqvv.backendecommerce.entity.Order;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class EmailService {
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    public EmailService(JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    public void sendOrderConfirmationEmail(Order order, String toEmail) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("Xác nhận đơn hàng");
            // Prepare the evaluation context
            Context context = new Context();
            context.setVariable("order", order);
            context.setVariable("orderItems", order.getOrderItems());
            System.out.println("Order Items:");
            for (var item : order.getOrderItems()) {
                System.out.println("Product Name: " + item.getProduct().getName());
                System.out.println("Product Image: " + item.getProduct().getMainImage());
                System.out.println("Price: " + item.getPrice());
                System.out.println("Quantity: " + item.getQuantity());
            }

            // Create HTML body using Thymeleaf
            String htmlContent = templateEngine.process("order-confirmation", context);
            helper.setText(htmlContent, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }
}