package TFEManagerLib.Models;


import java.util.HashMap;

/** Clase básica para StudentInfo, reviewerInfo y Director Info
 *  Especifica las características comunes como son:
 *  Nombre
 *  Apellidos
 *  Email
 *  Nombre completo
 *  Tipos de trabajo preferidos
 *  Líneas de trabajo preferidas
 */
public class Person extends HashMap<String, String> {
    public static final String NAME_KEY = "NOMBRE";
    public static final String EMAIL_KEY = "E-MAIL";
    public static final String TYPE_KEY = "TIPO";
    public static final String LINE_KEY = "LINEAS";
    public static final String KEYWORDS_KEY = "KEYWORDS";

    public Person() {
        super();
    }

    public Person(HashMap<String, String> info) {
        super(info);
    }
}
