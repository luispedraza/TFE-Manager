package TFEManagerLib;




import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.*;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
// http://poi.apache.org/components/spreadsheet/examples.html#xssf-only
/**
 * Esta clase sirve para gestionar un archivo Excel
 */
public class ExcelManager {
    private static final String DEFAULT_EXCEL_NAME = "lista_maestra.xlsx";
    private static final String PROPOSALS_SHEET = "ALUMNOS";
    private static final String PROPOSALS_TABLE_NAME = "ALUMNOS";
    private Path filePath;  // La ruta de la lista maestra con la que trabajamos
    private static final ArrayList<String> PROPOSALS_HEADERS = ProposalInfo.FIELDS;
    private static final String REVIEWERS_SHEET = "REVISORES";
    private static final String REVIEWERS_TABLE_NAME = "REVISORES";
    private static final ArrayList<String> REVIEWERS_HEADERS = ReviewerInfo.FIELDS;
    private static final String DIRECTOR_SHEET = "DIRECTORES";
    private static final String DIRECTOR_TABLE_NAME = "DIRECTORES";
    private static final ArrayList<String> DIRECTOR_HEADERS = DirectorInfo.FIELDS;
    private static final String PROGRESS_SHEET = "PROGRESO";
    private static final String PROGRESS_TABLE_NAME = "PROGRESO";

    /** Carga el archivo de configuraciónd de una lista maestra
     * del archivo de configuración en formato json
     * @return:
     */
    private String loadExcelConfigurationFile() {
        //ResourceBundle.getBundle();
        return "";
    }

    /**
     * Constructor del gestor de listas maestras
     * @param workingDirectory: Directorio de trabajo
     * @param fileName: Nombre de la lista maestra
     */
    public ExcelManager(String workingDirectory, String fileName) {
        if (fileName == null) {
            fileName = DEFAULT_EXCEL_NAME;
        }
        filePath = Paths.get(workingDirectory, fileName);
    }

    private void saveWorkbook(XSSFWorkbook wb) throws IOException {
        // Guaradamos el libro:
            FileOutputStream outputStream = new FileOutputStream(filePath.toString());
            wb.write(outputStream);
            outputStream.close();
            wb.close();
    }

    /** Limpia los contenidos de una hoja
     * @param sheet
     */
    private void clearSheet(XSSFSheet sheet) {
        for (Row row : sheet) {
            sheet.removeRow(row);
        }
    }

