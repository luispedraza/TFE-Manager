package TFEManagerLib;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class ReviewInfo extends HashMap<String, String> {
    public static final String REVIEW_STUDENT_NAME = "nombre";
    public static final String REVIEW_STUDENT_SURNAME = "apellido";
    public static final String REVIEW_REVIEWER_NAME = "nombre_revisor";
    public static final String REVIEW_ADEQUACY = "adecuacion";
    public static final String REVIEW_SCOPE = "alcance";
    public static final String REVIEW_THREATS = "amenazas";
    public static final String REVIEW_COMMENTS = "sugerencias";
    public static final String REVIEW_STATUS = "resultado";
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
        // TODO: ARREGLAR FORMULARIOS PARA QUE ESTO NO SEA NECESARIO
        switch (this.get("resultado")) {
            case "aceptada":
                return STATUS_CODES.get(0);
            case "condicional":
                return STATUS_CODES.get(1);
            case "mejorar":
                return STATUS_CODES.get(2);
            case "rechazada":
                return STATUS_CODES.get(3);
        }
        return "----------";
    }
}
