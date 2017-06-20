/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Client;

import java.awt.FlowLayout;
import java.awt.Container;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JLabel;


public class Client extends JFrame
{
    Listener listener;

    JButton btnReq;
    JButton btnSend;

    JButton btnClose;


    JTextField txtServer;
    JTextField txtMsg;

    Socket socket = null;
    BufferedReader reader = null;
    PrintWriter writer = null;
    int id;

    static int total = 0;

    Client()
    {
        super("Client");

        id = total++;

        setTitle("Client " + id);
        setSize(400,300);

        Container c = getContentPane();
        c.setLayout(new FlowLayout());
        c.add(new JLabel("Server"));
        c.add(txtServer = new JTextField("127.0.0.1"));
        c.add(btnReq = new JButton("RJ Req"));
        c.add(txtMsg = new JTextField("Write Your Message"));
        c.add(btnSend = new JButton("Send"));
        c.add(btnClose = new JButton("Close"));

        btnReq.addActionListener(
            new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                //makeRequest();
                (new RequestMaker()).start();
            }
        }
        );
        btnSend.addActionListener(
            new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                (new MessageSender()).start();
            }
        }
        );
        btnClose.addActionListener(
            new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                //makeRequest();
                listener.close();
                //System.exit(0);
            }
        }
        );
        setVisible(true);
        //setDefaultCloseOperation(EXIT_ON_CLOSE);

        listener = new Listener(id);
        listener.start();
    }

    class RequestMaker extends Thread
    {
        public void run()
        {
            makeRequest();
        }
        public void makeRequest()
        {
            if(socket != null)
                return;
            initReaderAndWriter();
            try
            {
                writer.println(Common.getReqMessage(id));
                writer.flush();
                String line;
                while(true)
                {
                    line = reader.readLine();
                    REQ reqType = Common.getReqType(line);
                    switch(reqType)
                    {
                    case RES:
                        STA status = Common.getResponseType(line);
                        onResponse(status);
                        txtServer.setText(line);
                        break;
                    case MSG:
                        String message = Common.getMessage(line);
                        txtServer.setText(message);
                        break;
                    }
                }
            }
            catch(Exception e)
            {
            }
        }
    }

    class MessageSender extends Thread
    {
        public void run()
        {
            writer.println(Common.getMsgMessage(id, txtMsg.getText()));
            writer.flush();
        }
    }

    void closeEverything()
    {
        reader = null;
        writer = null;
        socket = null;
    }

    void initReaderAndWriter()
    {
        if(socket == null)
        {
            try
            {
                socket = new Socket("127.0.0.1", Common.REQ_PORT);
            }
            catch(IOException e)
            {
            }
        }

        try
        {
            if(writer == null)
                writer = Common.getWriter(socket);
            if(reader == null)
                reader = Common.getReader(socket);
        }
        catch(IOException e)
        {
        }
    }

    public void onResponse(STA resType)
    {

        switch(resType)
        {
        case LIVE:

            break;
        case QUEUE:

            break;
        case REJECT:

            break;
        default:
        }
    }



    public static void main(String args[])
    {
        new Client();
        new Client();
    }
}

