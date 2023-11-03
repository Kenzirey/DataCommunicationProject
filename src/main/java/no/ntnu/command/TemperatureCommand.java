package no.ntnu.command;

public class TemperatureCommand implements Command {

    @Override
    public String execute(String[] args) {
        return "Temperature";
    }
}
