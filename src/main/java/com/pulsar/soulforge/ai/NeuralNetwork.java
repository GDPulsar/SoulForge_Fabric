package com.pulsar.soulforge.ai;

import com.pulsar.soulforge.SoulForge;

import java.util.Random;

public class NeuralNetwork {
    public int[] layerSizes;
    public double[][][] weights;
    public double[][] biases;
    public double[][] activations;
    public double[][] z;

    public NeuralNetwork(int[] layerSizes) {
        this.layerSizes = layerSizes;
        initializeNetwork();
    }

    public NeuralNetwork(int[] layerSizes, double[][][] weights, double[][] biases) {
        this.layerSizes = layerSizes;
        SoulForge.LOGGER.info("creating network with layer sizes: {}", layerSizes);
        this.weights = weights;
        this.biases = biases;
        SoulForge.LOGGER.info("weights: {}, biases: {}", weights, biases);
        this.activations = new double[layerSizes.length][];
        this.z = new double[layerSizes.length - 1][];
        for (int i = 0; i < layerSizes.length - 1; i++) {
            activations[i] = new double[layerSizes[i]];
            z[i] = new double[layerSizes[i + 1]];
        }
        this.activations[layerSizes.length - 1] = new double[layerSizes[layerSizes.length - 1]];
    }

    // Initialize weights and biases randomly
    private void initializeNetwork() {
        Random rand = new Random();

        weights = new double[layerSizes.length - 1][][];
        biases = new double[layerSizes.length - 1][];
        activations = new double[layerSizes.length][];
        z = new double[layerSizes.length - 1][];

        for (int i = 0; i < layerSizes.length - 1; i++) {
            weights[i] = new double[layerSizes[i + 1]][layerSizes[i]];
            biases[i] = new double[layerSizes[i + 1]];
            activations[i] = new double[layerSizes[i]];
            z[i] = new double[layerSizes[i + 1]];

            for (int j = 0; j < layerSizes[i + 1]; j++) {
                biases[i][j] = rand.nextGaussian();
                for (int k = 0; k < layerSizes[i]; k++) {
                    weights[i][j][k] = rand.nextGaussian();
                }
            }
        }
        activations[layerSizes.length - 1] = new double[layerSizes[layerSizes.length - 1]];
    }

    // Sigmoid activation function
    private double sigmoid(double x) {
        return 1.0 / (1.0 + Math.exp(-x));
    }

    // Derivative of the sigmoid function
    private double sigmoidDerivative(double x) {
        return sigmoid(x) * (1 - sigmoid(x));
    }

    // Feedforward pass
    public double[] feedforward(double[] input) {
        activations[0] = input;

        for (int i = 0; i < weights.length; i++) {
            for (int j = 0; j < weights[i].length; j++) {
                z[i][j] = biases[i][j];
                for (int k = 0; k < weights[i][j].length; k++) {
                    z[i][j] += weights[i][j][k] * activations[i][k];
                }
                activations[i + 1][j] = sigmoid(z[i][j]);
            }
        }

        return activations[activations.length - 1];
    }

    // Backpropagation
    public void backpropagate(double[] input, double[] target, double learningRate) {
        feedforward(input);

        double[][] delta = new double[layerSizes.length - 1][];
        for (int i = 0; i < delta.length; i++) {
            delta[i] = new double[layerSizes[i + 1]];
        }

        // Calculate delta for the output layer
        for (int i = 0; i < delta[delta.length - 1].length; i++) {
            double error = activations[activations.length - 1][i] - target[i];
            delta[delta.length - 1][i] = error * sigmoidDerivative(z[z.length - 1][i]);
        }

        // Calculate delta for the hidden layers
        for (int i = delta.length - 2; i >= 0; i--) {
            for (int j = 0; j < delta[i].length; j++) {
                double error = 0.0;
                for (int k = 0; k < delta[i + 1].length; k++) {
                    error += delta[i + 1][k] * weights[i + 1][k][j];
                }
                delta[i][j] = error * sigmoidDerivative(z[i][j]);
            }
        }

        // Update weights and biases
        for (int i = 0; i < weights.length; i++) {
            for (int j = 0; j < weights[i].length; j++) {
                for (int k = 0; k < weights[i][j].length; k++) {
                    weights[i][j][k] -= learningRate * delta[i][j] * activations[i][k];
                }
                biases[i][j] -= learningRate * delta[i][j];
            }
        }
    }
}
