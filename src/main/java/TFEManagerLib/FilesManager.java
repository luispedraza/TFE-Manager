package TFEManagerLib;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Esta clase se encarga de todas las operaciones relacionadas
 * con el manejo de archivos y estructura de directorios
 * incluyendo la descompersión y compresión de archivos zip.
 */

public class FilesManager {
    private final String PROPOSALS_FOLDER = "Formulario";   // Directorio del WS que contiene las propuestas
    private final String ATTACHMENTS_FOLDER = "Adjuntos del envio";     // Directorio que contiene adjuntos de una entrega
    private final String FEEDBACK_FOLDER = "Adjuntos al comentario";    // Directorio que contiene archivos devueltos en la corrección
    private final String FEEDBACK_FILE = "comments.txt";        // Nombre del archivo que contiene los comentarios 
    private final String REVIEWS_FOLDER = "Revisores";      // Directorio del WS que contiene los paquetes de revisión
    private final String DOCS_FOLDER = "Docs";              // Directorio del WS que contiene la documentación
    private final String GUIDE_FILE = "Guia-TFM.pdf";
    private final String REVIEW_GUIDE_FILE = "IndicacionesRevisores.pdf";
    private final String REVIEW_TEMPLATE_FILE = "ReviewTemplate.pdf";

    private String wd;

    private String getDocsPath() {
        return Paths.get(this.wd, this.DOCS_FOLDER).toString();
    }

    ;

    private String getReviewsPath() {
        return Paths.get(this.wd, this.REVIEWS_FOLDER).toString();
    }

    ;

    private String getProposalsPath() {
        return Paths.get(this.wd, PROPOSALS_FOLDER).toString();
    }

    /**
     * Utilidad para encontrar la extensión de un archivo
     *
     * @param filePath
     * @return
     */
    private String getExtension(String filePath) {
        int dotIndex = filePath.lastIndexOf(".");
        String ext = (dotIndex == -1) ? "" : filePath.substring(dotIndex + 1);
        return ext;
    }

    public FilesManager(String wd) {
        this.wd = wd;
    }

    public void unzip(String zipFilePath, String destDir) {
        try {
            ZipFile zipFile = new ZipFile(zipFilePath);
            zipFile.extractAll(destDir);
        } catch (ZipException e) {
            e.printStackTrace();
        }
    }

    /**
     * Función de utilidad para comprimir información
     *
     * @param origin
     * @param destination
     * @throws ZipException
     */
    public void zipFolder(String origin, String destination) throws ZipException {
        ZipFile zipFile = new ZipFile(destination);
        File targetFile = new File(origin);
        if (targetFile.isFile()) {
            zipFile.addFile(targetFile);
        } else if (targetFile.isDirectory()) {
            zipFile.addFolder(targetFile);
        }

    }

    /**
     * Carga del disco la información de las propuestas, leyendo los formularios en pdf
     *
     * @param proposalsPath: Ruta donde se encuentran las carpetas con las propuestas
     * @throws IOException
     * @return: Listado de informaciones de propuestas
     */
    public ArrayList<ProposalInfo> loadProposals(String proposalsPath) throws IOException {
        ArrayList<ProposalInfo> proposals = new ArrayList<ProposalInfo>();
        File dir = new File(proposalsPath);
        for (File propDir : dir.listFiles()) {
            if (propDir.isDirectory()) {
                System.out.println(propDir.getName());
                System.out.println(propDir.getAbsolutePath());
                String path = propDir.getName();
                String[] info = path.split("\\(");
                String[] fullName = info[0].split(",");
                String surname = fullName[0].trim();
                String name = fullName[1].trim();
                String id = info[1].split("\\)")[0].trim();

                Path attachments = Paths.get(propDir.getAbsolutePath(), ATTACHMENTS_FOLDER);
                System.out.println("Buscando adjuntos en " + attachments);
                Files.list(attachments).forEach(file -> {
                    if (getExtension(file.getFileName().toString()).equals("pdf")) {
                        System.out.println("Buscando información de propuesta en : " + file.getFileName());
                        ProposalInfo proposalInfo = new PDFManager(file.toString()).parseProposal();

                        if (!proposalInfo.isEmpty()) {
                            // Corregimos nombre y appelidos para uniformizar según nombre de carpetas
                            proposalInfo.setName(name);
                            proposalInfo.setSurname(surname);
                            proposalInfo.setID(id);
                            proposalInfo.setLink(file.toString());

                            System.out.println(proposalInfo.toString());
                            proposals.add(proposalInfo);
                        }
                    }
                });
            }
        }
        return proposals;
    }

