package no.ntnu.greenhouse;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import no.ntnu.gui.greenhouse.NodeGuiWindow;
import no.ntnu.listeners.greenhouse.NodeStateListener;
import no.ntnu.server.Server;
import no.ntnu.tools.Logger;

/**
 * Application entrypoint - a simulator for a greenhouse.
 */
public class GreenhouseSimulator {
  private final Map<Integer, SensorActuatorNode> nodes = new HashMap<>();

  private final List<PeriodicSwitch> periodicSwitches = new LinkedList<>();
  private final boolean fake;
  private Server server;

  private static GreenhouseSimulator instance;


  /**
   * Create a greenhouse simulator.
   *
   * @param fake When true, simulate a fake periodic events instead of creating
   *             socket communication
   */
  public GreenhouseSimulator(boolean fake) {
    this.fake = fake;
  }

  /**
   * Initialise the greenhouse but don't start the simulation just yet.
   */
  public void initialize() {
    createNode(1, 2, 1, 0, 0,2);
    createNode(1, 0, 0, 2, 1,1);
    createNode(2, 0, 0, 0, 1,1);
    Logger.info("Greenhouse initialized");
  }

  private void createNode(int temperature, int humidity, int windows, int fans, int heaters, int doorLock) {
    SensorActuatorNode node = DeviceFactory.createNode(
        temperature, humidity, windows, fans, heaters, doorLock);
    nodes.put(node.getId(), node);
  }

  /**
   * Start a simulation of a greenhouse - all the sensor and actuator nodes inside it.
   */
  public void start() {
    initiateCommunication();
    for (SensorActuatorNode node : nodes.values()) {
      node.start();
    }
    for (PeriodicSwitch periodicSwitch : periodicSwitches) {
      periodicSwitch.start();
    }

    Logger.info("Simulator started");
  }

  private void initiateCommunication() {
    if (fake) {
      initiateFakePeriodicSwitches();
    } else {
      initiateRealCommunication();
    }
  }

  private void initiateRealCommunication() {
    this.server = new Server(12346);
    server.start();
  }

  private void initiateFakePeriodicSwitches() {
    periodicSwitches.add(new PeriodicSwitch("Window DJ", nodes.get(1), 2, 20000));
    periodicSwitches.add(new PeriodicSwitch("Heater DJ", nodes.get(2), 7, 8000));
  }

  /**
   * Stop the simulation of the greenhouse - all the nodes in it.
   */
  public void stop() {
    stopCommunication();
    for (SensorActuatorNode node : nodes.values()) {
      node.stop();
    }
  }

  private void stopCommunication() {
    if (fake) {
      for (PeriodicSwitch periodicSwitch : periodicSwitches) {
        periodicSwitch.stop();
      }
    } else {
      if (server != null) {
        server.shutdown();
      }
    }
  }

  /**
   * Add a listener for notification of node staring and stopping.
   *
   * @param listener The listener which will receive notifications
   */
  public void subscribeToLifecycleUpdates(NodeStateListener listener) {
    for (SensorActuatorNode node : nodes.values()) {
      node.addStateListener(listener);
    }
  }

  //small getter of the nodes
  public Map<Integer, SensorActuatorNode> getNodes (){
    return nodes;
  }

  //needed to retrieve the instance of the Greenhouse
  public static GreenhouseSimulator getInstance(boolean fake) {
    if (instance == null) {
      instance = new GreenhouseSimulator(fake);
    }
    return instance;
  }
}