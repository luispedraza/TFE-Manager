package TFEManagerLib.Optimizers;

public class OptimizerConfiguration {
    String algorithm;
    public int WEIGHT_TYPE;
    public int WEIGHT_ZONE;
    public int WEIGHT_MAX;
    public int WEIGHT_LINES;
    public int WEIGHT_UNASSIGNED;

    public int MAX_ITERATIONS;

    public OptimizerConfiguration(String algorithm,
                                  int typeWeight,
                                  int zoneWeight,
                                  int maxWeight,
                                  int linesWeight,
                                  int unassignedWeight,
                                  int maxIterations
    ) {
        this.algorithm = algorithm;
        this.WEIGHT_TYPE = typeWeight;
        this.WEIGHT_ZONE = zoneWeight;
        this.WEIGHT_MAX = maxWeight;
        this.WEIGHT_LINES = linesWeight;
        this.WEIGHT_UNASSIGNED = unassignedWeight;
        this.MAX_ITERATIONS = maxIterations;

    }
}
