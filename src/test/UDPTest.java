import no.ntnu.greenhouse.GreenhouseSimulator;
import no.ntnu.server.EchoClient;
import no.ntnu.server.Server;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.net.SocketException;
import java.net.UnknownHostException;

import static org.junit.Assert.*;

public class UDPTest {
  EchoClient client;
  Server server;

  /**
   * Starts up the server and client before each test.
   * @throws SocketException
   * @throws UnknownHostException
   */
  @Before
  public void setup() {
    boolean fake = false;
    server = new Server(4445, new GreenhouseSimulator(fake));
    server.start();
    client = new EchoClient("localHost", 4445);
  }

  @Test
  public void sendAndReceivePackets() {
    String echo = client.sendAndReceive("name");
    assertEquals("UDP Server", echo);
    echo = client.sendAndReceive("server is working");
    assertNotEquals("test", echo);
  }

  @After
  public void tearDown() {
    server.shutdown();
    client.close();
  }
}
