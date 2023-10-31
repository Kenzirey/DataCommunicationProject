package no.ntnu.command;

/**
 * Returns the name of the server.
 */
public class NameCommand implements Command {
  @Override
  public String execute(String[] args) {
    return "UDP Server";
  }
}
