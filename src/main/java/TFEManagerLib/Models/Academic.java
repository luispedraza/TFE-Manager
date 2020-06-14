package TFEManagerLib.Models;

import java.util.HashMap;

public class Academic extends Person {
    public static final String MAX_NUMBER_KEY = "MAXIMO";           // Máximo de trabajos asignados
    public static final String ASSIGNED_NUMBER_KEY = "ASIGNADOS";   // Número de trabajos asignados

    int max = 0;

    public Academic() {
        super();
    }

    @Override
    public String put(String key, String value) {
        if (key.equals(MAX_NUMBER_KEY)) {
            setMaxNumberOfStudents(Integer.valueOf(value));
        }
        return super.put(key, value);
    }

    public Academic(String name, int max, String email) {
        this.put(NAME_KEY, name);   // Nombre del revisor
        this.put(MAX_NUMBER_KEY, Integer.toString(max));       // Máximo de propuestas
        this.put(EMAIL_KEY, email);
    }
    public Academic(HashMap<String, String> info) {
        super(info);
    }

    public String getName() {
        return this.get(NAME_KEY);
    }
    public void setName(String name) {
        this.put(NAME_KEY, name);
    }

    public int getMaxNumberOfStudents() {
        return this.max;
    }
    public void setMaxNumberOfStudents(int n) {
        this.max = n;
    }

    public String getEmail() {
        return this.get(EMAIL_KEY);
    }
    public void setEmail(String email) {
        this.put(EMAIL_KEY, email);
    }
}
