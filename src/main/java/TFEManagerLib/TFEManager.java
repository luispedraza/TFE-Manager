package TFEManagerLib;

import TFEManagerLib.Models.Director;
import TFEManagerLib.Models.Reviewer;
import TFEManagerLib.Models.Student;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Clase principal para la gestión de TFEs
 */

public class TFEManager {
    public static String WORKING_DIRECTORY;
    private FilesManager filesManager;
    private ExcelManager excelManager;
    private ArrayList<Student> proposals;

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
    public ArrayList<Student> loadProposalsFromDisc(String proposalsPath) throws IOException {
        // Cargamos la información de las propuestas
        this.proposals = filesManager.loadProposals(proposalsPath);
        return this.proposals;
    }

    public void saveProposalsToExcel(Path excelFile) throws Exception {
        // La guardamos en el archivo excel
        if (this.proposals != null) {
            excelManager.saveProposalsInfo(proposals);
        }
    }

    /**
     * Creación de los paquetes para enviar a los revisores
     * @throws Exception
     */
    public void createReviewPacks() throws Exception {
        ArrayList<Reviewer> reviewers =excelManager.readReviewersInfo();
        ArrayList<Student> proposals = excelManager.readProposalsInfo();
        // Mapeado de los revisores a un hashmap para buscarlos mejor:
        Map<String, Reviewer> reviewerMap = reviewers.stream().collect(
                Collectors.toMap(Reviewer::getName, r -> r)
        );

        for (Student p : proposals) {
            String r1Name = p.getRevieverName(0);
            Reviewer r1 = reviewerMap.get(r1Name);
            r1.addProposal(p);

            String r2Name = p.getRevieverName(1);
            Reviewer r2 = reviewerMap.get(r2Name);
            r2.addProposal(p);
        }
        // Lo pasamos al gestor de archivos para que cree la estructura de información
        filesManager.saveReviewPacks(reviewers);
    }

    /**
     * Carga del disco los resultados de las revisiones y los guarda en la lista maestra
     */
    public void loadReviewsResults(String fromPath) throws Exception {
        HashMap<String, ArrayList<ReviewInfo>> reviews = filesManager.loadReviewsResults(fromPath);
        System.out.println(reviews);
        excelManager.saveReviewsResults(reviews);
    }

    /**
     * Envío de las propuestas a los revisores
     */
    public void sendReviews() throws Exception {

        String username = System.getenv("MAIL_USERNAME");
        String password = System.getenv("MAIL_PASSWORD");

        // Obtenemos los zips con las revisiones
        ArrayList<File> zipFiles = filesManager.loadReviewPacks();

        // Obtenemos las información de los revisores, con los correos
        ArrayList<Reviewer> reviewers =  excelManager.readReviewersInfo();
        Map<String, Reviewer> reviewerMap = reviewers.stream().collect(Collectors.toMap(
                Reviewer::getName, r -> r
        ));

        // Comenzamos los envíos
        // Recorremos todos los zips para enviar su revisor:
        for (File zf : zipFiles) {
            String reviewerName = FilenameUtils.removeExtension(zf.getName());
            Reviewer r = reviewerMap.get(reviewerName);
            if (r == null) {
                throw new Exception("No se ha encontrado del revisor del paquete " + zf.getAbsolutePath());
            } else {
                MailManager mail = new MailManager(username, password);
                // El cuerpo del mensaje
                Map<String, Object> input = new HashMap<>();
                input.put("name", r.getName());
                input.put("titulation", "Máster Interuniversitario en Mecánica de Fluidos Computacional");
                input.put("date", "27 de abril");
                String content = mail.getReviewEmailContent(input);

                ArrayList<File> attachments = new ArrayList<File>();
                attachments.add(zf);

                mail.send(r.getEmail(),
                        "Revisiones de propuestas de TFM",
                        content,
                        attachments);
            }
        }
    }

    /** Se generan los arhichivos y comentarios de calificación de la propuesta
     * y el archivo zip para subir a sakai
     */
    public void generateGradings() throws Exception {
        ArrayList<Student> proposals = excelManager.readProposalsInfo();
        filesManager.copyReviewsToProposals();
    }

    /** Asignación automática de revisores **
     *
     * @throws Exception
     */
    public void assignReviewers() throws Exception {
        ArrayList<Student> proposals = excelManager.readProposalsInfo();
        ArrayList<Reviewer> reviewers = excelManager.readReviewersInfo();

        System.out.println(("hola"));

    }

    /**
     * Carga la información de progreso de revisión
     * @param path: ruta del excel de calificaciones
     * @param type: tipo de calificación: BORRADOR1, BORRADOR2, BORRADOR2
     */
    public void loadProgress(String path, String type) throws Exception {
        HashMap<String, Submission> progress = excelManager.readGradesFromPlatform(path);
        System.out.println(progress);
        excelManager.saveGradingsProgress(progress, type);
    }

    /** Asignación autoática de directores
     *
     */
    public void assignDirectors() throws Exception {
        ArrayList<Student> proposals = excelManager.readProposalsInfo();
        ArrayList<Director> directors = excelManager.readDirectorsInfo();

    }
}
