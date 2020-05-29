import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;

/**
 * Esta clase se encarga de todas las operaciones relacionadas
 * con el manejo de archivos y estructura de directorios
 * incluyendo la descompersión y compresión de archivos zip.
 */

public class FilesManager {
    private String wd;

    public FilesManager(String wd) {
        this.wd = wd;
    }

    public static void unzip(String zipFilePath, String destDir) {
        try {
            ZipFile zipFile = new ZipFile(zipFilePath);
            zipFile.extractAll(destDir);
        } catch (ZipException e) {
            e.printStackTrace();
        }
    }

}
