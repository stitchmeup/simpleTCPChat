import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

/*
SimpleService
connection handler for SimpleTCPClient
*/

class SimpleService extends Thread {
  private Socket s;
  private BufferedReader in;
  private PrintWriter out;
  private SimpleTCPServer srv;

  // Constructor
  SimpleService(Socket s, SimpleTCPServer srv) {
    this.s = s;
    try {
      this.in   = new BufferedReader(new InputStreamReader(s.getInputStream()));
      this.out  = new PrintWriter(s.getOutputStream());
      this.srv  = srv;

    } catch (IOException e) {
      e.printStackTrace();
    }
  }


  // Getters
  /**
   * @return the s
   */
  public Socket getSocket() {
  	return s;
  }

  /**
  * @return the in
  */
  public BufferedReader getInput() {
    return in;
  }

  /**
  * @return the out
  */
  public PrintWriter getOutput() {
    return out;
  }

  /**
  * @return the srv
  */
  public SimpleTCPServer getServer() {
    return srv;
  }


  @Override
  public void run() {
    String msgReceived = "";

    try {
      while ( true ) {
        msgReceived = this.in.readLine();
        // If client does not disconnect properly (i.e with a .bye message),
        // message might point to null
        if (msgReceived == null || msgReceived.equals(".bye")) {
          sendToBroadcast(this.s.getRemoteSocketAddress().toString() + " disconnected.");
          break;
        } else {
          sendToBroadcast(this.s.getRemoteSocketAddress().toString() + " : " + msgReceived);
        }

      }

    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        if (this.s != null) {
          this.s.close();
        }

        this.srv.release(this);

        if (this.in != null) {
          in.close();
        }

        if (this.out != null) {
          out.close();
        }

      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }


  // method
  private void sendToBroadcast(String msg) {
    System.out.println(msg);
    this.srv.broadcastMessage(msg, this);
  }


}
