package TFEManagerLib;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

/** Clase para gestión de correo:
 * - Envío de propuestas a los revisores.
 * - Envío de asignaciones a directores.
 * - Envío de avisos de corrección tardía a directores.
 */
public class MailManager {
    private static final String MAIL_HOST = "smtp.office365.com";
    private static final String MAIL_PORT = "587";
    private String username;
    private String password;

    public MailManager(String username, String password) {
        if (username!=null) {
            this.username = username;
        } else {
            this.username = System.getenv("MAIL_USERNAME");
        }
        if (password!=null) {
            this.password = password;
        } else {
            this.password = System.getenv("MAIL_PASSWORD");
        }
    }

    public void send() throws MessagingException {
        Properties prop = new Properties();
        prop.put("mail.smtp.auth", true);
        prop.put("mail.smtp.starttls.enable", "true");
        prop.put("mail.smtp.host", MAIL_HOST);
        prop.put("mail.smtp.port", MAIL_PORT);
        // prop.put("mail.smtp.ssl.trust", "smtp.mailtrap.io");
        Session session = Session.getInstance(prop, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
        // Construcción y envío del mensaje
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(username));
        message.setRecipients(
                Message.RecipientType.TO, InternetAddress.parse("luispedraza@gmail.com"));
        message.setSubject("envío de mensaje");
        String msg = "A ver si lleva";

        MimeBodyPart mimeBodyPart = new MimeBodyPart();
        mimeBodyPart.setContent(msg, "text/html");

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(mimeBodyPart);

        message.setContent(multipart);

        Transport.send(message);
    }
}
