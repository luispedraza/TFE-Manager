package TFEManagerLib;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class ProposalInfo extends HashMap<String, String> {
    public static final ArrayList<String> FIELDS = new ArrayList<String>(
            Arrays.asList(
                    "APELLIDOS",
                    "NOMBRE",
                    "PAIS",
                    "ENTREGADO",
                    "TIPO",
                    "LINEAS",
                    "TITULO",
                    "KEYWORDS",
                    "REVISOR1",
                    "REVISOR2",
                    "VEREDICTO1",
                    "VEREDICTO2",
                    "OK"
            )
    );

    /** Para inicializar el objeto a partir de un HashMap según se leen del excel
     *
     * @param p: HashMap con la información sacada de Excel
     */
    public ProposalInfo(HashMap<String, String> p) {
        super(p);
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
        return this.get("apellido");
    }

    public String getName() {
        return this.get("nombre");
    }

    public String getCountry() {
        return this.get("pais");
    }

    public String getTitle() {
        return this.get("titulo");
    }

    public String getType() {
        return this.get("tipo");
    }

    public String getReviever(int index) {
        String[] rev = {"revisor1", "revisor2"};
        return this.get(rev[index-1]);
    }

    public String toString() {
        return this.getFolderName();
    }
    public String toStringFull(){
        String info = "PROPUESTA DEL ALUMNO: " +
                this.get("apellido") +
                ", " +
                this.get("nombre") +
                System.lineSeparator();

        info += "=> Título de la propuesta: " +
                this.get("titulo") +
                System.lineSeparator();
        info += "=> Tipo de trabajo: " +
                this.get("tipo") +
                System.lineSeparator();
        info += "=> Primer revisor: " +
                this.get("revisor1") +
                System.lineSeparator();
        info += "=> Segundo revisor: " +
                this.get("revisor2") +
                System.lineSeparator();
        return info;
    }

    public String getFolderName() {
        return String.join(", ", this.get("apellido"), this.get("nombre"));
    }
}