    private void makeDir(String path) {
        File directory = new File(path);
        if (!directory.exists()) {
            directory.mkdir();
        }
    }

    private void copyFile(String origin, String destiny) throws IOException {
        // TODO: Eliminar esta función que tal vez no es necesaria
        FileUtils.copyFile(new File(origin), new File(destiny));
    }

    /**
     * Esta función guarda en el disco los paquetes de revisión, llegando a generar el zip
     *
     * @param reviewers: son los revisores
     * @throws IOException
     */
    public void saveReviewPacks(ArrayList<ReviewerInfo> reviewers) throws IOException {
        String docsPath = getDocsPath();        // Contiene la documentación que adjuntaremos
        String reviewsPath = getReviewsPath();  // Ruta donde se almacenarán las revisiones (ws/revisores)
        makeDir(reviewsPath);
        // Directorio que contiene las propuestas
        File proposalsDir = new File(getProposalsPath());

        int reviewerIndex = 0;
        // recorremos la lista de revisores:
        for (ReviewerInfo r : reviewers) {
            reviewerIndex++;
            String rPath = Paths.get(reviewsPath, r.getName()).toString();
            makeDir(rPath);
            // Copiamos la documentación para el revisor:
            copyFile(Paths.get(docsPath, GUIDE_FILE).toString(),
                    Paths.get(rPath, GUIDE_FILE).toString());
            copyFile(Paths.get(docsPath, REVIEW_GUIDE_FILE).toString(),
                    Paths.get(rPath, REVIEW_GUIDE_FILE).toString());

            // recorremos las propuestas del revisor:
            for (ProposalInfo p : r.getProposals()) {
                String studentFullName = p.getFullName();
                String pPath = Paths.get(rPath, studentFullName).toString();
                makeDir(pPath);
                // Buscamos la propuesta del alumno en el directorio de propuestas
                for (File proposalOriginFile : Objects.requireNonNull(proposalsDir.listFiles(
                        (dir, name) -> (new File(dir, name).isDirectory() && name.startsWith(studentFullName))
                ))) {
                    // Hemos entrado la carpeta original del alumno

//                    for (File f : Objects.requireNonNull(new File(proposalOriginFile, ATTACHMENTS_FOLDER).listFiles((dir, name) -> name.endsWith(".pdf")))) {
//                        System.out.println(f.toString());
//                        // Copiamos y renombramos la ropuesta
//                        String proposalFormPath = Paths.get(pPath, studentFullName + ".pdf").toString();
//                        copyFile(f.toString(), proposalFormPath);
//
//                        // Copiamos el formulario de revisión
//                        String reviewFormName = String.format("Review_%d.pdf", reviewerIndex);
//                        String reviewFormPath = Paths.get(pPath, reviewFormName).toString();
//                        copyFile(Paths.get(docsPath, REVIEW_TEMPLATE_FILE).toString(),
//                                reviewFormPath);
//                        // Rellenamos campos preliminaares en la propuesta:
//                        // TODO: Esto se puede optimizar rellenando en un único paso
//                        PDFManager pdfManager = new PDFManager(reviewFormPath);
//                        pdfManager.fillForm("nombre", p.get("nombre"));
//                        pdfManager.fillForm("apellido", p.get("apellido"));
//                        pdfManager.fillForm("titulo", p.get("titulo"));
//                    }
                    for (File pdfProposalFile : Objects.requireNonNull(new File(proposalOriginFile, ATTACHMENTS_FOLDER).listFiles((dir, name) -> name.endsWith(".pdf")))) {
                        // COPIAMOS EL FORMULARIO DE REVISIÓN
                        String tempReviewFormPath = Paths.get(pPath, String.format("REVIEW%02d - " + studentFullName +  ".pdf", reviewerIndex)).toString();
                        copyFile(Paths.get(docsPath, REVIEW_TEMPLATE_FILE).toString(), tempReviewFormPath);
                        // Rellenamos campos iniciales en la propuesta:
                        PDFManager pdfManagerReview = new PDFManager(tempReviewFormPath);
                        String[] originKeys = {ProposalInfo.NAME_KEY, ProposalInfo.SURNAME_KEY, ProposalInfo.TITLE_KEY};
                        String[] pdfFormKeys = {PDFManager.PROPOSAL_NAME, PDFManager.PROPOSAL_SURNAME, PDFManager.PROPOSAL_TITLE};
                        pdfManagerReview.fillForm(p, originKeys, pdfFormKeys, false);
                        // Copiamos el propuesta del alumno
                        String tempProposalPath = Paths.get(pPath, studentFullName + ".pdf").toString();
                        copyFile(pdfProposalFile.toString(), tempProposalPath);
                        PDFManager pdfManagerProposal = new PDFManager((tempProposalPath));
                        pdfManagerProposal.fillForm(null, null, null, true);

                        String destinyPath = Paths.get(pPath, studentFullName + String.format(" - REVIEW%02d.pdf", reviewerIndex)).toString();
                        File tempProposalFile = new File(tempProposalPath);
                        File tempReviewFile = new File(tempReviewFormPath);
                        ArrayList<File> input = new ArrayList<>(Arrays.asList(tempProposalFile, tempReviewFile));
                        PDFManager.mergeFiles(input, new File(destinyPath));
                        // Borramos los archivos temporales:
                        tempProposalFile.delete();
                        tempReviewFile.delete();
                    }
                }
            }

            // Finalmente comprimimos el paquete del revisor
            zipFolder(rPath, Paths.get(reviewsPath, r.getName() + ".zip").toString());
        }
    }


