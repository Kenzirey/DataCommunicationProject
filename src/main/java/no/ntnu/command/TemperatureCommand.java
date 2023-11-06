package no.ntnu.command;
import no.ntnu.greenhouse.Sensor;
public class TemperatureCommand implements Command {

    @Override
    public String execute(String[] args) {
        // either get a sensor as a parameter or create one
        // check for sensor type == temperature
        // get value
        return "Temperature";
    }
}
