package no.ntnu.command;

import no.ntnu.greenhouse.Actuator;
import no.ntnu.greenhouse.GreenhouseSimulator;
import no.ntnu.greenhouse.SensorActuatorNode;
import no.ntnu.tools.getElementsFromString;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ChangeStateCommand implements Command {

    /*
    Command Example
    changeState, State (on, off), NodeID, SensorID
    change on 1 3

    also possible to exchange number with all the every found node / sensor recieves the action
     */
    @Override
    public String execute(String[] args) {
        GreenhouseSimulator gs = GreenhouseSimulator.getInstance(true);
        SensorActuatorNode node = null;
        Map<Integer, SensorActuatorNode> nodesMap = new HashMap<>();

        String[] commandParts = args[0].split(" ", 3);

        // stashing al nodes from the input "all" / node id a node map
        if (Objects.equals(commandParts[1], "all")){
            nodesMap.putAll(gs.getNodes());
        }
        else {
            try {
                nodesMap.put(1, getElementsFromString.getNodeFromString(commandParts[1], gs));
            }
            catch (java.lang.NullPointerException e){
                System.out.println();
            }
        }
        if (!nodesMap.isEmpty()) {
            for (Map.Entry<Integer, SensorActuatorNode> entry : nodesMap.entrySet()) {

                node = entry.getValue();
                Actuator actuator = getElementsFromString.getActuatorFromString(commandParts[2], node);

                boolean boolIsOn = Objects.equals(commandParts[0], "on");

                if (actuator != null) {
                    if (actuator.isOn() == boolIsOn) {
                        System.out.printf(" Actuator with ID %s, on Node %s, is already turned %s", commandParts[2], commandParts[1], commandParts[0]);
                    } else {
                        actuator.toggle();
                        System.out.printf(" Actuator with ID %s, on Node %s, is now turned %s!", commandParts[2], commandParts[1], commandParts[0]);
                    }
                }
            }
            return "Command succesfully executed!";
        }
        return "Command failed!";
    }
}
