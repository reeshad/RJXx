/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Client;

/**
 *
 * @author reesha
 */
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

class Listener extends Thread
{
    Socket socket = null;
    InputStream reader = null;
    PrintWriter writer = null;

    int id;

    boolean isListening = true;

    public Listener(int id)
    {
        this.id = id;
    }
    public void logOut ()
    {
        isListening = false;
    }
    public void run()
    {

        if(socket != null)
            return;
        initReaderAndWriter();

        writer.println(Common.getLogInMessage(id));
        writer.flush();

        String line;
        //while(isListening)
        //{
//                if(reader.ready())
        //    processBroadcastData();
        //}
        //writer.println(Common.getLogOutMessage(id));
        //writer.flush();



    }

    void initReaderAndWriter()
    {
        if(socket == null)
        {
            try
            {
                socket = new Socket("127.0.0.1", Common.LOGIN_PORT);
                try
                {
                    if(writer == null)
                        writer = Common.getWriter(socket);
                    if(reader == null)
                        reader = socket.getInputStream();
                }
                catch(IOException e)
                {
                }
            }
            catch(IOException e)
            {
            }
        }


    }

    void processBroadcastData()
    {
        byte buffer[] = new byte[1024];

        int len;
        int start = 0;
        try{
        do
        {
            len = reader.read(buffer, start, 1024);
            start += 1024;
        }
        while(len == 1024);
        }catch(IOException e){

        }
    }

    public void close(){
        writer.println(Common.getLogOutMessage(id));
        writer.flush();
    }
}
