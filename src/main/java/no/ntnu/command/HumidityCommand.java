package no.ntnu.command;

public class HumidityCommand implements Command {

    @Override
    public String execute(String[] args) {
        return "Temperature";
    }
}