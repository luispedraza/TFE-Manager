package TFEManagerLib.Models;


import java.util.ArrayList;
import java.util.HashMap;

/**
 * Clase para almacenar la información de un revisor
 *
 */
public class Reviewer extends Academic {
    // Un revisor tiene propuestas
    private ArrayList<Student> proposals = new ArrayList<Student>();

    public Reviewer() {
        super();
    }
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


}
