import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Map;
// http://poi.apache.org/components/spreadsheet/examples.html#xssf-only
/**
 * Esta clase sirve para gestionar un archivo Excel
 */
public class ExcelManager {
    private static final String DEFAULT_EXCEL_NAME = "lista_maestra.xlsx";
    private static final String PROPOSALS_SHEET = "PROPUESTAS";
    private Path filePath;  // La ruta de la lista maestra con la que trabajamos

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

    /**
     * Se guarda la información de las propuestas
     * @param info: un array de diccionarios con la información de las propuestas
     */
    public void saveProposalsInfo(ArrayList<ProposalInfo> info) throws IOException {
        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet = wb.createSheet(PROPOSALS_SHEET);
        // Set which area the table should be placed in
        AreaReference reference = wb.getCreationHelper().createAreaReference(
                new CellReference(0, 0), new CellReference(2, 2));

        // Create
        XSSFTable table = sheet.createTable(reference);
        table.setName("propuestas");
        table.setDisplayName("Propuestas");

        // For now, create the initial style in a low-level way
        table.getCTTable().addNewTableStyleInfo();
        table.getCTTable().getTableStyleInfo().setName("TableStyleMedium2");

        // Style the table
        XSSFTableStyleInfo style = (XSSFTableStyleInfo) table.getStyle();
        style.setName("TableStyleMedium2");
        style.setShowColumnStripes(true);
        style.setShowRowStripes(true);
        style.setFirstColumn(false);
        style.setLastColumn(false);

        // Set the values for the table
        XSSFRow row;
        XSSFCell cell;

        for (int i = 0; i < info.size(); i++) {
            ProposalInfo proposal = info.get(i);

            if (i==0) {
                // Cabecera de la tabla
                row = sheet.createRow(i);
                int j = 0;
                for (String key : proposal.keySet()) {
                    System.out.println(key);
                    cell = row.createCell(j++);
                    cell.setCellValue(key);
                }
            }
            int j = 0;
            row = sheet.createRow(i+1);
            for (Map.Entry<String, String> entry : proposal.entrySet()) {
                System.out.println(entry.getKey() + "===>" + entry.getValue());
                cell = row.createCell(j++);
                cell.setCellValue(entry.getValue());
            }

        }
        // Guaradamos el libro:
        try (FileOutputStream outputStream = new FileOutputStream(filePath.toString())) {
            wb.write(outputStream);
            wb.close();
        }
    }
}
