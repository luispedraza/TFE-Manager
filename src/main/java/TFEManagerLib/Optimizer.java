// https://sourceforge.net/projects/jenetics/files/
// https://github.com/jenetics/jenetics#evolving-images
// https://stackoverflow.com/questions/7205742/adding-points-to-xyseries-dynamically-with-jfreechart
// http://www.java2s.com/Code/Java/Chart/JFreeChartDynamicDataDemo.htm


package TFEManagerLib;

import TFEManagerLib.Models.Director;
import TFEManagerLib.Models.Reviewer;
import TFEManagerLib.Models.Student;
import io.jenetics.*;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.engine.Limits;
import io.jenetics.util.Factory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Consumer;

/**
 * Optimizador de asignaciones con algoritmo genético
 **/

public class Optimizer {
    private ArrayList<Director> DIRECTORS;
    private ArrayList<Student> STUDENTS;
    private ArrayList<Reviewer> REVIEWERS;
    int WEIGHT_ZONE = 0;
    int WEIGHT_TYPE = 0;
    int WEIGHT_MAX = 0;
    int WEIGHT_LINES = 0;

    int MAX_ITERATIONS = 100;

    public static class OptimizerConfiguration {
        String algorithm;
        int typeWeight;
        int zoneWeight;
        int maxDirector;
        int linesWeight;
        int maxIterations;

        public OptimizerConfiguration(String algorithm, int typeWeight, int zoneWeight, int maxWeight, int linesWeight, int maxIterations) {
            this.algorithm = algorithm;
            this.typeWeight = typeWeight;
            this.zoneWeight = zoneWeight;
            this.maxDirector = maxWeight;
            this.linesWeight = linesWeight;
            this.maxIterations = maxIterations;
        }

        public String getAlgorithm() {
            return algorithm;
        }

        public int getTypeWeight() {
            return typeWeight;
        }

        public int getZoneWeight() {
            return zoneWeight;
        }

        public int getLinesWeight() {
            return linesWeight;
        }

        public int getMaxDirector() {
            return maxDirector;
        }
    }

    private void update(EvolutionResult<IntegerGene, Integer> r) {
        System.out.println(r.totalGenerations());
        System.out.println("Mejor individuo: " + r.bestFitness());
    }

    // Inicialización de un optimizador de directores para alumnos
    public Optimizer(ArrayList<Student> students, ArrayList<Director> directors, OptimizerConfiguration config) {
        STUDENTS = students;
        DIRECTORS = directors;

        WEIGHT_ZONE = config.getZoneWeight();
        WEIGHT_TYPE = config.getTypeWeight();
        WEIGHT_MAX = config.getMaxDirector();
        WEIGHT_LINES = config.getLinesWeight();

        MAX_ITERATIONS = config.maxIterations;
    }

    private int evalDirectorsForStudents(Genotype<IntegerGene> gt) {
        int fitness = 0;
        HashMap<Integer, Integer> directorsCount = new HashMap<>();
        int studentIndex = 0;
        for (Chromosome chromosome : gt) {
            IntegerChromosome studentChromosome = (IntegerChromosome) chromosome;
            Integer directorIndex = studentChromosome.intValue();
            if (directorIndex == -1) continue; // NO ASIGNADO

            Student student = STUDENTS.get(studentIndex);
            Director director = DIRECTORS.get(directorIndex);
            // COINCIDENCIA DE zona geográfica (país, zona)
//            if (student.getCountry().equals(director.getCountry())) fitness += WEIGHT_COUNTRY;
            if (!student.getZone().equals(director.getZone())) fitness -= WEIGHT_ZONE;
            // Mismo tipo
            if (student.getType() != director.getType()) fitness -= WEIGHT_TYPE;
            Integer count = directorsCount.get(directorIndex);
            if (count == null) {
                directorsCount.put(directorIndex, 1);
            } else {
                directorsCount.put(directorIndex, count + 1);
            }
            studentIndex++;
        }

        // Miramos el número de trabajos asignados a cada director
        for (Integer i : directorsCount.keySet()) {
            // Penalizamos según cuánto nos pasamos del número de trabajos
            fitness -= WEIGHT_MAX * Math.max(directorsCount.get(i) - DIRECTORS.get(i).getMaxNumberOfStudents(), 0);
        }
        return fitness;
    }

    /**
     * Algoritmo genético para asignación de directores a estudiantes
     *
     * @param students:  lista de estudiantes
     * @param directors: lista de directores disponibles
     * @return: lista de estudiantes con directores asginados
     */
    public ArrayList<Student> optimDirectorsForStudents(
            ArrayList<Student> students,
            ArrayList<Director> directors,
            final Consumer<Integer> callbackUpdate,
            final Consumer<ArrayList<Student>> finalUpdate) {
//            final Consumer<EvolutionResult<IntegerGene, Integer>> callbackUpdate) {
//        final Consumer<EvolutionResult<IntegerGene, Integer>> myCallback = (callbackUpdate == null) ? this::update : callbackUpdate;

        // FACTORÍA DE UN GENOTIPO APROPIADO PARA EL PROBLEMA
        // Tantos comosomas como estudiantes
        // Tantos genes como directores. Solo uno puede valer uno.
        int nStudents = students.size();
        int nDirectors = directors.size();
        System.out.println(String.format("Número de alumnos: %d", nStudents));
        System.out.println(String.format("Número de directores: %d", nDirectors));
        ArrayList<IntegerChromosome> chromos = new ArrayList<>();
//        double probability = (float) 1 / nDirectors;
        for (Student s : students) {
            // -1 indica un director no asignado
            chromos.add(IntegerChromosome.of(-1, directors.size() - 1));
        }
        Factory<Genotype<IntegerGene>> gtf = Genotype.of(chromos);
        // CREACIÓN DEL ENTORNO DE EJECUCIÓN
        Engine<IntegerGene, Integer> engine = Engine
                .builder(this::evalDirectorsForStudents, gtf)
                .populationSize(students.size() * 3)
                .build();
        // ARRANQUE:

        final Thread thread = new Thread(() -> {
            Genotype<IntegerGene> result = engine.stream()
                    .limit(r -> r.bestFitness()!=0)
                    .limit(MAX_ITERATIONS)
//                .peek( r -> {
//                    System.out.println(r.totalGenerations());
//                    System.out.println("Mejor individuo: " + r.bestFitness());
//                })
                    .peek(r -> {
                        this.update(r);
                        if (callbackUpdate != null) {
                            callbackUpdate.accept(r.bestFitness());
                        }
                    })
                    .collect(EvolutionResult.toBestGenotype());
            // MOSTRAMOS EL RESULTADO:
            System.out.println("RESULTADO DE LA OPTIMIZACIÓN: " + result);
            int i = -1;
            for (Chromosome chromosome : result) {
                IntegerChromosome student = (IntegerChromosome) chromosome;
                Integer directorIndex = student.intValue();
                if (directorIndex == -1) continue;
                i++;
                STUDENTS.get(i).setDirector(DIRECTORS.get(directorIndex).getName());
                // enviamos de vuelta el resultado para que se guarde

            }
            finalUpdate.accept(STUDENTS);
        });
        thread.start();
//        this._thread = thread;
        // TODO: Arreglar esto después de usar threads
        return STUDENTS;
    }
}
