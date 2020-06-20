package TFEManagerLib.Optimizers;

import TFEManagerLib.Models.Director;
import TFEManagerLib.Models.Student;
import io.jenetics.Genotype;
import io.jenetics.IntegerGene;
import io.jenetics.util.Factory;

import java.util.ArrayList;

public abstract class OptimizerDirectorForStudent {

    protected ArrayList<Director> DIRECTORS;
    protected ArrayList<Student> STUDENTS;
    protected final OptimizerConfiguration _CONFIG;

    // Inicialización de un optimizador de directores para alumnos
    public OptimizerDirectorForStudent(ArrayList<Student> students, ArrayList<Director> directors, OptimizerConfiguration config) {
        STUDENTS = students;
        DIRECTORS = directors;
        this._CONFIG = config;
    }

}
