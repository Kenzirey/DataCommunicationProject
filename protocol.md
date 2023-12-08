# Communication protocol

This document describes the protocol used for communication between the different nodes of the
distributed application.

## Terminology

* Sensor - a device which senses the environment and describes it with a value (an integer value in
  the context of this project). Examples: temperature sensor, humidity sensor.
* Actuator - a device which can influence the environment. Examples: a fan, a window opener/closer,
  door opener/closer, heater.
* Sensor and actuator node - a computer which has direct access to a set of sensors, a set of
  actuators and is connected to the Internet.
* Control-panel node - a device connected to the Internet which visualizes status of sensor and
  actuator nodes and sends control commands to them.
* Graphical User Interface (GUI) - A graphical interface where users of the system can interact with
  it.

## The underlying transport protocol used: UDP (User Datagram Protocol)

For this greenhouse project, UDP was chosen over TCP as the transport-layer protocol.

### Why UDP over TCP?
1. Real-time tracking: The greenhouse system requires real-time tracking of environmental variables
such as temperature and humidity. UDP's low-latency data transmission is more suited for real-time updates,
where receiving outdated packets is less critical than ensuring a continuous stream flow of data. Temperature
and humidity values do not change rapidly, so it is not necessary to ensure that every single packet is received.
2. Bandwidth: UDP uses less bandwidth to send data, due to smaller header size, no handshake required, or maintain connection state information. 
Thus, UDP is more resource-efficient for our use case, where sensor nodes will be sending data to the control panel nodes frequently, requiring a fast protocol.
Being resource-efficient and fast is important for a lightweight system like the greenhouse project, which may run on limited-resource devices (in this case, one server).
3. Simplicity: the Greenhouse application involves sending many small packets of data, such as sensor readings, switch statuses, actuator updates etc. 
UDP's simplicity allows for these small packets of data to be sent without the overhead of establishing and maintaining a connection.


### The port number(s):
* 12346 - used for communication between sensor/actuator nodes and control-panel nodes.

## The architecture
Emma write this part
TODO - show the general architecture of your network. Which part is a server? Who are clients? 
Do you have one or several servers? Perhaps include a picture here.
# TODO: Write this when we have commands and such set up with the Server.


## The flow of information and events
# Robert?
TODO - describe what each network node does and when. Some periodic events? Some reaction on 
incoming packets? Perhaps split into several subsections, where each subsection describes one 
node type (For example: one subsection for sensor/actuator nodes, one for control panel nodes).

Commands are getting send to the nodes which distribute it to the sensors / actuators of the nodes.
So our nodes are a collection of sensors and actuators, and are responsible for the control of those.
## Connection and state

#### Connection-less
* The communication protocol is based on UDP, which is a connection-less protocol. 
This means that the sensors and actuators do not need to establish a connection with the control panel nodes before exchanging data.
Each packet of data is sent independently of each other, with no formal session between sender and receiver. This allows for flexible communication,
where packets can be broadcast to multiple devices without the overhead of managing multiple connections.
# Work in progress: Make sure that at the end, the communication protocol stays active to this description.

#### Stateless
* The greenhouse system is stateless, as each request is processed independently, and is not tracked, 
nor is the data maintained for later use. The server does not include any methods for tracking user sessions, or storing its session state.
DatagramSocket that is used for packet delivery, is designed for connectionless delivery, 
which reinforces the stateless nature of the greenhouse server.
In addition, each packet has the standard response per request, and is not altered based on previous interactions.

## Types, constants

TODO ROBERT - Do you have some specific value types you use in several messages? They you can describe 
them here.

We use the specific value type LocalTime, but the output gets always converted to Strings, which makes the communication easier.

## Message format

TODO ROBERT - describe the general format of all messages. Then describe specific format for each 
message type in your protocol.

First there is a message which says that its recieving a packet, afterwards its specifies the command which the client sends.
The next message is that the server is waiting for the packet. And the last is that the server recieves the message and gives the response.


### Error messages

TODO - describe the possible error messages that nodes can send in your system.


## An example scenario

TODO - describe a typical scenario. How would it look like from communication perspective? When 
are connections established? Which packets are sent? How do nodes react on the packets? An 
example scenario could be as follows:
1. A sensor node with ID=1 is started. It has a temperature sensor, two humidity sensors. It can
   also open a window.
2. A sensor node with ID=2 is started. It has a single temperature sensor and can control two fans
   and a heater.
3. A control panel node is started.
4. Another control panel node is started.
5. A sensor node with ID=3 is started. It has a two temperature sensors and no actuators.
6. After 5 seconds all three sensor/actuator nodes broadcast their sensor data.
7. The user of the first-control panel presses on the button "ON" for the first fan of
   sensor/actuator node with ID=2.
8. The user of the second control-panel node presses on the button "turn off all actuators".

## Reliability and security

TODO - describe the reliability and security mechanisms your solution supports.
