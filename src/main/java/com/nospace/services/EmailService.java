package com.nospace.services;

import com.nospace.entities.User;
import com.nospace.entities.VerificationToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSenderImpl mailSender;
    private final TemplateEngine templateEngine;

    private Map<String, String> prepareVerificationEmailContent(VerificationToken verificationToken){
        User user = verificationToken.getUser();
        String verificationLink = "http://localhost:3000/account/activate?token="+verificationToken.getToken();

        Map<String, String> emailContent = new HashMap<>();
        emailContent.put("from", "nospace@noreply.com");
        emailContent.put("subject", "Account verification email | NoSpace");
        emailContent.put("userEmail", user.getEmail());
        emailContent.put("username", user.getUsername());
        emailContent.put("verificationLink", verificationLink);
        emailContent.put("expireDate", verificationToken.getExpirationDate().toString());

        Context context = new Context();
        context.setVariable("username", emailContent.get("username"));
        context.setVariable("verificationLink", emailContent.get("verificationLink"));
        String text = templateEngine.process("email", context);
        emailContent.put("content", text);

        return emailContent;
    }

    @Async
    public void sendAccountVerificationEmail(VerificationToken verificationToken) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(
                mimeMessage, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name()
        );

        Map<String, String> emailContent = prepareVerificationEmailContent(verificationToken);
        messageHelper.setTo(emailContent.get("userEmail"));
        messageHelper.setFrom(emailContent.get("from"));
        messageHelper.setSubject(emailContent.get("subject"));
        messageHelper.setText(emailContent.get("content"), true);

        mailSender.send(mimeMessage);
        log.info("An email verification email has been sent");
    }
}
