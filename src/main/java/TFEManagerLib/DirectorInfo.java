package TFEManagerLib;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/** Clase para almacenar la información de un Director
 *
 */
public class DirectorInfo extends HashMap<String, String> {
    public static final ArrayList<String> FIELDS = new ArrayList<String>(
            Arrays.asList("ID",
                    "NOMBRE",
                    "E-MAIL",
                    "MAXIMO")
    );
}
