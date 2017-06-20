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
//package org.ftilde.rjx;

//import java.net.Socket;
import java.awt.Container;
import java.awt.FlowLayout;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.IOException;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.DefaultListModel;
import javax.swing.ListSelectionModel;

public class Server extends JFrame implements Runnable
{

    HashMap<String, RequestHandler> reqs;

    Socket socket;
    ServerSocket serverSocket;

    JList<String> list;
    JButton btnLive;
    JButton btnQueue;
    JButton btnReject;
    JButton btnMessage;
    JTextField txtMessage;

    DefaultListModel<String> listModel;

    public Server()
    {
        reqs = new HashMap<String, RequestHandler>();

        setSize(400,300);

        Container c = getContentPane();
        c.setLayout(new FlowLayout());



        listModel = new DefaultListModel<String>();
        //	listModel.addElement("Kathy Green");

        list = new JList<String>(listModel); //data has type Object[]

        //String[] data = {"one", "two", "three", "four"};
        //list = new JList<String>(data);

        list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        list.setLayoutOrientation(JList.VERTICAL);
        list.setVisibleRowCount(-1);
        //...
        JScrollPane listScroller = new JScrollPane(list);
        listScroller.setPreferredSize(new Dimension(250, 80));

        c.add(listScroller);
        c.add(btnLive = new JButton("Live"));
        c.add(btnQueue =  new JButton("Queue"));
        c.add(btnReject =  new JButton("Reject"));
        c.add(txtMessage = new JTextField(20));
        c.add(btnMessage =  new JButton("Send Message"));

//*
        btnLive.addActionListener(
            new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                onLive();
            }
        }
        );
        btnQueue.addActionListener(
            new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                onQueue();
            }
        }
        );
        btnReject.addActionListener(
            new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                onReject();
            }
        }
        );
        btnMessage.addActionListener(
            new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                sendMessage();
            }
        }
        );
        //*/

        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        (new BroadCaster()).start();

    }

    public void start()
    {
        Thread t = new Thread(this);
        t.start();
    }

    public void run()
    {
        try
        {
            serverSocket = new ServerSocket(Common.REQ_PORT);
            w("Waiting for request");
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
            try
            {
                reader = Common.getReader(socket);
                writer = Common.getWriter(socket);
                while(true)
                {

                    String line = reader.readLine();

                    REQ reqType = Common.getReqType(line);
                    switch(reqType)
                    {
                    case REQ:
                        onRequest(Common.getReqID(line));
                        break;
                    case MSG:
                        String message = Common.getMessage(line);
                        messageArrived(message);
                        break;
                    }
                    w("Accpeted " + socket.getInetAddress() + " " + line);
                    //writer.println(Common.getMsgMessage(0, "Hello"));
                    //writer.flush();

                }
            }
            catch(IOException e)
            {
            }
        }
        public void onRequest(int id)
        {
            //Actually we should retrieve the user name instead of ID.
            String sID = "" + id;

            reqs.put(sID, this);
            requestArrived(this, sID);
        }

        public void send(String s)
        {
            writer.println(s);
            writer.flush();
        }
    }

    void messageArrived(String message)
    {
        //txtMsg.setText(message);
        w(message);
    }
    void requestArrived(RequestHandler rh, String id)
    {
        listModel.addElement(id);
        int index = listModel.getSize() - 1;
        list.setSelectedIndex(index);
        list.ensureIndexIsVisible(index);
    }

    RequestHandler getSelectedRJ()
    {
        String selected = list.getSelectedValue();

        return reqs.get(selected);
    }

    void onLive()
    {
        RequestHandler rh = getSelectedRJ();
        if(rh != null)
            rh.send(Common.getResMessage(STA.LIVE));
    }

    void onQueue()
    {
        RequestHandler rh = getSelectedRJ();
        if(rh != null)
            rh.send(Common.getResMessage(STA.QUEUE));
    }

    void onReject()
    {
        RequestHandler rh = getSelectedRJ();
        if(rh != null)
            rh.send(Common.getResMessage(STA.REJECT));
    }

    void sendMessage()
    {
        RequestHandler rh = getSelectedRJ();
        if(rh != null)
            rh.send(Common.getMsgMessage(0, txtMessage.getText()));
    }

    public static void main(String args[])
    {
        (new Server()).start();
    }

    public void w(String s)
    {
        System.out.println(s);
    }
}
