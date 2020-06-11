package TFEManagerLib;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Contiene la información de correcciones de una entrega
 */
public class Submission {
    String id;
    String surname;
    String name;
    String status;
    boolean submitted;

    /**
     * Se construye el objeto a partir de la información leída del excel de calificaciones
     * @param data
     */
    public Submission(HashMap<String, String> data) {
        this.id = data.get("ID");
        this.surname = data.get("Apellido");
        this.name = data.get("Nombre");
        this.status = data.get("nota").toUpperCase();
        this.submitted = data.get("Fecha de envío").isEmpty() ? false : true;
    }

    public String getId() {
        return id;
    }

    public String getSurname() {
        return surname;
    }

    public String getName() {
        return name;
    }

    public String getStatus() {
        return this.status;
    }

    public String getStatusCode() {
        if (!this.submitted) return "NE";
        if (this.status.equals("SIN CALIFICAR")) return "NC";
        if (this.status.equals("APROBADO")) return "+";
        if (this.status.equals("NO APTO")) return "-";
        return this.status;
    }

    public boolean isSubmitted() {
        return submitted;
    }
}
