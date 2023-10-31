package no.ntnu.command;

/**
 * For testing if the server responds as it should, by using a ping.
 */
public class PingCommand implements Command {

  @Override
  public String execute(String[] args) {
    return "pong";
  }
}
