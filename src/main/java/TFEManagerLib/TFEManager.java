package TFEManagerLib;

import freemarker.template.TemplateException;

import javax.mail.MessagingException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Clase principal para la gestión de TFEs
 */

public class TFEManager {
    public static String WORKING_DIRECTORY;
    private FilesManager filesManager;
    private ExcelManager excelManager;
    private ArrayList<ProposalInfo> proposals;

    public TFEManager(String workingDirectory) {
        WORKING_DIRECTORY = workingDirectory;
        filesManager = new FilesManager(WORKING_DIRECTORY);
        excelManager = new ExcelManager(WORKING_DIRECTORY, null);
    }

    /**
     * Descromprime los contenidos de un archivo de propuestas en el destino indicado.
     *
     * @param proposalsFile: Archivo zip que contiene las propuestas
     * @param destDir:       destino donde se descomprimirán las propuestas
     */
    public void unzipProposals(String proposalsFile, String destDir) {
        // Descrompresión del archivo con las propuestas
        filesManager.unzip(proposalsFile, destDir);
    }

    /**
     * Esta función carga la información de las propuestas contenidas en un directorio
     *
     * @param proposalsPath: Directorio que contiene las propuestas
     * @return: Resultado del proceso
     */
    public ArrayList<ProposalInfo> loadProposalsFromDisc(String proposalsPath) throws IOException {
        // Cargamos la información de las propuestas
        this.proposals = filesManager.loadProposals(proposalsPath);
        return this.proposals;
    }

    public void saveProposalsToExcel(Path excelFile) throws IOException {
        // La guardamos en el archivo excel
        if (this.proposals != null) {
            excelManager.saveProposalsInfo(proposals);
        }
    }

    public void createReviews() throws IOException {
        HashMap<String, ReviewerInfo> reviewers = new HashMap<>();
        ArrayList<ProposalInfo> proposals = excelManager.readProposalsInfo();

        for (ProposalInfo p : proposals) {
            String r1Name = p.get("revisor1");
            ReviewerInfo r1 = reviewers.getOrDefault(r1Name, new ReviewerInfo(r1Name));
            r1.addProposal(p);
            reviewers.put(r1Name, r1);

            String r2Name = p.get("revisor2");
            ReviewerInfo r2 = reviewers.getOrDefault(r2Name, new ReviewerInfo(r2Name));
            r2.addProposal(p);
            reviewers.put(r2Name, r2);
        }
        // Hasta aquí tenemos gnerados una lista con la información de revisores
        ArrayList<ReviewerInfo> revieweres = new ArrayList<>(reviewers.values());
        System.out.println(revieweres);
        // Lo pasamos al gestor de archivos para que cree la estructura de información
        filesManager.saveReviewersInfo(revieweres);

    }

    /**
     * Carga del disco los resultados de las revisiones y los guarda en la lista maestra
     */
    public void loadReviewsResults() throws IOException {
        HashMap<String, ArrayList<ReviewInfo>> reviews = filesManager.loadReviewsResults();
        System.out.println(reviews);
        excelManager.saveReviewsResults(reviews);
    }


    /**
     * envío de las propuestas a los revisores
     */
    public void sendReviews() throws IOException, TemplateException, MessagingException {
        MailManager m = new MailManager(null, null);
        String content = "";

        Map<String, Object> input = new HashMap<String, Object>();
        input.put("name", "Luis Pedraza");
        input.put("titulation", "Máster Interuniversitario en Mecánica de Fluidos Computacional");
        input.put("date", "27 de abril");

        content = m.getReviewEmailContent(input);

        String username = System.getenv("MAIL_USERNAME");
        String password = System.getenv("MAIL_PASSWORD");
        MailManager mail = new MailManager(username, password);
        ArrayList<File> attachments = new ArrayList<File>();
        attachments.add(new File("/Users/luispedraza/OneDrive - Universidad Internacional de La Rioja/TFE-MANAGER/Revisores/Gómez, Alonso.zip"));
        attachments.add(new File("/Users/luispedraza/OneDrive - Universidad Internacional de La Rioja/TFE-MANAGER/Revisores/Pedraza, Luis.zip"));

        mail.send("luispedraza@gmail.com",
                "Asunto del mensaje",
                content,
                attachments);
    }


    /**
     * envío de los trabajos a los directores
     */
    public void sendToDirectors() {

    }
}
