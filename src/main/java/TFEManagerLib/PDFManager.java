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

    // Nombres de campos en la propuesta del alumnos (PDF)
    public static final String PROPOSAL_NAME = "nombre";
    public static final String PROPOSAL_SURNAME = "apellido";
    public static final String PROPOSAL_COUNTRY = "pais";
    public static final String PROPOSAL_TITLE = "titulo";
    public static final String PROPOSAL_TYPE = "tipo";
    public static final String PROPOSAL_FORMER_DIRECTOR = "director_anterio";
    public static final String PROPOSAL_CONTINUE_DIRECTOR = "seguir";
    public static final String PROPOSAL_L1 = "linea1";
    public static final String PROPOSAL_L2 = "linea1";
    public static final String PROPOSAL_L3 = "linea1";
    public static final String PROPOSAL_L4 = "linea1";
    public static final String PROPOSAL_L5 = "linea1";
    public static final String PROPOSAL_L6 = "linea1";
    public static final String PROPOSAL_L7 = "linea1";
    public static final String PROPOSAL_L8 = "linea1";

    // Nombres de campos en el formulario de revisión (PDF)
    public static final String REVIEW_FORM_NAME = "nombre";
    public static final String REVIEW_FORM_SURNAME = "apellido";
    public static final String REVIEW_FORM_TITLE = "titulo";


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

        if (data!=null) {
            if (form != null) {
//                for (PDField field : form.getFields()) {
//                    //System.out.println(field.getPartialName() + "====>" + field.getValueAsString());
//                    System.out.println(field.getFullyQualifiedName() + "====>" + field.getValueAsString());
//                }
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
        ProposalInfo proposal = new ProposalInfo();
        HashMap<String, String> info = getFormInfo();
        proposal.setName(info.get(PROPOSAL_NAME));
        proposal.setSurname(info.get(PROPOSAL_SURNAME));
        proposal.setFormerDirector(String.join(" - ",
                info.get(PROPOSAL_FORMER_DIRECTOR),
                info.get(PROPOSAL_CONTINUE_DIRECTOR)));
        proposal.setTitle(info.get(PROPOSAL_TITLE));
        proposal.setCountry(info.get(PROPOSAL_COUNTRY));
        proposal.setType(info.get(PROPOSAL_TYPE));

        return proposal;
    }

    public ReviewInfo parseReview() {
        HashMap<String, String> info = getFormInfo();
        // Aquí es posible hacer comprobaciones y filtrados para el caso de revisiones.
        if (info.containsKey("resultado")) {
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
