package TFEManagerLib;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class ProposalInfo extends HashMap<String, String> {
    public static final ArrayList<String> FIELDS = new ArrayList<String>(
            Arrays.asList("ID",
                    "apellido",
                    "nombre",
                    "pais",
                    "titulo",
                    "tipo",
                    "revisor1",
                    "revisor2",
                    "veredicto1",
                    "veredicto2",
                    "veredictoglobal")
    );

    /** Para inicializar el objeto a partir de un HashMap según se leen del excel
     *
     * @param p: HashMap con la información sacada de Excel
     */
    public ProposalInfo(HashMap<String, String> p) {
        super(p);
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
