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

public class OptimizerDirectorForStudentA extends OptimizerDirectorForStudent {
    public OptimizerDirectorForStudentA(ArrayList<Student> students, ArrayList<Director> directors, OptimizerConfiguration config) {
        super(students, directors, config);
    }

    @Override
    Factory<Genotype<IntegerGene>> getGenotypeFactory() {
        // Tantos comosomas como estudiantes
        // Tantos genes como directores. Solo uno puede valer uno.
        ArrayList<IntegerChromosome> chromosomes = new ArrayList<>();
        for (Student s : STUDENTS) {
            chromosomes.add(IntegerChromosome.of(-1, DIRECTORS.size() - 1)); // -1 indica un director no asignado
        }
        return Genotype.of(chromosomes);
    }

    @Override
    int evalDirectorsForStudents(Genotype<IntegerGene> gt) {
        int fitness = 0;
        HashMap<Integer, Integer> directorsCount = new HashMap<>();
        int studentIndex = -1;
        for (Chromosome chromosome : gt) {
            studentIndex++;
            IntegerChromosome studentChromosome = (IntegerChromosome) chromosome;
            Integer directorIndex = studentChromosome.intValue();
            if (directorIndex == -1) {
                fitness -= _CONFIG.WEIGHT_UNASSIGNED;
                continue; // NO ASIGNADO
            }
            Student student = STUDENTS.get(studentIndex);
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

    @Override
    void generateSolution(Genotype<IntegerGene> result) {
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
    }
}
