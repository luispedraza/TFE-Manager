package TFEManagerLib;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class ReviewInfo extends HashMap<String, String> {
    public static final ArrayList<String> FIELDS = new ArrayList<String>(
            Arrays.asList("nombre_alumno",
                    "nombre_revisor",
                    "adecuacion",
                    "alcance",
                    "amenazas",
                    "sugerencias",
                    "valoracion")
    );

    /** devuelve el estado de aceptación de la propuesta
     *
     * @return
     */
    public String getStatus() {
        switch (this.get("resultado")) {
            case "aceptada":
                return "ACEPTADA";
            case "condicional":
                return "ACEPTADO CONDICONAL";
            case "mejorar":
                return "DEBE MEJORAR";
            case "rechazada":
                return "RECHAZADA";
        }
        return "";
    }
}
