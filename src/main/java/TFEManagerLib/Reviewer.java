package TFEManagerLib;


import java.util.ArrayList;
import java.util.HashMap;

/**
 * Clase para almacenar la información de un revisor
 *
 */
public class Reviewer extends Person {
    // Un revisor tiene propuestas
    private ArrayList<Student> proposals = new ArrayList<Student>();

    /**
     * Para crear copias a partir de un HashMap
     * @param r
     */
    public Reviewer(HashMap<String, String> r) {
        super(r);
        this.proposals = new ArrayList<Student>();
    }

    public String toString() {
        String info = "INFORMACIÓN DEL REVISOR " + this.getName() + System.lineSeparator();
        info += String.format("==> Tiene asignadas %d propuestas", proposals.size());
        info += System.lineSeparator();
        return info;
    };

    /** Para añadir una nueva propuesta al revisor
     *
     * @param p: Propuesta que añadimos
     */
    public void addProposal(Student p) {
        proposals.add(p);
    }

    public ArrayList<Student> getProposals() {
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
