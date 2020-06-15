package TFEManagerLib.Models;

import com.google.common.base.Joiner;

import java.util.ArrayList;
import java.util.HashMap;

public class Student extends Person {
    public static final String FOLDER_NAME_KEY = "CARPETA ENTREGA";
    public static final String ID_KEY = "ID";

    public static final String SUBMITTED_KEY = "ENTREGADO";
    public static final String TYPE_KEY = "TIPO";

    public static final String TITLE_KEY = "TITULO";
    public static final String KEYWORDS_KEY = "KEYWORDS";
    public static final String REV1_KEY = "REVISOR1";
    public static final String REV2_KEY = "REVISOR2";
    public static final String OK1_KEY = "VEREDICTO1";
    public static final String OK2_KEY = "VEREDICTO2";
    public static final String OK_KEY = "OK";
    public static final String DIRECTOR_KEY = "DIRECTOR";

    public static final String FORMER_DIRECTOR = "DIRECTOR ANTERIOR";
    public static final String PROPOSAL_FILE_PATH = "LINK";

    public Student() {
        super();
    }
    /** Para inicializar el objeto a partir de un HashMap según se leen del excel
     *
     * @param p: HashMap con la información sacada de Excel
     */
    public Student(HashMap<String, String> p) {
        super(p);
        // TODO: Revisar esto sobre el estado de entrega de una propuesta entregada o no

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
        return this.getSurname() + ", " + this.getName();
    }


    public String getTitle() {
        return this.get(TITLE_KEY);
    }



    public String getRevieverName(int index) {
        String[] rev = {REV1_KEY, REV2_KEY};
        return this.get(rev[index]);
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
                this.get(TYPE_KEY) +
                System.lineSeparator();
        info += "=> Primer revisor: " +
                this.get(REV1_KEY) +
                System.lineSeparator();
        info += "=> Segundo revisor: " +
                this.get(REV2_KEY) +
                System.lineSeparator();
        return info;
    }

    public void setFormerDirector(String director) {
        this.put(FORMER_DIRECTOR, director);
    }

    public void setTitle(String title) {
        this.put(TITLE_KEY, title);
    }

    public void setCountry(String country) {
        this.put(COUNTRY_KEY, country);
    }

    public void setType(String type) {
        this.put(TYPE_KEY, type);
    }

    public void setID(String id) {
        this.put(ID_KEY, id);
    }

    public String getID() {
        return this.get(ID_KEY);
    }

    public void setLink(String url) {
        this.put(PROPOSAL_FILE_PATH, url);
    }

    public String getLink() {
        return this.get(PROPOSAL_FILE_PATH);
    }

    public void setDirector(String name) {
        this.put(DIRECTOR_KEY, name);
    }
    public String getDirector() {
        return this.get(DIRECTOR_KEY);
    }


    /** Guarda información del nombre de carpeta y también extrae ID, Apellidos y Nombre
     */
    public void setFolderName(String folderName) {
        this.put(FOLDER_NAME_KEY, folderName);
        String[] info = folderName.split("\\(");
        String[] fullName = info[0].split(",");
        this.setName(fullName[0].trim());
        this.setSurname(fullName[1].trim());
        this.setID(info[1].split("\\)")[0].trim());
    }

    /** El nombre de la carpeta de entregas del alumnos
     *
     * @return: Nombre de la carpeta, según la entrega de la propuesta
     */
    public String getFolderName() {
        return this.get(FOLDER_NAME_KEY);
    }

    /** Genera el nombre completo a partir de apellidos y nombre
     *
     */
    public void setFullName() {
        this.put(SURNAME_NAME_KEY, ", ".join(this.getSurname(), this.getName()));
    }
}
