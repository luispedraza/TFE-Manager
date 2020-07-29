package TFEManagerLib;

import TFEManagerLib.Models.Student;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Clase para leer los contenidos de un formulario en pdf
 */
public class PDFManager {

    // Nombres de campos en la propuesta del alumnos (PDF)
    public static final String PROPOSAL_NAME = "nombre";
    public static final String PROPOSAL_SURNAME = "apellido";
    public static final String PROPOSAL_COUNTRY = "pais";
    public static final String PROPOSAL_TITLE = "titulo";
    public static final String PROPOSAL_TYPE = "tipo";
    public static final String PROPOSAL_FORMER_DIRECTOR = "director_anterior";
    public static final String PROPOSAL_CONTINUE_DIRECTOR = "cambiar";
    public static final String PROPOSAL_LINE = "LINEA%d";   // No sabemos el número de líneas que habrá en la propuesta

    // Nombres de campos en el formulario de revisión (PDF)
    public static final String REVIEW_FORM_NAME = "NOMBRE";
    public static final String REVIEW_FORM_SURNAME = "APELLIDOS";
    public static final String REVIEW_FORM_TITLE = "TITULO";
    public static final String REVIEW_FORM_STATUS = "RESULTADO";
    public static final String REVIEW_FORM_ID = "ID";


    String filePath;

    public PDFManager(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Método para rellenar el campo de un formulario
     *
     * @param key:   nombre del campo
     * @param value: valor que queremos insertar
     */
    public void fillForm(HashMap<String, String> data, String[] originKeys, String[] pdfKeys, boolean flatten) throws IOException {
        String filePath = this.filePath;

        PDDocument doc = PDDocument.load(new File(filePath));
        PDDocumentCatalog catalog = doc.getDocumentCatalog();
        PDAcroForm form = catalog.getAcroForm();

        if (data != null) {
            if (form != null) {
                for (int i = 0; i < originKeys.length; i++) {
                    PDField field = form.getField(pdfKeys[i]);
                    if (field != null) {
                        field.setValue(data.get(originKeys[i]));
                        field.setReadOnly(true);
                    }
                }
            }
        }

        // Eliminamos los campos de formulario manteniendo el contenido
        if (flatten) {
            form.flatten();
        }
        doc.save(this.filePath);
        doc.close();

    }

    /**
     * Rellena un único campo de un formulario
     *
     * @param key:   clave del formulario
     * @param value: valor a insertar
     * @throws IOException
     */
    public void fillForm(String key, String value) throws IOException {
        String filePath = this.filePath;
        PDDocument doc = PDDocument.load(new File(filePath));
        PDDocumentCatalog catalog = doc.getDocumentCatalog();
        PDAcroForm form = catalog.getAcroForm();
        if (form != null) {
            PDField field = form.getField(key);
            if (field != null) {
                field.setValue(value);
                field.setReadOnly(true);
                field.setReadOnly(true);
            }
        }
        doc.save(this.filePath);
        doc.close();
    }

    /**
     * Obtiene toda la información que contiene el formulario de un pdf
     *
     * @return: Un HashMap con la información
     */
    private HashMap<String, String> getFormInfo() {
        HashMap<String, String> info = new HashMap<>();
        String filePath = this.filePath;

        try {
            PDDocument doc = PDDocument.load(new File(filePath));
            PDDocumentCatalog catalog = doc.getDocumentCatalog();
            PDAcroForm form = catalog.getAcroForm();
            if (form != null) {
                for (PDField field : form.getFields()) {
                    info.put(field.getPartialName(), field.getValueAsString());
                }
            }
            doc.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return info;
    }

    public Student parseProposal(Student student) {
        HashMap<String, String> info = getFormInfo();
        if (student == null) {
            student = new Student();
            // Si nos pasan por la propuesta, ya tiene identificación
            student.setName(info.get(PROPOSAL_NAME));
            student.setSurname(info.get(PROPOSAL_SURNAME));
            student.setFullName();
        }

        student.setTitle(info.get(PROPOSAL_TITLE));
        student.setCountry(info.get(PROPOSAL_COUNTRY));
        student.setType(info.get(PROPOSAL_TYPE));

        // Buscamos las líneas de trabajo (ASUMIENDO QUE A LO SUMO HAY 10)
        ArrayList<Integer> lines = new ArrayList<>();
        for (int i = 1; i < 10; i++) {
            String line = info.get(String.format(PROPOSAL_LINE, i));
            if (line == null) break;
            if (line.equals("Sí")) {
                lines.add(i);
            }
        }
        student.setLines(lines);
        student.setFormerInfo (
                info.get(PROPOSAL_FORMER_DIRECTOR),
                info.get(PROPOSAL_CONTINUE_DIRECTOR));
        return student;
    }

    public ReviewInfo parseReview() {
        HashMap<String, String> info = getFormInfo();
        // Aquí es posible hacer comprobaciones y filtrados para el caso de revisiones.
        if (info.containsKey(REVIEW_FORM_STATUS)) {
            return new ReviewInfo(info);
        }
        return null;
    }

    /**
     * Clase de utilidad para juntar varios pdfs
     *
     * @param input
     * @param output
     */
    public static void mergeFiles(ArrayList<File> input, File output) throws IOException {
        PDFMergerUtility pdfMerger = new PDFMergerUtility();
        pdfMerger.setDestinationFileName(output.toString());
        for (File pdf : input) {
            pdfMerger.addSource(pdf);
        }
        pdfMerger.mergeDocuments(null);
    }
}
