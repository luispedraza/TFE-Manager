package TFEManagerLib;


// Ejemplo de envío de mensajes: https://www.baeldung.com/java-email
// Sobre el uso de resources https://www.jetbrains.com/help/idea/content-roots.html
// Recursos en guava https://www.stubbornjava.com/posts/reading-file-resources-with-guava
// Sobre estilos para el mail: https://www.litmus.com/blog/a-guide-to-css-inlining-in-email/?utm_campaign=newsletter_feb2016&utm_source=pardot&utm_medium=email
// Tutorial freemarker https://www.vogella.com/tutorials/FreeMarker/article.html
// CSS inliner https://templates.mailchimp.com/resources/inline-css/


import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import javax.mail.*;
import javax.mail.internet.*;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

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

    class ReviewEmailContent {

    }

    public MailManager(String username, String password) {
        this.username = username;
        this.password = password;
    }

    /** Genera el contenido de un correo para enviar a un revisor
     *
     * @return: Contenido del correo
     */
    public String getReviewEmailContent(Map<String, Object> data) throws IOException, TemplateException {
        /*
        URL url = Resources.getResource("TFEManagerLib/templates/review_template.ftl");
        String template = Resources.toString(url, Charsets.UTF_8);
        // getClass().getResource("review_template.html").getFile();
        */

        Configuration cfg = new Configuration(Configuration.VERSION_2_3_30);

        cfg.setClassForTemplateLoading(MailManager.class, "/TFEManagerLib/templates/");
        cfg.setDefaultEncoding("UTF-8");
        Template template = cfg.getTemplate("review_template.ftl");
        StringWriter strWriter = new StringWriter();
        // Se procesa la plantilla para obtener el contenido del email
        template.process(data, strWriter);
        String content = strWriter.toString();
        System.out.println(content);
        return content;
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
        mimeBodyPart.setContent(content, "text/html; charset=UTF-8");
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
