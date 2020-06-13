package TFEManagerLib;

import java.util.ArrayList;
import java.util.HashMap;

public class Academic extends Person {
    public static final String MAX_NUMBER_KEY = "MAXIMO";
    public static final String ASSIGNED_NUMBER_KEY = "ASIGNADOS";

    public Academic(String name, int max, String email) {
        this.put(NAME_KEY, name);   // Nombre del revisor
        this.put(MAX_NUMBER_KEY, Integer.toString(max));       // Máximo de propuestas
        this.put(EMAIL_KEY, email);
    }
    public Academic(HashMap<String, String> info) {
        super(info);
    }
}
