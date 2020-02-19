package a16team1.virtualwallet.services.email_tokens;

import org.springframework.mail.SimpleMailMessage;

public interface EmailSenderService {

    void sendEmail(SimpleMailMessage email);
}
