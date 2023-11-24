package no.ntnu.tools;
import no.ntnu.greenhouse.*;
import no.ntnu.greenhouse.SensorActuatorNode;

import java.util.Map;

public class getElementsFromString {
    public static SensorActuatorNode getNodeFromString(String nodeIdString, GreenhouseSimulator simulator) {
        try {
            int nodeId = Integer.parseInt(nodeIdString);
            Map<Integer, SensorActuatorNode> nodes = simulator.getNodes();
            return nodes.get(nodeId);
        } catch (NumberFormatException e) {
            System.out.printf("Identifier Node-ID: %s is not a number", nodeIdString);
            return null;
        }
    }

    public static Actuator getActuatorFromString(String actuatorIdString, SensorActuatorNode node) {
        try {
            int actuatorId = Integer.parseInt(actuatorIdString);
            return node.getActuators().get(actuatorId);
        } catch (NumberFormatException e) {
            System.out.printf("Identifier Actuator-ID: %s is not a number", actuatorIdString);
            return null;
        }
    }
    public static Sensor getSensorFromString(String sensorIdString, SensorActuatorNode node) {
        try {
            int sensorID = Integer.parseInt(sensorIdString);
            return node.getSensors().get(sensorID);
        } catch (NumberFormatException e) {
            System.out.printf("Identifier Sensor-ID: %s is not a number", sensorIdString);
            return null;
        }
    }
}
