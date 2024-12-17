package poly.gamemarketplacebackend.core.service;

public interface EmailService {
    void sendEmail(String to, String subject, String content);
}