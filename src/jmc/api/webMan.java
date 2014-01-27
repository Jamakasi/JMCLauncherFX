/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jmc.api;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 *
 * @author DimanA90
 */
public class webMan {
    /*
     * Return file size from server
     */
    public static long getFileSize(String strURL) {
        long result = -1;
      HttpURLConnection con = null;
        try {
            con = (HttpURLConnection) new URL(strURL).openConnection();
            con.setRequestMethod("GET");
            result = con.getContentLengthLong();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (con != null) {
                try {
                    con.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }
    /*
     * Download file and save to hdd
     * Example downloadFile("http://mysite.ru/files/","C:\","example.zip")
     * It download file from "http://mysite.ru/files/example.zip" and save to "C:\example.zip"
     * @return true if file downloaded and false if download error
     */
    private boolean downloadFile(String pathURL, String pathToSave, String fileName) 
    {
        Integer buffSize = 1024;
        byte buffer[] = new byte[buffSize];
        HttpURLConnection urlconn = null;
        InputStream in = null;
        OutputStream writer = null;
        boolean result = false;
        try {
            urlconn = (HttpURLConnection) new URL(pathURL+fileName).openConnection();
            urlconn.setRequestMethod("GET");
            urlconn.connect();
            in = urlconn.getInputStream();
            writer = new FileOutputStream(pathToSave+fileName);
            int c = in.read(buffer);
            while (c > 0) {
                writer.write(buffer, 0, c);
                c = in.read(buffer);
            }
            result = true;
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            if(urlconn != null){
               try{
                   urlconn.disconnect();
               }catch (Exception e) {
                    e.printStackTrace();
               }
            }if(writer != null){
               try{
                   writer.flush();
                   writer.close();
               }catch (Exception e) {
                    e.printStackTrace();
               }
            }if(in != null){
               try{
                   in.close();
               }catch (Exception e) {
                    e.printStackTrace();
               }
            }
                
        }
        return result;
    }
    /*
     * Check internet connection
     * @return true if avaible or false
     */
    private static boolean isInternetConnected() {
        Boolean result = false;
        HttpURLConnection con = null;
        try {
            con = (HttpURLConnection) new URL("http://www.ya.ru").openConnection();
            con.setRequestMethod("HEAD");
            result = (con.getResponseCode() == HttpURLConnection.HTTP_OK);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (con != null) {
                try {
                    con.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }
    /*
     * Send post to url and get response
     */
     public static String excutePost(String targetURL, String urlParameters)
     {
            HttpURLConnection connection = null;
            try
            {
              URL url = new URL(targetURL);

              connection = (HttpURLConnection) url.openConnection();
              connection.setRequestMethod("POST");
              connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

              connection.setRequestProperty("Content-Length", Integer.toString(urlParameters.getBytes().length));
              connection.setRequestProperty("Content-Language", "en-US");

              connection.setUseCaches(false);
              connection.setDoInput(true);
              connection.setDoOutput(true);

              connection.connect();


              DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
              wr.writeBytes(urlParameters);
              wr.flush();
              wr.close();

              InputStream is = connection.getInputStream();
              BufferedReader rd = new BufferedReader(new InputStreamReader(is));

              StringBuffer response = new StringBuffer();
              String line;
              while ((line = rd.readLine()) != null)
              {
                response.append(line);
                response.append('\r');
              }
              rd.close();

              String str1 = response.toString();
              return str1;
            }
            catch (Exception e)
            {
              e.printStackTrace();
              return null;
            }
            finally
            {
              if (connection != null)
                connection.disconnect();
            }
      }
}
