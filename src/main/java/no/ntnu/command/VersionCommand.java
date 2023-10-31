package no.ntnu.command;

/**
 * Returns the current version of the server.
 */
public class VersionCommand implements Command {
  @Override
  public String execute(String[] args) {
    return "Server_V0.1";
  }
}
