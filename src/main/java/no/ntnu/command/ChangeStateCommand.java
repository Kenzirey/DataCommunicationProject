package no.ntnu.command;
import no.ntnu.greenhouse.Actuator;

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
        // TODO: 06.11.2023 replace the created actuator with an actually one
        int actuatorId = 0;
        boolean checkSuccesful = false;
        String isOn = args[1];
        boolean boolIsOn = Objects.equals(isOn, "on");
        String type = args[2];
        try {
            actuatorId = Integer.parseInt(args[3]);
            checkSuccesful = true;
            
        }
        catch (java.lang.NumberFormatException e){
            System.out.printf("Identifier ID: %s is not a number", args[3]);
        }
        if (checkSuccesful){
            Actuator act = new Actuator(type, actuatorId);
            if (act.isOn() == boolIsOn){
                System.out.printf(" Actuator with  ID: %s is already on the state %s", args[3], args[1]);
            }
            else {
                act.toggle();
                System.out.printf(" Actuator with  ID: %s is now turned %s!", args[3], args[1]);
            }
        }
        

        return "Current date: " + java.time.LocalDate.now();
    }
}
