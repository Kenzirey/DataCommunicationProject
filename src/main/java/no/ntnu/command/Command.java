package no.ntnu.command;

/**
 * Implements the Command pattern.
 * For commands in the DatagramHandler.
 */
public interface Command {
  String execute(String[] args);
}
