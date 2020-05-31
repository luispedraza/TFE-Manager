package TFEManagerLib;




import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
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
    private static final String PROPOSALS_SHEET = "PROPUESTAS";
    private static final String PROPOSALS_TABLE_NAME = "PROPUESTAS";
    private Path filePath;  // La ruta de la lista maestra con la que trabajamos
    private static final ArrayList<String> PROPOSALS_HEADERS = ProposalInfo.FIELDS;
    private static final String REVIEWERS_SHEET = "REVISORES";
    private static final String REVIEWERS_TABLE_NAME = "REVISORES";
    private static final ArrayList<String> REVIEWERS_HEADERS = ReviewerInfo.FIELDS;
    private static final String DIRECTOR_SHEET = "REVISORES";
    private static final String DIRECTOR_TABLE_NAME = "REVISORES";
    private static final ArrayList<String> DIRECTOR_HEADERS = DirectorInfo.FIELDS;

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
    public ArrayList<ProposalInfo>  readProposalsInfo() {
        ArrayList<ProposalInfo> proposals = new ArrayList<ProposalInfo>();
        XSSFWorkbook wb = getWorkbook();
        XSSFSheet sheet = getSheet(wb, PROPOSALS_SHEET);
        XSSFTable table = getTable(sheet, PROPOSALS_TABLE_NAME);
        System.out.println(table);

        int startRow = table.getStartRowIndex();
        int endRow = table.getEndRowIndex();
        int startColumn = table.getStartColIndex();
        int endColumn = table.getEndColIndex();
        System.out.println("Coordenadas de la tabla " + PROPOSALS_TABLE_NAME + ": "+ startRow + "-" + endRow + "-" + startColumn + "-" + endColumn);

        for (int i = startRow+1; i<=endRow; i++) {
            ProposalInfo p = new ProposalInfo();
            XSSFRow row = sheet.getRow(i);
            for (int j = startColumn; j <= endColumn; j++) {
                XSSFCell c = row.getCell(j);
                if (c != null) {
                    p.put(PROPOSALS_HEADERS.get(j), c.getStringCellValue());
                }
            }
            proposals.add(p);
        }
        System.out.println(String.format("Leídas %d propuestas", proposals.size()));
        System.out.println(proposals);
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
        if (table == null) {
            // Set which area the table should be placed in
            AreaReference reference = wb.getCreationHelper().createAreaReference(
                new CellReference(0, 0), new CellReference(info.size(), PROPOSALS_HEADERS.size()-1));
            table = sheet.createTable(reference);
            table.setName(PROPOSALS_TABLE_NAME);
            table.setDisplayName("Propuestas");
             // For now, create the initial style in a low-level way
            table.getCTTable().addNewTableStyleInfo();
            table.getCTTable().getTableStyleInfo().setName("TableStyleMedium2");

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

        // Set the values for the table
        XSSFRow row;
        XSSFCell cell;
        int i = 0;
        int j = 0;
        for (i = 0; i < info.size(); i++) {
            ProposalInfo proposal = info.get(i);

            if (i==0) {
                // Cabecera de la tabla
                row = sheet.createRow(i);
                j = 0;
                for (String header : PROPOSALS_HEADERS) {
                    cell = row.createCell(j++);
                    cell.setCellValue(header);
                }
            }
            j = 0;
            row = sheet.createRow(i+1);
            //for (Map.Entry<String, String> entry : proposal.entrySet()) {
            for (String header : PROPOSALS_HEADERS) {
                cell = row.createCell(j++);
                cell.setCellValue(proposal.get(header));
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
    int findStudentRow(XSSFSheet sheet, String surname, String name) {
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
        int review1Column = this.PROPOSALS_HEADERS.indexOf("veredicto1");
        int review2Column = this.PROPOSALS_HEADERS.indexOf("veredicto2");
        int resultColumn = this.PROPOSALS_HEADERS.indexOf("veredictoglobal");
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
            }
        }

        saveWorkbook(wb);
    }
}
