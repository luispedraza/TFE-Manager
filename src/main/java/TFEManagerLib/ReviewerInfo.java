package TFEManagerLib;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Clase para almacenar la información de un revisor
 *
 */
public class ReviewerInfo extends HashMap<String, String> {
    private ArrayList<ProposalInfo> proposals;
    public static final ArrayList<String> FIELDS = new ArrayList<String>(
            Arrays.asList("ID",
                    "NOMBRE",
                    "E-MAIL",
                    "MAXIMO")
    );

    public ReviewerInfo(String name) {
        this.put("NOMBRE", name);
        this.proposals = new ArrayList<ProposalInfo>();
    }

    public String toString() {
        String info = "INFORMACIÓN DEL REVISOR " + this.get("NOMBRE") + System.lineSeparator();
        info += String.format(" Tiene asignadas %d propuestas", proposals.size());
        info += System.lineSeparator();
        return info;
    };

    /** Para añadir una nueva propuesta al revisor
     *
     * @param p: Propuesta que añadimos
     */
    public void addProposal(ProposalInfo p) {
        proposals.add(p);
    }

    public ArrayList<ProposalInfo> getProposals() {
        return this.proposals;
    }

    public String getName() {
        return this.get("NOMBRE");
    }

}
