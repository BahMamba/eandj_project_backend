package com.ejinternational.ej_platform_backend.service;

import com.ejinternational.ej_platform_backend.model.dto.email.EmailRequest;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    /**
     * Méthode générique pour envoyer un email
     */
    public void sendEmail(EmailRequest request) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(from);
            helper.setTo(request.to());
            helper.setSubject(request.subject());
            helper.setText(request.body(), true); 

            mailSender.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException("Erreur lors de l'envoi de l'email : " + e.getMessage(), e);
        }
    }

    /**
     * Envoi des credentials d’un commercial (via DTO)
     */
    public void sendCommercialCredentials(String to, String username, String email, String password) {
        String subject = "Vos identifiants pour E&J Platform";

        String body = """
                <div style="font-family: Arial, sans-serif; color: #333;">
                    <h2>Bienvenue sur E&J Platform 🎉</h2>
                    <p>Bonjour <b>%s</b>,</p>
                    <p>Votre compte commercial a été créé avec succès. Voici vos identifiants :</p>
                    <ul>
                        <li><b>Email :</b> %s</li>
                        <li><b>Mot de passe provisoire :</b> %s</li>
                    </ul>
                    <p>⚠️ Veuillez vous connecter dès maintenant et modifier votre mot de passe lors de la première connexion.</p>
                    <br>
                    <p>À très bientôt,<br>L’équipe E&J.</p>
                </div>
                """.formatted(username, email, password);

        sendEmail(new EmailRequest(to, subject, body));
    }

    /**
     * Confirmation changement mot de passe (via DTO)
     */
    public void sendPasswordChangedConfirmation(String to, String username) {
        String subject = "Confirmation de changement de mot de passe";

        String body = """
                <div style="font-family: Arial, sans-serif; color: #333;">
                    <h2>Mot de passe modifié avec succès 🔑</h2>
                    <p>Bonjour <b>%s</b>,</p>
                    <p>Votre mot de passe sur E&J Platform a été mis à jour avec succès.</p>
                    <p>Si vous n’êtes pas à l’origine de cette action, merci de contacter immédiatement le support.</p>
                    <br>
                    <p>– L’équipe E&J</p>
                </div>
                """.formatted(username);

        sendEmail(new EmailRequest(to, subject, body));
    }
}
