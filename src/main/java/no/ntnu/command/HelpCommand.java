package no.ntnu.command;

public class HelpCommand implements Command {

    @Override
    public String execute(String[] args) {
        if (args.length == 0){
         return"Possible commands are: version, echo, name, date, time, change value";
    }
        return "not implemented yet";
    }
}