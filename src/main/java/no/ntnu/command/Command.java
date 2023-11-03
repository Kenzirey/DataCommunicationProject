package no.ntnu.command;

/**
 * Implements the Command pattern.
 * For commands in the DatagramHandler.
 */
public interface Command {
  //TODO: add command to shut down server.
  //TODO: add command to disconnect client from server.
  String execute(String[] args);
}