    /**
     * Obtiene todos los paquetes de revisión
     *
     * @return: list de archivos zip con las revisiones de cada revisor
     */
    public ArrayList<File> loadReviewPacks() {
        ArrayList result = new ArrayList<File>();
        File reviewsFolder = new File(getReviewsPath());    // la carpeta donde están las revisiones
        for (File zipFile : Objects.requireNonNull(reviewsFolder.listFiles((dir, name) -> name.endsWith(".zip")))) {
            System.out.println(zipFile);
            result.add(zipFile);
        }
        return result;
    }


    /**
     * Lee del disco los resultados de las revisiones
     *
     * @return Un mapeado entre el nombre de cada alumno y los resultados de las dos revisiones.
     */
    public HashMap<String, ArrayList<ReviewInfo>> loadReviewsResults() {
        HashMap<String, ArrayList<ReviewInfo>> reviews = new HashMap<>();
        File reviewsFolder = new File(getReviewsPath());    // la carpeta donde están las revisiones (wb/Revisores)

        for (File reviewer : Objects.requireNonNull(reviewsFolder.listFiles((dir, name) -> new File(dir, name).isDirectory()))) {
            System.out.println("Buscando revisiones en: " + reviewer);
            for (File proposal : Objects.requireNonNull(reviewer.listFiles((dir, name) -> new File(dir, name).isDirectory()))) {
                System.out.println(proposal);
                String proposalName = proposal.getName();
                for (File pdfFile : Objects.requireNonNull(proposal.listFiles((dir, name) -> name.endsWith(".pdf")))) {
                    PDFManager pdfManager = new PDFManager(pdfFile.getAbsolutePath());
                    ReviewInfo info = pdfManager.parseReview();
                    if (info != null) {
                        ArrayList<ReviewInfo> proposalReviews = reviews.getOrDefault(proposalName, new ArrayList<>());
                        proposalReviews.add(info);
                        reviews.put(proposalName, proposalReviews);
                    }
                }
            }
        }
        return reviews;
    }

    /**
     * Copia las revisiones de las carpetas de propuestas para poder enviar a los alumnos
     */
    public void copyReviewsToProposals() throws IOException {
        File reviewsFolder = new File(getReviewsPath());    // la carpeta donde están las revisiones (wb/Revisores)
        File proposalsFolder = new File(getProposalsPath());    // la carpeta donde están las revisiones (wb/Formulario)

        for (File reviewer : Objects.requireNonNull(reviewsFolder.listFiles((dir, name) -> new File(dir, name).isDirectory()))) {
            System.out.println("Buscando revisiones en: " + reviewer);
            for (File proposal : Objects.requireNonNull(reviewer.listFiles((dir, name) -> new File(dir, name).isDirectory()))) {
                System.out.println(proposal);
                String proposalName = proposal.getName();
                // Ahora estamos en la carpeta de revisión de un alumno. Buscamos el pdf con su revisión
                for (File pdfFile : Objects.requireNonNull(proposal.listFiles((dir, name) -> name.endsWith(".pdf") && name.startsWith("Review")))) {
                    for (File destiny : Objects.requireNonNull(proposalsFolder.listFiles((dir, name) -> name.startsWith(proposalName)))){
                        String feedbackFolder = Paths.get(destiny.toString(), FEEDBACK_FOLDER).toString();
                        makeDir(feedbackFolder);
                        FileUtils.copyFileToDirectory(new File(pdfFile.toString()), new File(feedbackFolder));
                    }
                }
            }
        }
    }

}
