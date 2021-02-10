import java.io.*;
import java.util.*;

/*
ClientInput
thread handling client socket input for SimpleTCPClient
*/

class ClientInput extends Thread {
  private SimpleTCPClient client;
  private BufferedReader in;

  //Constructor
  ClientInput(SimpleTCPClient client) {
    this.client = client;
    this.in     = client.getInput();
  }


  // Getters
  /**
  * @return the client
  */
  public SimpleTCPClient getClient() {
    return client;
  }

  /**
  * @return the in
  */
  public BufferedReader getInput() {
    return in;
  }



  @Override
  public void run() {
    String msgReceived = "";

    try {
      while ( true ) {
        msgReceived = in.readLine();
        // Break if server closed connection
        if (this.client.getSocket().isInputShutdown()) {
          System.out.println("Server connection lost.");
          break;
        }
        if ( ! msgReceived.equals("null") ) {
          System.out.println(msgReceived);
        }
      }

    } catch (NullPointerException e) {
      //e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      this.client.close();
    }
  }


}
