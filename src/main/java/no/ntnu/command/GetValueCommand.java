package no.ntnu.command;

import no.ntnu.greenhouse.*;
import no.ntnu.tools.getElementsFromString;

import java.util.Objects;

public class GetValueCommand implements Command {

    /*
    Command Example
    getValue, NodeID, SensorID
    value 2 3
     */
    @Override
    public String execute(String[] args) {
        String[] commandParts = args[0].split(" ", 2);
        GreenhouseSimulator gs = GreenhouseSimulator.getInstance(true);
        SensorActuatorNode node = getElementsFromString.getNodeFromString(commandParts[0], gs);

        if (node != null) {
            Sensor sensor = getElementsFromString.getSensorFromString(commandParts[1], node);

            if (sensor != null) {
                SensorReading reading = sensor.getReading();

                System.out.printf("Reading from Sensor ID %s on Node %s: %s %s",
                        commandParts[1], commandParts[0], sensor.getType(), reading.getValue());

            }
            return "Command succesfully executed!";
        }
        return "Command failed!";
    }
}
