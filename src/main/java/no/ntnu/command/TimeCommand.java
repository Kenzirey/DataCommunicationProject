package no.ntnu.command;

/**
 * Returns the current time.
 */
public class TimeCommand implements Command {

  @Override
  public String execute(String[] args) {
    return "Current time: " + java.time.LocalTime.now();
  }
}
