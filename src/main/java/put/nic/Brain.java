package put.nic;

import java.util.Arrays;

public class Brain {
    double[][] neuronActivations;
    double[][][] connectionStrength;

    public Brain(Brain parentBrain) {
        neuronActivations = parentBrain.neuronActivations.clone();
        connectionStrength = parentBrain.connectionStrength.clone();
    }

    public Brain(int[] brainLayerSizes) {
        neuronActivations = new double[brainLayerSizes.length][0];
        connectionStrength = new double[brainLayerSizes.length][0][0];

        for (int brainLayer = 0; brainLayer < brainLayerSizes.length; brainLayer++) {
            neuronActivations[brainLayer] = new double[brainLayerSizes[brainLayer]];

            if (brainLayer < brainLayerSizes.length -1) { //Connections only apply when not the last layer
                connectionStrength[brainLayer] = new double[brainLayerSizes[brainLayer]][brainLayerSizes[brainLayer + 1]];

                for (int neuron = 0; neuron < brainLayerSizes[brainLayer]; neuron++) {
                    for (int destinationNeuron = 0; destinationNeuron < brainLayerSizes[brainLayer + 1]; destinationNeuron++) {
                        connectionStrength[brainLayer][neuron][destinationNeuron] = Math.random() * 2 - 1;
                    }
                }
            }
        }
    }

    private void clearActivations() {
        for (int brainLayer = 0; brainLayer < connectionStrength.length; brainLayer++) {
            Arrays.fill(neuronActivations[brainLayer], 0);
        }
    }

    public double[] propagateThought(double[] input) {
        clearActivations();

        neuronActivations[0] = input;

        for (int brainLayer = 0; brainLayer < connectionStrength.length - 1; brainLayer++) {
            for (int neuron = 0; neuron < connectionStrength[brainLayer].length; neuron++) {
                for (int connection = 0; connection < connectionStrength[brainLayer][neuron].length; connection++) {
                    neuronActivations[brainLayer + 1][connection] += neuronActivations[brainLayer][neuron] * connectionStrength[brainLayer][neuron][connection];
                }
            }
        }

        return neuronActivations[neuronActivations.length - 1];
    }

    public void mutate() {
        for (int brainLayer = 0; brainLayer < connectionStrength.length - 1; brainLayer++) {
            for (int neuron = 0; neuron < connectionStrength[brainLayer].length; neuron++) {
                for (int connection = 0; connection < connectionStrength[brainLayer][neuron].length; connection++) {
                    neuronActivations[brainLayer + 1][connection] += (Math.random() * 2) - 1;
                }
            }
        }
    }
}
