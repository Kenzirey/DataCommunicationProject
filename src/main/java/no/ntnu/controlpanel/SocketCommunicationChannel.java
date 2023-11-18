package no.ntnu.controlpanel;

import java.io.BufferedReader;
import java.io.ObjectOutputStream;
import java.net.Socket;



public class SocketCommunicationChannel implements CommunicationChannel {
  private final ControlPanelLogic logic;
  private final Socket socket;
  private final BufferedReader reader;
  private final ObjectOutputStream writer;

  public SocketCommunicationChannel(ControlPanelLogic logic, Socket socket, BufferedReader reader, ObjectOutputStream writer) {
    this.logic = logic;
    this.socket = socket;
    this.reader = reader;
    this.writer = writer;

  }

  @Override
  public void sendActuatorChange(int nodeId, int actuatorId, boolean isOn) {
    // Implement this method to send actuator change commands through the socket
  }

  @Override
  public boolean open() {
    // Implement this method to open the communication channel
    return true;
  }
}

