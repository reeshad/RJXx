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
//package org.ftilde.rjx;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.Socket;

public class Common
{

    public static final int REQ_PORT = 9005;
    public static final int LOGIN_PORT = 9006;
    public static final int BROADCAST_PORT = 9007;

    /*
      Necessary members for Request Making and Response
     */
    /*
      Can be modified later. Hence they are private
     */
    private static final char cREQ = 'R';
    private static final char cMSG = 'M';
    private static final char cRES = 'S';

    private static final char cLIV = 'L';
    private static final char cQUE = 'Q';
    private static final char cREJ = 'J';

    private static final char cERR = 'E';

    private static final char cLGI = 'I';
    private static final char cLGO = 'O';
    private static final char cAUD = 'A';
    /*
      for client
     */
    public static String getReqMessage(int id)
    {
        return cREQ + (new Integer(id)).toString();
    }
    /*
      for client and server
     */
    public static String getMsgMessage(int id, String s)
    {
        return cMSG + (new Integer(id)).toString() + ':' + s;
    }
    /*
      for server
     */
    public static String getResMessage(STA status)
    {
        char res = cERR;
        switch(status)
        {
        case LIVE:
            res = cLIV;
            break;
        case QUEUE:
            res = cQUE;
            break;
        case REJECT:
            res = cREJ;
        }
        return cRES + "" + res;
    }
    public static int getReqID(String s) //ID of requested RJ
    {
        if(s.length() < 2)
            return -1;
        return Integer.parseInt( s.substring(1) );
    }
    /*
      for client and server
    */
    public static REQ getReqType(String s)
    {
        if(s==null || s.length()<1)
            return REQ.ERR;

        switch(s.charAt(0))
        {
        case cREQ:
            return REQ.REQ;
        case cMSG:
            return REQ.MSG;
        case cRES:
            return REQ.RES;
        default:
            return REQ.ERR;
        }
    }
    /*
      for client
    */
    public static STA getResponseType(String s)
    {
        if(s==null || s.length()<2)
            return STA.ERR;

        switch(s.charAt(1))
        {
        case cLIV:
            return STA.LIVE;
        case cQUE:
            return STA.QUEUE;
        case cREJ:
            return STA.REJECT;
        default:
            return STA.ERR;
        }
    }
    /*
      for client and server
     */
    public static String getMessage(String s)
    {
        return s.substring(1);
    }

    public static String getLogInMessage(int id)
    {
        return cLGI + "" + id;
    }

    public static String getLogOutMessage(int id)
    {
        return cLGO + "" + id;
    }

    public static LOG getLogMessageType(String s)
    {
        if(s == null || s.length() < 1)
            return LOG.ERROR;
        if(s.charAt(0) == cLGI)
            return LOG.LOGIN;
        if(s.charAt(0) == cLGO)
            return LOG.LOGOUT;
        return LOG.AUDIO;
    }
    public static BufferedReader getReader(Socket s) throws IOException
    {
        return new BufferedReader(new InputStreamReader(s.getInputStream()));
    }

    public static PrintWriter getWriter(Socket s) throws IOException
    {
        return new PrintWriter(s.getOutputStream());
    }
}
