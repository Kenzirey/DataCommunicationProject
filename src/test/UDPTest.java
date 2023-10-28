import no.ntnu.server.EchoClient;
import no.ntnu.server.Server;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;

import static org.junit.Assert.*;

public class UDPTest {
  EchoClient client;

  /**
   * How do I avoid throws? Using try/catch?
   * @throws SocketException
   * @throws UnknownHostException
   */
  @Before
  public void setup() throws SocketException, UnknownHostException {
    new Server().start();
    client = new EchoClient();
  }

  @Test
  public void whenCanSendAndReceivePacket_thenCorrect() throws IOException {
    String echo = client.sendEcho("test");
    assertEquals("test", echo);
    echo = client.sendEcho("server is working");
    assertNotEquals("test", echo);
  }

  @After
  public void tearDown() throws IOException {
    client.sendEcho("end");
    client.close();
  }
}
