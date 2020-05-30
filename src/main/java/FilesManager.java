import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Dictionary;

/**
 * Esta clase se encarga de todas las operaciones relacionadas
 * con el manejo de archivos y estructura de directorios
 * incluyendo la descompersión y compresión de archivos zip.
 */

public class FilesManager {
    private final String ATTACHMENTS_FOLDER = "Adjuntos del envio";
    private String wd;

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
                        ProposalInfo proposalInfo = PDFManager.parseProposal(file.toString());
                        if (!proposalInfo.isEmpty()) {
                            proposals.add(proposalInfo);
                        }
                    }
                });
            }
        }
        return proposals;
    }

}
