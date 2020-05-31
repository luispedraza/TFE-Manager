package TFEManagerLib;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;

import java.io.File;
import java.io.IOException;
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
    public void fillForm(String key, String value) {
        String filePath = this.filePath;
        try {
            PDDocument doc = PDDocument.load(new File(filePath));
            PDDocumentCatalog catalog = doc.getDocumentCatalog();
            PDAcroForm form = catalog.getAcroForm();
            if (form != null) {
                for (PDField field : form.getFields()) {
                    //System.out.println(field.getPartialName() + "====>" + field.getValueAsString());
                    System.out.println(field.getFullyQualifiedName() + "====>" + field.getValueAsString());
                }

                PDField field = form.getField(key);
                if (field != null) {
                    field.setValue(value);
                    field.setReadOnly(true);
                }
            }
            doc.save(this.filePath);
            doc.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getFormInfo(HashMap<String, String> info) {
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
    }

    public ProposalInfo parseProposal() {
        ProposalInfo proposalInfo = new ProposalInfo();
        getFormInfo(proposalInfo);
        return proposalInfo;
    }

    public ReviewInfo parseReview() {
        ReviewInfo info = new ReviewInfo();
        getFormInfo(info);
        // Aquí es posible hacer comprobaciones y filtrados para el caso de revisiones.
        if (info.containsKey("resultado")) {
            return info;
        }
        return null;
    }
}
