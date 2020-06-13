package TFEManagerLib;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class ReviewInfo extends HashMap<String, String> {
    public static final String REVIEW_STUDENT_NAME = "NOMBRE";
    public static final String REVIEW_STUDENT_SURNAME = "APELLIDOS";
    public static final String REVIEW_REVIEWER_NAME = "REVIEWER-NAME";
    public static final String REVIEW_ADEQUACY = "ADECUACION";
    public static final String REVIEW_SCOPE = "ALCANCE";
    public static final String REVIEW_THREATS = "AMENAZAS";
    public static final String REVIEW_COMMENTS = "SUGERENCIAS";
    public static final String REVIEW_STATUS = "RESULTADO";
    public static final String REVIEW_PREFERENCE = "PREFERENCIA";

    public static final ArrayList<String> FIELDS = new ArrayList<>(
            Arrays.asList(REVIEW_STUDENT_NAME,
                    REVIEW_REVIEWER_NAME,
                    REVIEW_ADEQUACY,
                    REVIEW_SCOPE,
                    REVIEW_THREATS,
                    REVIEW_COMMENTS,
                    REVIEW_STATUS)
    );
    public static final ArrayList<String> STATUS_CODES = new ArrayList<>(
            Arrays.asList(
                    "ACEPTADA",
                    "CONDICIONAL",
                    "MEJORAR",
                    "RECHAZADA"
            )
    );

    public ReviewInfo(HashMap<String, String> info) {
        super(info);
    }

    public String getFullName() {
        return this.get(REVIEW_STUDENT_SURNAME) + ", " + this.get(REVIEW_STUDENT_NAME);
    }

    /** Calcula un resultado global a partir de dos revisiones
     *
     * @param r1:
     * @param r2:
     * @return: Código de la resolución.
     */
    public static String computeDecision (ReviewInfo r1, ReviewInfo r2) {
        int i1 = STATUS_CODES.indexOf(r1.getStatusCode());
        int i2 = STATUS_CODES.indexOf(r2.getStatusCode());
        // De manera simple, tomamos la decisión más restrictiva
        return STATUS_CODES.get(Math.max(i1, i2));
    }

    /** devuelve el estado de aceptación de la propuesta
     *
     * @return
     */
    public String getStatusCode() {
        return this.get(REVIEW_STATUS);
    }

    public boolean getPreference() {
        return this.get(REVIEW_PREFERENCE).equals("SI");
    }
}
