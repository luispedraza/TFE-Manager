package TFEManagerLib;

import TFEManagerLib.Models.Director;
import TFEManagerLib.Models.Reviewer;
import TFEManagerLib.Models.Student;
import io.jenetics.*;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.util.Factory;
import org.apache.commons.math3.genetics.GeneticAlgorithm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/** Optimizador de asignaciones con algoritmo genético **/

public class Optimizer {
    private static ArrayList<Director> DIRECTORS;
    private static ArrayList<Student> STUDENTS;
    private static ArrayList<Reviewer> REVIEWERS;
    static int WEIGHT_COUNTRY = 1;
    static int WEIGHT_TYPE = 1;
    static int WEIGHT_MAX = 2;
    static int WEIGHT_LINES = 0;

    // Inicialización de un optimizador de directores para alumnos
    public Optimizer(ArrayList<Student> students, ArrayList<Director> directors) {
        Optimizer.STUDENTS = students;
        Optimizer.DIRECTORS = directors;
    }

    private static int evalDirectorsForStudents(Genotype<IntegerGene> gt) {
        int fitness = 0;
        HashMap<Integer, Integer> directorsCount = new HashMap<>();
        int studentIndex = 0;
        for (Chromosome chromosome : gt) {
            IntegerChromosome studentChromosome = (IntegerChromosome)chromosome;
            Integer directorIndex = studentChromosome.intValue();
            if (directorIndex == -1) continue; // NO ASIGNADO

            Student student = Optimizer.STUDENTS.get(studentIndex);
            Director director = Optimizer.DIRECTORS.get(directorIndex);
            // COINCIDENCIA DE zona geográfica (país, zona)
//            if (student.getCountry().equals(director.getCountry())) fitness += WEIGHT_COUNTRY;
            if (student.getZone().equals(director.getZone())) fitness += WEIGHT_COUNTRY;
            // Mismo tipo
            if (student.getType() == director.getType()) fitness += WEIGHT_TYPE;
            Integer count = directorsCount.get(directorIndex);
            if (count == null) {
                directorsCount.put(directorIndex, 1);
            } else {
                directorsCount.put(directorIndex, count+1);
            }
            studentIndex++;
        }

        // Miramos el número de trabajos asignados a cada director
        for (Integer i : directorsCount.keySet()) {
            // Penalizamos según cuánto nos pasamos del número de trabajos
            fitness -= WEIGHT_MAX * Math.max(directorsCount.get(i) - Optimizer.DIRECTORS.get(i).getMaxNumberOfStudents(), 0);
        }
        return fitness;
    }
    /**
     * Algoritmo genético para asignación de directores a estudiantes
     * @param students: lista de estudiantes
     * @param directors: lista de directores disponibles
     * @return: lista de estudiantes con directores asginados
     */
    public static ArrayList<Student> optimDirectorsForStudents(ArrayList<Student> students, ArrayList<Director> directors) {
        // FACTORÍA DE UN GENOTIPO APROPIADO PARA EL PROBLEMA
        // Tantos comosomas como estudiantes
        // Tantos genes como directores. Solo uno puede valer uno.
        int nStudents = students.size();
        int nDirectors = directors.size();
        System.out.println(String.format("Número de alumnos: %d", nStudents));
        System.out.println(String.format("Número de directores: %d", nDirectors));
        ArrayList<IntegerChromosome> chromo = new ArrayList<>();
        double probability = (float)1/nDirectors;
        for (Student s : students) {
            // -1 indica un director no asignado
            chromo.add(IntegerChromosome.of(-1, directors.size()-1));
        }
        Factory<Genotype<IntegerGene>> gtf = Genotype.of(chromo);
        // CREACIÓN DEL ENTORNO DE EJECUCIÓN
        Engine<IntegerGene, Integer> engine = Engine
                .builder(Optimizer::evalDirectorsForStudents, gtf)
                .populationSize(students.size()*2)
                .build();
        // ARRANQUE:
        Genotype<IntegerGene> result = engine.stream()
                .limit(5000)
                .peek( r -> {
                    System.out.println(r.totalGenerations());
                    System.out.println("Mejor individuo: " + r.bestFitness());
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
            Optimizer.STUDENTS.get(i).setDirector(Optimizer.DIRECTORS.get(directorIndex).getName());
        }

        return Optimizer.STUDENTS;
    }
}
