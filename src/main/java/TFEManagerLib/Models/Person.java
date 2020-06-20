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
    public static final String SURNAME_KEY = "APELLIDOS";
    public static final String SURNAME_NAME_KEY = "APELLIDOS, NOMBRE";
    public static final String EMAIL_KEY = "E-MAIL";
    public static final String TYPE_KEY = "TIPO";
    public static final String LINES_KEY = "LINEAS";
    public static final String KEYWORDS_KEY = "KEYWORDS";

    public static final String COUNTRY_KEY = "PAIS";
    public static final String ZONE_KEY = "ZONA";

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

        return super.put(key, value);
    }

    public void setType(String type) {
        this.put(TYPE_KEY, type);
    }
    public String getType() {
        return this.get(TYPE_KEY);
    }

    public String getSurname() {
        return this.get(SURNAME_KEY);
    }
    public void setSurname(String surname) {
        this.put(SURNAME_KEY, surname);
    }

    public String getName() {
        return this.get(NAME_KEY);
    }
    public void setName(String name) {
        this.put(NAME_KEY, name);
    }

    /**
     * Nombre completo del alumno
     * @return: nombre completo en la forma "APELLIDOS, NOMBRE".
     */
    public String getFullName() {
        return this.get(SURNAME_NAME_KEY);
    }

    /** Genera el nombre completo a partir de apellidos y nombre
     *
     */
    public void setFullName() {
        this.put(SURNAME_NAME_KEY, ", ".join(this.getSurname(), this.getName()));
    }

    public void setFullName(String surname, String name) {
        setName(name);
        setSurname(surname);
        this.put(SURNAME_NAME_KEY, String.join(", ", surname, name));
    }

    public void setFullName(String fullName) {
        this.put(SURNAME_NAME_KEY, fullName);
        String[] info = fullName.split(", ");
        setName(info[1].trim());
        setSurname(info[0].trim());
    }

    public void setCountry(String country) {
        this.put(COUNTRY_KEY, country.toUpperCase());
    }
    public String getCountry() {
        return this.get(COUNTRY_KEY);
    }

    /** La zona geográfica donde está la persona
     *
     * @return
     */
    public String getZone() {
        return this.get(ZONE_KEY);
    }
    public String setZone(String zone) {
        return this.get(ZONE_KEY);
    }

    public void setLines(ArrayList<Integer> lines) {
        this.lines = lines.toArray(new Integer[0]);
        this.put(LINES_KEY, Joiner.on(";").join(this.lines));
    }





}
