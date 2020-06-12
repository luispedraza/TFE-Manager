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

    public static final String NAME_KEY = "NOMBRE";
    public static final String EMAIL_KEY = "E-MAIL";
    public static final String TYPE_KEY = "TIPO";
    public static final String LINE_KEY = "LINEAS";
    public static final String KEYWORDS_KEY = "KEYWORDS";
    public static final String MAX_NUMBER_KEY = "MAXIMO";
    public static final String ASSIGNED_NUMBER_KEY = "ASIGNADOS";

    public static final ArrayList<String> FIELDS = new ArrayList<>(
            Arrays.asList(
                    NAME_KEY,
                    EMAIL_KEY,
                    TYPE_KEY,
                    LINE_KEY,
                    KEYWORDS_KEY,
                    MAX_NUMBER_KEY,
                    ASSIGNED_NUMBER_KEY)
    );

    public ReviewerInfo(String name, int max, String email) {
        this.put(NAME_KEY, name);   // Nombre del revisor
        this.put(MAX_NUMBER_KEY, Integer.toString(max));       // Máximo de propuestas
        this.put(EMAIL_KEY, email);
        this.proposals = new ArrayList<ProposalInfo>();
    }

    /**
     * Para crear copias a partir de un HashMap
     * @param r
     */
    public ReviewerInfo(HashMap<String, String> r) {
        super(r);
        this.proposals = new ArrayList<ProposalInfo>();
    }

    public String toString() {
        String info = "INFORMACIÓN DEL REVISOR " + this.get("NOMBRE") + System.lineSeparator();
        info += String.format("==> Tiene asignadas %d propuestas", proposals.size());
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
        return this.get(NAME_KEY);
    }
    public void setName(String name) {
        this.put(NAME_KEY, name);
    }

    public String getEmail() {
        return this.get(EMAIL_KEY);
    }
    public void setEmail(String email) {
        this.put(EMAIL_KEY, email);
    }
}
