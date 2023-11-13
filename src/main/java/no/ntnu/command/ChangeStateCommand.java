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
    changeState, State (on, off), NodeID, ActuatorID
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
        GreenhouseSimulator gs = new GreenhouseSimulator(true);
        Map<Integer, SensorActuatorNode> mapNodes = gs.getNodes();

        int nodeID = 0;
        int actuatorID = 0;
        boolean checkSuccesful = false;
        boolean nodeIDWorked = false;
        String isOn = args[1];
        boolean boolIsOn = Objects.equals(isOn, "on");
        try {
            nodeID = Integer.parseInt(args[3]);
            nodeIDWorked = true;
            actuatorID = Integer.parseInt(args[3]);
            checkSuccesful = true;

        } catch (java.lang.NumberFormatException e) {
            if (nodeIDWorked) System.out.printf("Identifier Actuator-ID: %s is not a number", args[3]);
            else System.out.printf("Identifier Node-ID: %s is not a number", args[2]);
        }
        if (checkSuccesful) {
            if(mapNodes.containsKey(nodeID)){
                SensorActuatorNode node = mapNodes.get(nodeID);
                Actuator act = node.getActuators().get(actuatorID);
                if (act.isOn() == boolIsOn) {
                    System.out.printf(" Actuator with ID %s, on Node %s, is already turned %s", args[3], args[2], args[1]);
                } else {
                    act.toggle();
                    System.out.printf(" Actuator with ID %s, on Node %s, is now turned %s!", args[3], args[2], args[1]);
                }
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