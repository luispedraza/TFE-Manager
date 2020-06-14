package TFEManagerLib.Models;


import com.google.common.base.Joiner;

import java.util.ArrayList;
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
    public static final String LINES_KEY = "LINEAS";
    public static final String KEYWORDS_KEY = "KEYWORDS";
    public static final String SURNAME_KEY = "APELLIDOS";
    public static final String COUNTRY_KEY = "PAIS";

    // Para los métodos de optimización es mejor guardar tipo y líneas como enteros
    private Integer[] lines = {};
    private Integer type = 0;

    public Person() {
        super();
    }

    public Person(HashMap<String, String> info) {
        super(info);
    }

    @Override
    public String put(String key, String value) {
        if (key.equals(TYPE_KEY)) {
            type = Integer.valueOf(value);
        } else if (key.equals(LINES_KEY)) {

        }
        return super.put(key, value);
    }

    public void setLines(ArrayList<Integer> lines) {
        this.lines = lines.toArray(new Integer[0]);
        this.put(LINES_KEY, Joiner.on(";").join(this.lines));
    }

    public int getType() {
        return this.type;
    }


    public String getCountry() {
        return this.get(COUNTRY_KEY);
    }
}
