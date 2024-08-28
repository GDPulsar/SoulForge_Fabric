package com.pulsar.soulforge.ai;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.pulsar.soulforge.SoulForge;

import java.io.*;

public class NetworkSerializer {
    public static void writeNetworkToFile(NeuralNetwork network, String path) {
        try (FileOutputStream stream = new FileOutputStream(path)) {
            stream.write(network.layerSizes.length);
            for (int i = 0; i < network.layerSizes.length; i++) {
                stream.write(network.layerSizes[i]);
            }
            stream.write(network.weights.length);
            for (int i = 0; i < network.weights.length; i++) {
                stream.write(network.weights[i].length);
                for (int j = 0; j < network.weights[i].length; j++) {
                    stream.write(network.weights[i][j].length);
                    for (int k = 0; k < network.weights[i][j].length; k++) {
                        for (int l = 0; l < 8; l++) {
                            stream.write((byte)((Double.doubleToLongBits(network.weights[i][j][k]) >> ((7 - i) * 8)) & 0xFF));
                        }
                    }
                }
            }
            stream.write(network.biases.length);
            for (int i = 0; i < network.biases.length; i++) {
                stream.write(network.biases[i].length);
                for (int j = 0; j < network.biases[i].length; j++) {
                    for (int l = 0; l < 8; l++) {
                        stream.write((byte)((Double.doubleToLongBits(network.biases[i][j]) >> ((7 - i) * 8)) & 0xFF));
                    }
                }
            }
        } catch (FileNotFoundException e) {
            SoulForge.LOGGER.warn("Could not find file while writing network data!", e);
        } catch (IOException e) {
            SoulForge.LOGGER.warn("Invalid file located while writing network data!", e);
        }
    }

    public static NeuralNetwork readNetworkFromFile(String path) {
        try (FileInputStream stream = new FileInputStream(path)) {
            int layerCount = stream.read();
            int[] layerSizes = new int[layerCount];
            for (int i = 0; i < layerCount; i++) {
                layerSizes[i] = stream.read();
            }
            int weightSize1 = stream.read();
            double[][][] weights = new double[weightSize1][][];
            for (int i = 0; i < weightSize1; i++) {
                int weightSize2 = stream.read();
                weights[i] = new double[weightSize2][];
                for (int j = 0; j < weightSize2; j++) {
                    int weightSize3 = stream.read();
                    weights[i][j] = new double[weightSize3];
                    for (int k = 0; k < weightSize3; k++) {
                        byte[] bytes = stream.readNBytes(8);
                        long lngVal = 0;
                        for (int l = 0; l < 8; l++) {
                            lngVal += (long)bytes[l] << ((7 - l) * 8);
                        }
                        double value = Double.longBitsToDouble(lngVal);
                        weights[i][j][k] = value;
                    }
                }
            }
            int biasesSize1 = stream.read();
            double[][] biases = new double[biasesSize1][];
            for (int i = 0; i < biasesSize1; i++) {
                int biasesSize2 = stream.read();
                biases[i] = new double[biasesSize2];
                for (int j = 0; j < biasesSize2; j++) {
                    byte[] bytes = stream.readNBytes(8);
                    long lngVal = 0;
                    for (int k = 0; k < 8; k++) {
                        lngVal += (long)bytes[k] << ((7 - k) * 8);
                    }
                    double value = Double.longBitsToDouble(lngVal);
                    biases[i][j] = value;
                }
            }
            return new NeuralNetwork(layerSizes, weights, biases);
        } catch (IOException e) {
            SoulForge.LOGGER.warn("Error while reading network data!", e);
        }
        return null;
    }

    public static void writeTrainingData(String path, double[] inputs, double[] expected) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonObject trainingDataJson = new JsonObject();
        try (FileReader reader = new FileReader(path)) {
            trainingDataJson = gson.fromJson(reader, JsonObject.class);
            JsonArray inputArray = new JsonArray();
            for (double input : inputs) {
                inputArray.add(input);
            }
            JsonArray expectedArray = new JsonArray();
            for (double value : expected) {
                expectedArray.add(value);
            }
            JsonObject json = new JsonObject();
            json.add("inputs", inputArray);
            json.add("expected", expectedArray);
            trainingDataJson.getAsJsonArray("datasets").add(json);
        } catch (IOException e) {
            SoulForge.LOGGER.warn("Error while reading training data!", e);
        }

        try (FileWriter writer = new FileWriter(path)) {
            gson.toJson(trainingDataJson, writer);
        } catch (IOException e) {
            SoulForge.LOGGER.warn("Error while writing training data!", e);
        }
    }
}
