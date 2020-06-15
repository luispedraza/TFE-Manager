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
    static int WEIGHT_MAX = 1;
    static int WEIGHT_LINES = 0;

    // Inicialización de un optimizador de directores para alumnos
    public Optimizer(ArrayList<Student> students, ArrayList<Director> directors) {
        Optimizer.STUDENTS = students;
        Optimizer.DIRECTORS = directors;
    }
//
//    // FUNCIÓN DE FITNESS
//    private static int eval(Genotype<BitGene> gt) {
//        return gt.chromosome()
//                .as(BitChromosome.class)
//                .bitCount();
//    }

    // Función de fitness para la asignación de Directores a alumnos:
//    private static int evalDirectorsForStudents(Genotype<BitGene> gt) {
//        int fitness = 0;
//
//        HashMap<Integer, Integer> directorsCount = new HashMap<>();
//        int studentIndex = 0;
//        for (Chromosome chromosome : gt) {
//
//
//            BitChromosome studentChromosome = (BitChromosome)chromosome;
//
//            // Miramos que cada alumno solo tenga un director asignado
//            fitness += (studentChromosome.bitCount() == 1) ? 1 : (-1);
//
//            studentChromosome.ones().forEach((directorIndex) -> {
//                // Aquí se puede comparar al director con el alumno para ver su match:
//                // País:
//                // Tipo:
//                // Líneas:
//                Integer count = directorsCount.get(directorIndex);
//                if (count!=null) {
//                    directorsCount.put(directorIndex,count+1);
//                } else {
//                    directorsCount.put(directorIndex,1);
//                }
//            });
//
//            studentIndex++;
//        }
//        // Miramos el número de trabajos asignados a cada director
//        for (Integer i : directorsCount.keySet()) {
//            // Penalizamos según cuánto nos alejamos del número de trabajos
//            fitness -= Math.abs(directorsCount.get(i) - Optimizer.DIRECTORS.get(i).getMaxNumberOfStudents());
//        }
//        return fitness;
//    }

//    // MÉTODO QUE LANZA LA OPTIMIZACIÓN
//    public static void optimDEMO() {
//        // FACTORÍA DE UN GENOTIPO APROPIADO PARA EL PROBLEMA
//        Factory<Genotype<BitGene>> gtf = Genotype.of(BitChromosome.of(18, 0.5));
//        // CREACIÓN DEL ENTORNO DE EJECUCIÓN
//        Engine<BitGene, Integer> engine = Engine
//                .builder(Optimizer::eval, gtf)
//                .build();
//        // ARRANQUE:
//        Genotype<BitGene> result = engine.stream()
//                .limit(100)
//
//                .collect(EvolutionResult.toBestGenotype());
//        // MOSTRAMOS EL RESULTADO:
//        System.out.println("RESULTADO DE LA OPTIMIZACIÓN: " + result);
//    }

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
            // COINCIDENCIA DE PAÍS
            if (student.getCountry().equals(director.getCountry())) fitness += WEIGHT_COUNTRY;
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
                .build();
        // ARRANQUE:
        Genotype<IntegerGene> result = engine.stream()
                .limit(1200)
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
//    /**
//     * Algoritmo genético para asignación de directores a estudiantes
//     * @param students: lista de estudiantes
//     * @param directors: lista de directores disponibles
//     * @return: lista de estudiantes con directores asginados
//     */
//    public static ArrayList<Student> optimDirectorsForStudents(ArrayList<Student> students, ArrayList<Director> directors) {
//        // FACTORÍA DE UN GENOTIPO APROPIADO PARA EL PROBLEMA
//        // Tantos comosomas como estudiantes
//        // Tantos genes como directores. Solo uno puede valer uno.
//        int nStudents = students.size();
//        int nDirectors = directors.size();
//        System.out.println(String.format("Número de alumnos: %d", nStudents));
//        System.out.println(String.format("Número de directores: %d", nDirectors));
//        ArrayList<BitChromosome> chromo = new ArrayList<>();
//        double probability = (float)1/nDirectors;
//        for (Student s : students) {
//            chromo.add(BitChromosome.of(nStudents, probability));
//        }
//        Factory<Genotype<BitGene>> gtf = Genotype.of(chromo);
//        // CREACIÓN DEL ENTORNO DE EJECUCIÓN
//        Engine<BitGene, Integer> engine = Engine
//                .builder(Optimizer::evalDirectorsForStudents, gtf)
//                .build();
//        // ARRANQUE:
//        Genotype<BitGene> result = engine.stream()
//                .limit(1500)
//                .peek( r -> {
//                    System.out.println(r.totalGenerations());
//                    System.out.println("Mejor individuo: " + r.bestFitness());
//                })
//                .collect(EvolutionResult.toBestGenotype());
//        // MOSTRAMOS EL RESULTADO:
//        System.out.println("RESULTADO DE LA OPTIMIZACIÓN: " + result);
//        int i = -1;
//        for (Chromosome chromosome : result) {
//            i++;
//            int j = -1;
//            for (BitGene gene : (BitChromosome)chromosome) {
//                j++;
//                if (gene.booleanValue()) {
//                    Optimizer.STUDENTS.get(j).setDirector(Optimizer.DIRECTORS.get(i).getName());
//                }
//            }
//        }
//
//        return Optimizer.STUDENTS;
//    }
}
