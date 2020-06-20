// https://sourceforge.net/projects/jenetics/files/
// https://github.com/jenetics/jenetics#evolving-images
// https://stackoverflow.com/questions/7205742/adding-points-to-xyseries-dynamically-with-jfreechart
// http://www.java2s.com/Code/Java/Chart/JFreeChartDynamicDataDemo.htm


package TFEManagerLib.Optimizers;

import TFEManagerLib.Models.Director;
import TFEManagerLib.Models.Student;
import io.jenetics.*;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.util.Factory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Consumer;

/**
 * Optimizador de asignaciones con algoritmo genético
 **/

public class OptimizerDirectorForStudentB extends OptimizerDirectorForStudent {
    public OptimizerDirectorForStudentB(ArrayList<Student> students, ArrayList<Director> directors, OptimizerConfiguration config) {
        super(students, directors, config);
    }


    private int evalDirectorsForStudents(Genotype<IntegerGene> gt) {
        int fitness = 0;
        HashMap<Integer, Integer> directorsCount = new HashMap<>();
        int studentIndex = -1;
        for (Chromosome chromosome : gt) {
            studentIndex++;
            Student student = STUDENTS.get(studentIndex);
            IntegerChromosome studentChromosome = (IntegerChromosome) chromosome;
            Integer directorIndex = studentChromosome.intValue();
            if (directorIndex == -1) {
                fitness -= _CONFIG.WEIGHT_UNASSIGNED;
                continue; // NO ASIGNADO
            }

            Director director = DIRECTORS.get(directorIndex);
            fitness += student.match(director, _CONFIG.WEIGHT_ZONE, _CONFIG.WEIGHT_TYPE);
            Integer count = directorsCount.get(directorIndex);
            if (count == null) {
                directorsCount.put(directorIndex, 1);
            } else {
                directorsCount.put(directorIndex, count + 1);
            }
        }

        // Miramos el número de trabajos asignados a cada director
        for (Integer i : directorsCount.keySet()) {
            // Penalizamos según cuánto nos pasamos del número de trabajos
            fitness -= _CONFIG.WEIGHT_MAX * Math.max(directorsCount.get(i) - DIRECTORS.get(i).getMaxNumberOfStudents(), 0);
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
    public void optimDirectorsForStudents(
            int POPULATION_SIZE,
            final Consumer<Integer> callbackUpdate,
            final Consumer<ArrayList<Student>> finalUpdate) {
        // FACTORÍA DE UN GENOTIPO APROPIADO PARA EL PROBLEMA
        ArrayList<IntegerChromosome> chromos = new ArrayList<>();
        for (Student s : STUDENTS) {
            chromos.add(IntegerChromosome.of(-1, DIRECTORS.size() - 1)); // -1 indica un director no asignado
        }
        Factory<Genotype<IntegerGene>> gtf = Genotype.of(chromos);
        // CREACIÓN DEL ENTORNO DE EJECUCIÓN
        Engine<IntegerGene, Integer> engine = Engine
                .builder(this::evalDirectorsForStudents, gtf)
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
            int i = -1;
            for (Chromosome chromosome : result) {
                IntegerChromosome studentChromosome = (IntegerChromosome) chromosome;
                Integer directorIndex = studentChromosome.intValue(); // El director asignados
                if (directorIndex == -1) continue;
                i++;
                Student student = STUDENTS.get(i);
                Director director = DIRECTORS.get(directorIndex);
                director.addStudent(student);
                student.setDirector(director);
            }
            // Enviamos de vuelta el resultado para que se guarde
            finalUpdate.accept(STUDENTS);
        });
        thread.start();
    }
}
