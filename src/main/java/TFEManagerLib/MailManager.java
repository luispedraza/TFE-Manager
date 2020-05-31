package TFEManagerLib;


// Ejemplo de envío de mensajes: https://www.baeldung.com/java-email
// Sobre el uso de resources https://www.jetbrains.com/help/idea/content-roots.html

import javax.mail.*;
import javax.mail.internet.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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
        this.username = username;
        this.password = password;
    }


    /** Envía un mensaje a un destinatario
     *
     * @param destinationMail: email del destinatario
     * @param subject: asunto del mensaje
     * @param content: contenido del mensaje
     * @param attachments: adjuntos del mensaje (lista de archivos)
     * @throws MessagingException
     * @throws IOException
     */
    public void send(String destinationMail, String subject, String content, ArrayList<File> attachments) throws MessagingException, IOException {
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
        System.out.println(username);
        message.setFrom(new InternetAddress(username));
        message.setRecipients(
                Message.RecipientType.TO, InternetAddress.parse(destinationMail));
        message.setSubject(subject);
        // para el contenido del mensaje:
        Multipart multipart = new MimeMultipart();
        // el contenido
        MimeBodyPart mimeBodyPart = new MimeBodyPart();
        mimeBodyPart.setContent(content, "text/html");
        multipart.addBodyPart(mimeBodyPart);

        // Los adjuntos, si los hay
        if (attachments != null) {
            for (File f : attachments) {
                mimeBodyPart = new MimeBodyPart();
                mimeBodyPart.attachFile(f);
                multipart.addBodyPart(mimeBodyPart);
            }
        }

        message.setContent(multipart);

        Transport.send(message);
    }
}
