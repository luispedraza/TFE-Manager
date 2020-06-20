package TFEManagerLib.Optimizers;

import TFEManagerLib.Models.Director;
import TFEManagerLib.Models.Student;
import io.jenetics.Genotype;
import io.jenetics.IntegerGene;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.util.Factory;

import java.util.ArrayList;
import java.util.function.Consumer;

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

    abstract Factory<Genotype<IntegerGene>> getGenotypeFactory();
    abstract int evalDirectorsForStudents(Genotype<IntegerGene> gt);
    abstract void generateSolution(Genotype<IntegerGene> result);

    /**
     * Algoritmo genético para asignación de directores a estudiantes
     * @param callbackUpdate : es llamado en cada iteración
     * @param finalUpdate : es llamado al final de la optimización
     * @return: lista de estudiantes con directores asginados
     */
    public void optimDirectorsForStudents(
            int POPULATION_SIZE,
            final Consumer<Integer> callbackUpdate,
            final Consumer<ArrayList<Student>> finalUpdate) {
        // CREACIÓN DEL ENTORNO DE EJECUCIÓN
        Engine<IntegerGene, Integer> engine = Engine
                .builder(this::evalDirectorsForStudents, getGenotypeFactory())
                .populationSize(POPULATION_SIZE)
                .build();
        // ARRANQUE:
        final Thread thread = new Thread(() -> {
            Genotype<IntegerGene> result = engine.stream()
//                    .limit(r -> r.bestFitness()!=0)
                    .limit(_CONFIG.MAX_ITERATIONS)
                    .peek(r -> {
                        System.out.println(r.totalGenerations());
                        System.out.println("Mejor individuo: " + r.bestFitness());
                        if (callbackUpdate != null) {
                            callbackUpdate.accept(r.bestFitness());
                        }
                    })
                    .collect(EvolutionResult.toBestGenotype());
            // MOSTRAMOS EL RESULTADO:
            System.out.println("RESULTADO DE LA OPTIMIZACIÓN: " + result);

            // Generación de la solución final
            generateSolution(result);
            // Enviamos de vuelta el resultado para que se guarde
            finalUpdate.accept(STUDENTS);
        });
        thread.start();
    }
}
