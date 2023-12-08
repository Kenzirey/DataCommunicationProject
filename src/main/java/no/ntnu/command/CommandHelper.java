package no.ntnu.command;

public class CommandHelper {

    // simple helper which uses a switch to give help for specific commands
    public static String getCommandHelp(String command) {
        return switch (command.toLowerCase()) {
            case "version" -> "Returns the version of the software. Example command: version";
            case "name" -> "Returns the name of the application. Example command: name";
            case "date" -> "Returns the current date. Example command: date";
            case "time" -> "Returns the current time. Example command: time";
            case "change" -> """
                    Changes the state of a node's sensor. Example command: change on 1 3
                    Usage: change <State (on/off)> <NodeID> <SensorID>
                     - State: The new state for the sensor, either 'on' or 'off'.
                     - NodeID: The identifier of the node where the sensor is located.
                     - SensorID: The identifier of the sensor.""";
            case "value" -> """
                    Gets the value of a node's sensor. Example command: value 2 3
                    Usage: value <NodeID> <SensorID>
                     - NodeID: The identifier of the node where the sensor is located.
                     - SensorID: The identifier of the sensor.""";
            default -> "Unknown command. Possible commands are: version, echo, name, date, time, change, value";
        };
    }
}
