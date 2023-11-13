package no.ntnu.command;
import no.ntnu.controlpanel.FakeCommunicationChannel;
import no.ntnu.greenhouse.Actuator;
import no.ntnu.controlpanel.CommunicationChannel;
import no.ntnu.greenhouse.GreenhouseSimulator;
import no.ntnu.greenhouse.SensorActuatorNode;

import java.util.Map;
import java.util.Objects;

public class ChangeStateCommand implements Command {
    /*
    Command Example
    changeState, State (on, off), Type, ID
    changeState on fan 3
     */
    @Override
    public String execute(String[] args) {
        /*
           Check if Actuator with ID and Type exists
           Check if Actuator is already on that State
           Change State
           Return Succesful Change
         */
        // either creating the channel here or give it as parameter
        GreenhouseSimulator gs = new GreenhouseSimulator(true);
        Map<Integer, SensorActuatorNode> mapNodes = gs.getNodes();

        int actuatorId = 0;
        boolean checkSuccesful = false;
        String isOn = args[1];
        boolean boolIsOn = Objects.equals(isOn, "on");
        String type = args[2];
        try {
            actuatorId = Integer.parseInt(args[3]);
            checkSuccesful = true;

        } catch (java.lang.NumberFormatException e) {
            System.out.printf("Identifier ID: %s is not a number", args[3]);
        }
        if (checkSuccesful) {
            Actuator act = new Actuator(type, actuatorId);
            if (act.isOn() == boolIsOn) {
                System.out.printf(" Actuator with  ID: %s is already on the state %s", args[3], args[1]);
            } else {
                act.toggle();
                System.out.printf(" Actuator with  ID: %s is now turned %s!", args[3], args[1]);
            }
        }


        return "Current date: " + java.time.LocalDate.now();
    }


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