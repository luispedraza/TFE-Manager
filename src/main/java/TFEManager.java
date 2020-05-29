/**
 * Clase principal para la gestión de TFEs
 */

public class TFEManager {
    public static String WORKING_DIRECTORY;
    private FilesManager filesManager;

    public TFEManager(String workingDirectory) {
        WORKING_DIRECTORY = workingDirectory;
        filesManager = new FilesManager(WORKING_DIRECTORY);
    }

    public void unzipProposals(String proposalsFile, String destDir) {
        // Descrompresión del archivo con las propuestas
        filesManager.unzip(proposalsFile, destDir);
    }

    /**
     * Esta función carga la información de las propuestas contenidas en un directorio
     * @param proposalsPath: Directorio que contiene las propuestas
     * @return: Resultado del proceso
     */
    public String loadProposals(String proposalsPath) {

        
        return "Proceso completado con éxito";
    }
}
