package TFEManagerLib;

import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Clase para leer los contenidos de un formulario en pdf
 */
public class PDFManager {

    String filePath;
    public PDFManager(String filePath) {
        this.filePath = filePath;
    }

    /** Método para rellenar el campo de un formulario
     *
     * @param key: nombre del campo
     * @param value: valor que queremos insertar
     */
    public void fillForm(HashMap<String, String> data, String[] keys, boolean flatten) {
        String filePath = this.filePath;
        try {
            PDDocument doc = PDDocument.load(new File(filePath));
            PDDocumentCatalog catalog = doc.getDocumentCatalog();
            PDAcroForm form = catalog.getAcroForm();
            if (form != null) {
//                for (PDField field : form.getFields()) {
//                    //System.out.println(field.getPartialName() + "====>" + field.getValueAsString());
//                    System.out.println(field.getFullyQualifiedName() + "====>" + field.getValueAsString());
//                }
                for (String key : keys) {
                    String value = data.get(key);
                    PDField field = form.getField(key);
                    if (field != null) {
                        field.setValue(value);
                        //field.setReadOnly(true);
                    }
                }

            }
            // Eliminamos los campos de formulario manteniendo el contenido
            if (flatten) {
                form.flatten();
            }
            doc.save(this.filePath);
            doc.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Obtiene toda la información que contiene el formulario de un pdf
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
                    // System.out.println(field.getPartialName() + "====>" + field.getValueAsString());
                    info.put(field.getPartialName(), field.getValueAsString());
                }
            }
            doc.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return info;
    }

    public ProposalInfo parseProposal() {
        return new ProposalInfo(getFormInfo());
    }

    public ReviewInfo parseReview() {
        HashMap<String, String> info = getFormInfo();
        // Aquí es posible hacer comprobaciones y filtrados para el caso de revisiones.
        if (info.containsKey("resultado")) {
            return new ReviewInfo(info);
        }
        return null;
    }

    /** Clase de utilidad para juntar varios pdfs
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