    /**
     * Función de ayuda para obtener el libro de trabajo según el path almacenado
     * @return: un libro de trabajo, o null si no puede abrirse
     */
    private XSSFWorkbook getWorkbook() {
        File f = new File(filePath.toString());
        if (f.isFile()) {
            try {
                return new XSSFWorkbook(new FileInputStream(f));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            return new XSSFWorkbook();
        }
        return null;
    }

    /** Función de ayuda para obtener una hoja por su nombre
     *
     * @param sheetName: nombre de la hoja de trabajo
     * @return: la hoja de trabajo, o crea una si no existía
     */
    private XSSFSheet getSheet(XSSFWorkbook wb, String sheetName) {
        XSSFSheet sheet = wb.getSheet(sheetName);
        return (sheet != null) ? sheet : wb.createSheet(sheetName);
    }

    /**
     * Obtiene la tabla contenida en una hoja a partir de su nombre
     * @param sheet: Hoja en la que buscamos la propuesta
     * @param tableName: Nombre de la tabla
     * @return
     */
    private XSSFTable getTable(XSSFSheet sheet, String tableName) {
        for (XSSFTable table : sheet.getTables()) {
            if (table.getName().equals(tableName)) {
                return table;
            }
        }
        return null;
    }

    /**
     * Lee la información que contiene la lista maestra sobre las propuestas
     * @return
     */
    public HashMap<String, ProposalInfo> readProposalsInfo() throws Exception {
        HashMap<String, ProposalInfo> proposals = new HashMap<>();

        ArrayList<HashMap<String, String>> tableData = readTable(PROPOSALS_SHEET, PROPOSALS_TABLE_NAME);
        for (HashMap<String, String> p : tableData) {
            proposals.put(p.get("apellido") + ", " + p.get("nombre"), new ProposalInfo(p));
        }

        return proposals;
    }

    /**
     * Se guarda la información de las propuestas
     * @param info: un array de diccionarios con la información de las propuestas
     */
    public void saveProposalsInfo(ArrayList<ProposalInfo> info) throws IOException {

        XSSFWorkbook wb = getWorkbook();
        XSSFSheet sheet = getSheet(wb, PROPOSALS_SHEET);

        XSSFTable table = getTable(sheet, PROPOSALS_TABLE_NAME);
        XSSFRow row = null;
        XSSFCell cell = null;

        if (table == null) {
            row = sheet.createRow(0);
            int j = 0;
            for (String header : PROPOSALS_HEADERS) {
                cell = row.createCell(j++);
                cell.setCellValue(header);
            }


            // Set which area the table should be placed in
            AreaReference reference = wb.getCreationHelper().createAreaReference(
                new CellReference(0, 0), new CellReference(info.size(), PROPOSALS_HEADERS.size()-1));
            table = sheet.createTable(reference);
            table.setName(PROPOSALS_TABLE_NAME);
            table.setDisplayName(PROPOSALS_TABLE_NAME);
            /*
             // For now, create the initial style in a low-level way
            table.getCTTable().addNewTableStyleInfo();
            table.getCTTable().getTableStyleInfo().setName("TableStyleMedium2");
            */
            /*
            // Style the table
            XSSFTableStyleInfo style = (XSSFTableStyleInfo) table.getStyle();
            style.setName("TableStyleMedium9");
            style.setShowColumnStripes(true);
            style.setShowRowStripes(true);
            style.setFirstColumn(false);
            style.setLastColumn(false);
            // TODO: para la ordenación de las columnas: https://stackoverflow.com/questions/28419961/how-to-add-table-heading-drop-down-with-apache-poi

             */
        }

        // Buscamos las cabeceras, por si hubieran cambiado las columnas de orden
        int startRow = table.getStartRowIndex();
        row = sheet.getRow(startRow);
        ArrayList<String> headers = new ArrayList<>();
        for (Cell c : row) {
            headers.add(c.getStringCellValue());
        }
        // Set the values for the table
        int j = 0;
        for (int i = startRow+1; i < info.size(); i++) {
            ProposalInfo proposal = info.get(i);
            row = sheet.createRow(i);
            //for (Map.Entry<String, String> entry : proposal.entrySet()) {
            j = 0;
            for (String header : headers) {
                cell = row.createCell(j++);
                cell.setCellValue(proposal.getValue(header));
            }

        }
        saveWorkbook(wb);
    }

    /** Función de ayuda para encontrar la fila en que se encuentra un alumno
     *
     * @param name
     * @param surname
     * @return
     */
    private int findStudentRow(XSSFSheet sheet, String surname, String name) {
        int surnameColumn = this.PROPOSALS_HEADERS.indexOf("apellido");
        int nameColumn = this.PROPOSALS_HEADERS.indexOf("nombre");

        XSSFRow row = null;
        for (int i=0; i<=sheet.getLastRowNum(); i++) {
            row = sheet.getRow(i);
            if (row.getCell(surnameColumn).getStringCellValue().equals(surname)) {
                if (row.getCell(nameColumn).getStringCellValue().equals(name)) {
                    System.out.println("Encontrado: " + surname + name);
                    return i;
                }
            }
        }
        return -1;
    }

    /** Guarda en la lista maestra los resultados de las propuestas junto con la decisión global
     *
     * @param reviews
     */
    public void saveReviewsResults(HashMap<String, ArrayList<ReviewInfo>> reviews) throws IOException {
        XSSFWorkbook wb = getWorkbook();
        XSSFSheet sheet = getSheet(wb, PROPOSALS_SHEET);
        XSSFTable table = getTable(sheet, PROPOSALS_TABLE_NAME);
        int review1Column = PROPOSALS_HEADERS.indexOf("veredicto1");
        int review2Column = PROPOSALS_HEADERS.indexOf("veredicto2");
        int resultColumn = PROPOSALS_HEADERS.indexOf("veredictoglobal");
        // TODO: Habría que comprobar que la tabla es no nula para dar más robustez.
        XSSFRow row = null;
        XSSFCell cell = null;
        for (String proposalName : reviews.keySet()) {
            // Las revisiones de la propuesta actual:
            ArrayList<ReviewInfo> proposalReviews = reviews.get(proposalName);
            String[] surname_name = proposalName.split("\\s*,\\s*");
            System.out.println(surname_name[0]);
            System.out.println(surname_name[1]);
            int studentRow = findStudentRow(sheet, surname_name[0], surname_name[1]);
            if (studentRow != -1) {
                row = sheet.getRow(studentRow);
                cell = row.getCell(review1Column);
                if (cell == null) {
                    cell = row.createCell(review1Column);
                }
                cell.setCellValue(proposalReviews.get(0).getStatus());

                cell = row.getCell(review2Column);
                if (cell == null) {
                    cell = row.createCell(review2Column);
                }
                cell.setCellValue(proposalReviews.get(1).getStatus());
                // TODO: HAGO ESTO AQUÍ COMO PRUEBA PERO LA DECISIÓN DEBERÍA TOMARLA OTRA CLASE
                cell = row.getCell(resultColumn);
                if (cell == null) {
                    cell = row.createCell(resultColumn);
                }
                cell.setCellValue("ACEPTADA");
            }
        }

        saveWorkbook(wb);
    }

    /** Función de utilidad para leer los datos en una tabla **
     *
     * @param sheetName: Nombre de la hoja que contiene la tabla
     * @param tableName: Nombre de la tabla que buscamos
     * @return
     */
    private ArrayList<HashMap<String, String>> readTable(String sheetName, String tableName) throws Exception {
        ArrayList<HashMap<String, String>> result = new ArrayList<>();
        ArrayList<String> headers = new ArrayList<>();
        DataFormatter formatter = new DataFormatter();  // Para trabajar con celdas con diferentes tipos de datos

        XSSFWorkbook wb = getWorkbook();
        XSSFSheet sheet = getSheet(wb, sheetName);
        XSSFTable table = getTable(sheet, tableName);
        if (table == null) {
            throw new Exception ("No se ha encontrado la tabla: " + tableName);
        }

        int startRow = table.getStartRowIndex();
        int endRow = table.getEndRowIndex();
        int startColumn = table.getStartColIndex();
        int endColumn = table.getEndColIndex();
        System.out.println("Coordenadas de la tabla " + tableName + ": "+ startRow + "-" + endRow + "-" + startColumn + "-" + endColumn);

        XSSFRow row;
        XSSFCell cell;
        int i;
        int j;
        for (i = startRow; i <= endRow; i++) {
            row = sheet.getRow(i);
            if (i==startRow) {
                for (j = startColumn; j<= endColumn; j++) {
                    cell = row.getCell(j);
                    if (cell != null) {
                        headers.add(formatter.formatCellValue(cell));
                    }
                }
                continue;
            }
            // El objeto que almacenará la información de la fila
            HashMap<String, String> info = new HashMap<>(table.getRowCount()-1);

            for (j = startColumn; j <= endColumn; j++) {
                cell = row.getCell(j);
                if (cell != null) {
                    info.put(headers.get(j), formatter.formatCellValue(cell));
                }
            }
            result.add(info);
        }
        System.out.println(String.format("Leídas %d filas de información", result.size()));
        System.out.println(result);
        return result;
    }

    /**
     * Se carga de la lista maestra la información de los revisores
     * @return
     */
    public HashMap<String, ReviewerInfo> readReviewersInfo() throws Exception {
        HashMap<String, ReviewerInfo> result = new HashMap<>();
        ArrayList<HashMap<String, String>> tableInfo = readTable(REVIEWERS_SHEET, REVIEWERS_TABLE_NAME);
        for (HashMap<String, String> r : tableInfo) {
            result.put(r.get("NOMBRE"), new ReviewerInfo(r));
        }
        return result;
    }

    /** lee la información de calificaciones de un borrador
     *
     */
    public HashMap<String, Submission> readGradesFromPlatform(String path) throws IOException {
        Workbook wb = WorkbookFactory.create(new File(path));
        Sheet sheet = wb.getSheetAt(0);

        HashMap<String, Submission> result = new HashMap<>();
        ArrayList<String> headers = new ArrayList<>();

        System.out.println(sheet);
        int i = -1;
        for (Row row : sheet) {
            i++;
            if (i == 0) {
                // Leemos los encabezados
                for (Cell cell : row) {
                    headers.add(cell.getStringCellValue());
                }
                continue;
            }
            int j = 0;
            HashMap<String, String> item = new HashMap<>();
            for (Cell cell : row) {
                item.put(headers.get(j), cell.getStringCellValue());
                j++;
            }
            Submission submission = new Submission(item);
            result.put(submission.getId(), submission);
        }
        return result;
    }

    /** Guarda en la lista maestra la información de progreso de revisión
     *  @param progress : información del progreso de revisión
     * @param type : BORRADOR1, BORRADOR2, BORRADOR3
     */
    public void saveGradingsProgress(HashMap<String, Submission> progress, String type) throws IOException {
        XSSFWorkbook wb = getWorkbook();
        XSSFSheet sheet = getSheet(wb, PROGRESS_SHEET);
        XSSFTable table = getTable(sheet, PROGRESS_TABLE_NAME);

        int ID_COLUMN = 0;
        int SUBMISSION_COLUMN = 0;
        switch (type) {
            case "BORRADOR1":
                SUBMISSION_COLUMN = 5;
                break;
            case "BORRADOR2":
                SUBMISSION_COLUMN = 6;
                break;
            case "BORRADOR3":
                SUBMISSION_COLUMN = 7;
        }

        int firstRow = table.getStartRowIndex();
        int endRow = table.getEndRowIndex();

        for (int i = firstRow+1; i< endRow; i++) {
            XSSFRow row = sheet.getRow(i);
            Submission submission = progress.get(row.getCell(ID_COLUMN).getStringCellValue());
            if (submission != null) {
                row.getCell(SUBMISSION_COLUMN).setCellValue(submission.getStatusCode());
            }
        }
        saveWorkbook(wb);
    }
}
