package no.ntnu.command;

/**
 * Returns the current date.
 */
public class DateCommand implements Command {
  @Override
  public String execute(String[] args) {
    return "Current date: " + java.time.LocalDate.now();
  }
}
