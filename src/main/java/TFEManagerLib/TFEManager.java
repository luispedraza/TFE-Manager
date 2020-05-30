package TFEManagerLib;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;

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
    public ArrayList<ProposalInfo> loadProposals(String proposalsPath) throws IOException {
        // Cargamos la información de las propuestas
        this.proposals = filesManager.loadProposals(proposalsPath);
        return this.proposals;
    }

    public void saveProposals(Path excelFile) throws IOException {
        // La guardamos en el archivo excel
        if (this.proposals != null) {
            excelManager.saveProposalsInfo(proposals);
        }
    }

}
