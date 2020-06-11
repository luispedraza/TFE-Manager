package TFEManagerLib;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class ProposalInfo extends HashMap<String, String> {
    public static final String ID_KEY = "ID";
    public static final String SURNAME_KEY = "APELLIDOS";
    public static final String NAME_KEY = "NOMBRE";
    public static final String COUNTRY_KEY = "PAIS";
    public static final String SUBMITTED_KEY = "ENTREGADO";
    public static final String TYPE_KEY = "TIPO";
    public static final String LINES_KEY = "LINEAS";
    public static final String TITLE_KEY = "TITULO";
    public static final String KEYWORDS_KEY = "KEYWORDS";
    public static final String REV1_KEY = "REVISOR1";
    public static final String REV2_KEY = "REVISOR2";
    public static final String OK1_KEY = "VEREDICTO1";
    public static final String OK2_KEY = "VEREDICTO2";
    public static final String OK_KEY = "OK";

    public static final ArrayList<String> FIELDS = new ArrayList<String>(
            Arrays.asList(
                    ID_KEY,
                    SURNAME_KEY,
                    NAME_KEY,
                    COUNTRY_KEY,
                    SUBMITTED_KEY,
                    TYPE_KEY,
                    LINES_KEY,
                    TITLE_KEY,
                    KEYWORDS_KEY,
                    REV1_KEY,
                    REV2_KEY,
                    OK1_KEY,
                    OK2_KEY,
                    OK_KEY
            )
    );

    /** Para inicializar el objeto a partir de un HashMap según se leen del excel
     *
     * @param p: HashMap con la información sacada de Excel
     */
    public ProposalInfo(HashMap<String, String> p) {
        super(p);
        // TODO: REvisar esto sobre el estado de entrega de una propuesta
        this.put("ENTREGADO", "NO");
    }

    public String getValue(String key) {
        switch (key) {
            case "APELLIDOS":
                return this.getSurname();
            case "NOMBRE":
                return this.getName();
            case "PAIS":
                return this.getCountry();
            case "TIPO":
                return this.getType();
            case "TITULO":
                return this.getTitle();
        }
        return " ";
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
        return this.getName() + ", " + this.getSurname();
    }

    public String getCountry() {
        return this.get(COUNTRY_KEY);
    }

    public String getTitle() {
        return this.get(TITLE_KEY);
    }

    public String getType() {
        return this.get(TYPE_KEY);
    }

    public String getReviever(int index) {
        String[] rev = {REV1_KEY, REV2_KEY};
        return this.get(rev[index-1]);
    }

    /**
     * Esto es así para la vista en el árbol
     * @return
     */
    public String toString() {
        return this.getFolderName();
    }

    public String toStringFull() {
        String info = "PROPUESTA DEL ALUMNO: " +
                this.getFullName() +
                System.lineSeparator();
        info += "=> Título de la propuesta: " +
                this.getTitle() +
                System.lineSeparator();
        info += "=> Tipo de trabajo: " +
                this.getType() +
                System.lineSeparator();
        info += "=> Primer revisor: " +
                this.get(REV1_KEY) +
                System.lineSeparator();
        info += "=> Segundo revisor: " +
                this.get(REV2_KEY) +
                System.lineSeparator();
        return info;
    }

    public String getFolderName() {
        return this.getFullName();
    }
}
