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
import java.util.stream.Collectors;

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
            if (directorIndex >= 0) {
                Student student = STUDENTS.get(studentIndex);
                Director director = DIRECTORS.get(directorIndex);
                fitness += student.match(director, _CONFIG.WEIGHT_ZONE, _CONFIG.WEIGHT_TYPE);
            }
        }
        // Restricción del número de trabajos */
        int loadPenalty = gt.stream()
                .map( c -> (IntegerChromosome) c)
                .collect(Collectors.groupingBy(
                        IntegerChromosome::intValue,
                        Collectors.summingInt(x -> 1)))
                .entrySet()
                .stream()
                .map((item) -> (item.getKey() == -1) ? (item.getValue() * _CONFIG.WEIGHT_UNASSIGNED) : _CONFIG.WEIGHT_MAX * Math.max(item.getValue() - DIRECTORS.get(item.getKey()).getMaxNumberOfStudents(), 0))
                .reduce(0, (a, b) -> a + b);

        return fitness - loadPenalty;
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
