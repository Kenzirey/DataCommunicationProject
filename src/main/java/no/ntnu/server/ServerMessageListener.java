package no.ntnu.server;

import java.io.IOException;

public interface ServerMessageListener {
  void onMessageReceived(String message) throws IOException;
}
