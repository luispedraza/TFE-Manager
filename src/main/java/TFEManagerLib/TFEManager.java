package TFEManagerLib;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;

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
     * @param proposalsFile: Archivo zip que contiene las propuestas
     * @param destDir: destino donde se descomprimirán las propuestas
     */
    public void unzipProposals(String proposalsFile, String destDir) {
        // Descrompresión del archivo con las propuestas
        filesManager.unzip(proposalsFile, destDir);
    }

    /**
     * Esta función carga la información de las propuestas contenidas en un directorio
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

    public void createReviews() {
        HashMap<String, ReviewerInfo> reviewers = new HashMap<>();
        ArrayList<ProposalInfo> proposals = excelManager.readProposalsInfo();

        for (ProposalInfo p : proposals) {
            String r1Name = p.get("revisor1");
            ReviewerInfo r1 = reviewers.getOrDefault(r1Name, new ReviewerInfo(r1Name));
            r1.addProposal(p);
            reviewers.put(r1Name, r1);

            String r2Name = p.get("revisor2");
            ReviewerInfo r2 =  reviewers.getOrDefault(r2Name, new ReviewerInfo(r2Name));
            r2.addProposal(p);
            reviewers.put(r2Name, r2);
        }
        System.out.println(
                new ArrayList<>(reviewers.values())
        );

    }

}
