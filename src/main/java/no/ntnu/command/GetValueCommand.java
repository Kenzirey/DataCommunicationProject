package no.ntnu.command;

import no.ntnu.greenhouse.*;
import no.ntnu.tools.getElementsFromString;

import java.util.Objects;

public class GetValueCommand implements Command {

    /*
    Command Example
    getValue, NodeID, SensorID
    changeState on fan 3
     */
    @Override
    public String execute(String[] args) {
        GreenhouseSimulator gs = GreenhouseSimulator.getInstance(true);
        SensorActuatorNode node = getElementsFromString.getNodeFromString(args[2], gs);
        assert node != null;
        Sensor sensor = getElementsFromString.getSensorFromString(args[3], node);

        if (node != null && sensor != null) {
            SensorReading reading = sensor.getReading();

            System.out.printf("Reading from Sensor ID %s on Node %s: %s %s",
                    args[2], args[1], sensor.getType(), sensor.getReading().getValue());

        }

        return "Current date: " + java.time.LocalDate.now();
    }

    // Just a tester not needed in the real application
    public static void main(String[] args) {
        ChangeStateCommand c = new ChangeStateCommand();
        args = new String[4];
        args[0] = "change";
        args[1] = "on";
        args[2] = "1";
        args[3] = "3";
        c.execute(args);
    }
}
