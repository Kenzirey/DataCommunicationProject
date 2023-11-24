package no.ntnu.command;

import no.ntnu.greenhouse.Actuator;
import no.ntnu.greenhouse.GreenhouseSimulator;
import no.ntnu.greenhouse.SensorActuatorNode;
import no.ntnu.tools.getElementsFromString;

import java.util.Objects;

public class ChangeStateCommand implements Command {

    /*
    Command Example
    changeState, State (on, off), NodeID, SensorID
    changeState on fan 3
     */
    @Override
    public String execute(String[] args) {
        GreenhouseSimulator gs = GreenhouseSimulator.getInstance(true);
        SensorActuatorNode node = getElementsFromString.getNodeFromString(args[2], gs);
        assert node != null;
        Actuator actuator = getElementsFromString.getActuatorFromString(args[3], node);

        boolean boolIsOn = Objects.equals(args[1], "on");

        if (node != null && actuator != null) {
            if (actuator.isOn() == boolIsOn) {
                System.out.printf(" Actuator with ID %s, on Node %s, is already turned %s", args[3], args[2], args[1]);
            } else {
                actuator.toggle();
                System.out.printf(" Actuator with ID %s, on Node %s, is now turned %s!", args[3], args[2], args[1]);
            }
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
