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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Esta clase se encarga de todas las operaciones relacionadas
 * con el manejo de archivos y estructura de directorios
 * incluyendo la descompersión y compresión de archivos zip.
 */

public class FilesManager {
    private final String PROPOSALS_FOLDER = "Formulario";   // Directorio del WS que contiene las propuestas
    private final String ATTACHMENTS_FOLDER = "Adjuntos del envio"; // Directorio que contiene adjuntos de una entrega
    private final String REVIEWS_FOLDER = "Revisores";      // Directorio del WS que contiene los paquetes de revisión
    private final String DOCS_FOLDER = "Docs";              // Directorio del WS que contiene la documentación
    private final String GUIDE_FILE = "Guia-TFM.pdf";
    private final String REVIEW_GUIDE_FILE = "IndicacionesRevisores.pdf";
    private final String REVIEW_TEMPLATE_FILE = "ReviewTemplate.pdf";

    private String wd;

    private String getDocsPath() {
        return Paths.get(this.wd, this.DOCS_FOLDER).toString();
    };
    private String getReviewsPath() {
        return Paths.get(this.wd, this.REVIEWS_FOLDER).toString();
    };
    private String getProposalsPath() {
        return Paths.get(this.wd, PROPOSALS_FOLDER).toString();
    }

    /**
     * Utilidad para encontrar la extensión de un archivo
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

    /** Función de utilidad para comprimir información
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

    public ArrayList<ProposalInfo> loadProposals(String proposalsPath) throws IOException {
        ArrayList<ProposalInfo> proposals = new ArrayList<ProposalInfo>();
        File dir = new File(proposalsPath);
        for (File prop : dir.listFiles()) {
            if (prop.isDirectory()) {
                System.out.println(prop.getName());
                System.out.println(prop.getAbsolutePath());
                String path = prop.getName();
                String[] info = path.split("\\(");
                String[] fullName = info[0].split(",");
                String surname = fullName[0].trim();
                String name = fullName[1].trim();
                String id = info[1].split("\\)")[0].trim();
                System.out.println("Nombre: " + name);
                System.out.println("Apellidos: " + surname);
                System.out.println("ID: " + id);
                Path attachments = Paths.get(prop.getAbsolutePath(), ATTACHMENTS_FOLDER);
                System.out.println("Buscando adjuntos en " + attachments);
                Files.list(attachments).forEach(file -> {
                    if (getExtension(file.getFileName().toString()).equals("pdf")) {
                        System.out.println("Buscando información de propuesta en : " + file.getFileName());
                        ProposalInfo proposalInfo = new PDFManager(file.toString()).parseProposal();

                        if (!proposalInfo.isEmpty()) {
                            // Corregimos nombre y appelidos para uniformizar según nombre de carpetas
                            proposalInfo.put("nombre", name);
                            proposalInfo.put("apellido", surname);
                            proposalInfo.put("ID", id);
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
        FileUtils.copyFile(new File(origin), new File(destiny));
    }

    /**
     * Esta función guarda en el disco los paquetes de revisión, llegando a generar el zip
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
                String proposalFolderName = p.getFolderName();
                String pPath = Paths.get(rPath, proposalFolderName).toString();
                makeDir(pPath);
                // Buscamos la propuesta del alumno en el directorio de propuestas
                System.out.println("Buscando la propuesta de " + proposalFolderName + " para " + r.getName());
                for (File proposalOrigin : Objects.requireNonNull(proposalsDir.listFiles(
                        (dir, name) -> (new File(dir, name).isDirectory() && name.startsWith(proposalFolderName))
                ))) {
                    System.out.println("Encontrada en : ");
                    System.out.println(proposalOrigin);
                    for (File f : Objects.requireNonNull(new File(proposalOrigin, ATTACHMENTS_FOLDER).listFiles((dir, name) -> name.endsWith(".pdf")))) {
                        System.out.println(f.toString());
                        // Copiamos y renombramos la ropuesta
                        String proposalFormPath = Paths.get(pPath, proposalFolderName+".pdf").toString();
                        copyFile(f.toString(), proposalFormPath);
                        // Copiamos el formulario de revisión
                        String reviewFormName = String.format("Review_%d.pdf", reviewerIndex);
                        String reviewFormPath = Paths.get(pPath, reviewFormName).toString();
                        copyFile(Paths.get(docsPath, REVIEW_TEMPLATE_FILE).toString(),
                                reviewFormPath);
                        // Rellenamos campos preliminaares en la propuesta:
                        // TODO: Esto se puede optimizar rellenando en un único paso
                        PDFManager pdfManager = new PDFManager(reviewFormPath);
                        pdfManager.fillForm("nombre", p.get("nombre"));
                        pdfManager.fillForm("apellido", p.get("apellido"));
                        pdfManager.fillForm("titulo", p.get("titulo"));
                    }
                }
            }

            // Finalmente comprimimos el paquete del revisor
            zipFolder(rPath, Paths.get(reviewsPath, r.getName()+".zip").toString());
        }
    }


    /** Obtiene todos los paquetes de revisión
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
     * @return Un mapeado entre el nombre de cada alumno y los resultados de las dos revisiones.
     */
    public HashMap<String, ArrayList<ReviewInfo>> loadReviewsResults() {
        HashMap<String, ArrayList<ReviewInfo>> reviews =  new HashMap<>();
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
}