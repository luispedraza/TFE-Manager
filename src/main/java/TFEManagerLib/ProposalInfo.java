package TFEManagerLib;

import java.util.HashMap;

public class ProposalInfo extends HashMap<String, String> {
    public String toString(){
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
        return info;
    }
}
