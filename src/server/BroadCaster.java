/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package server;

/**
 *
 * @author reesha
 */
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class BroadCaster extends Thread
{

    HashMap<String, RequestHandler> reqs;

    Socket socket;
    ServerSocket serverSocket;

    public BroadCaster(){
        reqs = new HashMap<String, RequestHandler>();
    }
    public void run()
    {
        try
        {
            serverSocket = new ServerSocket(Common.LOGIN_PORT);
            w("Waiting for broadcasting login request");
            RequestHandler rh;

            while(true)
            {
                rh = new RequestHandler(serverSocket.accept());
                rh.start();
            }
        }
        catch(Exception e)
        {

        }
    }

    class RequestHandler extends Thread
    {
        BufferedReader reader;
        PrintWriter writer;

        Socket socket;
        RequestHandler(Socket socket)
        {
            this.socket = socket;
        }
        public void run()
        {
            boolean isRunning = true;
            try
            {
                reader = Common.getReader(socket);
                writer = Common.getWriter(socket);
                while(isRunning)
                {

                    String line = reader.readLine();
//*
                    LOG reqType = Common.getLogMessageType(line);
                    switch(reqType)
                    {
                    case LOGIN:
                        onLogIn(Common.getReqID(line));
                        break;
                    case LOGOUT:
                        onLogOut(Common.getReqID(line));
                        isRunning = false;
                        break;
                    case AUDIO:

                        break;
                    default:
                        continue;
                    }
                    w("Accpeted " + socket.getInetAddress() + " " + line);
                    //writer.println(Common.getMsgMessage(0, "Hello"));
                    //writer.flush();
//*/
                }
            }
            catch(IOException e)
            {
            }
        }
        public void onLogIn(int id)
        {
            //Actually we should retrieve the user name instead of ID.
            String sID = "" + id;

            reqs.put(sID, this);
            w(id + " is added.");
//            requestArrived(this, sID);
        }
        public void onLogOut(int id)
        {
            //Actually we should retrieve the user name instead of ID.
            String sID = "" + id;

            reqs.remove(sID);
            w(id + " is removed.");
//            requestArrived(this, sID);
        }
        public void send(String s)
        {
            writer.println(s);
            writer.flush();
        }
    }


    public void w(String s)
    {
        System.out.println(s);
    }
}

