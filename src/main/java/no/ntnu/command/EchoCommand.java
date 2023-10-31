package no.ntnu.command;

/**
 * Echoes provided text.
 */
public class EchoCommand implements Command {
  @Override
  public String execute(String[] args) {
    if (args.length >= 1) {
      return args[0];
    } else {
      return "No text to echo";
    }
  }
}
