/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jmc.minecraft.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;


/**
 *
 * @Copy from notch launcher
 */
public class Utils {
 
  private static File workDir = null;
    
    
  public static enum OS
  {
    linux, solaris, windows, macos, unknown ;
  }
  public static OS getPlatform() {  
      
    String osName = System.getProperty("os.name").toLowerCase();
      
    if (osName.contains("win")) return OS.windows;
    if (osName.contains("mac")) return OS.macos;
    if (osName.contains("solaris")) return OS.solaris;
    if (osName.contains("sunos")) return OS.solaris;
    if (osName.contains("linux")) return OS.linux;
    if (osName.contains("unix")) return OS.linux;
    
    return OS.unknown;
  }
    public static File getWorkingDirectory() {
    if (workDir == null) workDir = getWorkingDirectory(GlobalVar.WorkDir);
    return workDir;
  }
    public static File getWorkingDirectory(String applicationName) {
    String userHome = new String();
      try {
          userHome = URLDecoder.decode(System.getProperty("user.home", "."),"UTF-8");
      } catch (UnsupportedEncodingException ex) {
          //Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
      }
    File workingDirectory;
    switch (getPlatform().ordinal()) {
    case 0:
        System.out.println("Current system linux");
    case 1:
    {
        System.out.println("Current system solaris");
      workingDirectory = new File(userHome, '.' + applicationName + '/');
      break;
    }
    case 2:
    {
        System.out.println("Current system windows");
      //String applicationData = System.getenv("APPDATA");
      String applicationData = new String();
      try {
          applicationData = URLDecoder.decode(System.getenv("APPDATA"),"UTF-8");
      }catch(Exception e){  }
      if (applicationData != null)
    	  workingDirectory = new File(applicationData, "." + applicationName + '/');
      else
        workingDirectory = new File(userHome, '.' + applicationName + '/');
      break;
    }
    case 3:
    {
        System.out.println("Current system mac os");
      workingDirectory = new File(userHome, "Library/Application Support/" + applicationName);
      break;
    }

    default:
    {
      System.out.println("Unknown system =(");
      workingDirectory = new File(userHome, applicationName + '/');
    }
    }
    if ((!workingDirectory.exists()) && (!workingDirectory.mkdirs())) throw new RuntimeException("The working directory could not be created: " + workingDirectory);
    return workingDirectory;
  }
    
 /*
  * @Open link in desktop web browser
  */
  public static void openLink(URI uri) {
    try {
      Object o = Class.forName("java.awt.Desktop").getMethod("getDesktop", new Class[0]).invoke(null, new Object[0]);
      o.getClass().getMethod("browse", new Class[] { URI.class }).invoke(o, new Object[] { uri });
    } catch (Throwable e) {
      System.out.println("Failed to open link " + uri.toString());
    }
  }
 /*
  * Execute post and wait answer
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

  public static boolean isEmpty(String str) {
    return (str == null) || (str.length() == 0);
  }
  
  public static boolean isOnline() {
    Boolean result = false;
    HttpURLConnection con = null;
    try {
        // HttpURLConnection.setFollowRedirects(false);
        // HttpURLConnection.setInstanceFollowRedirects(false)
        con = (HttpURLConnection) new URL(GlobalVar.HostUrl).openConnection();
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
 
public static long downloadFilesSize(String strURL) {
        try {
            URL connection = new URL(strURL);
            HttpURLConnection urlconn;
            urlconn = (HttpURLConnection) connection.openConnection();
            urlconn.setRequestMethod("GET");
            urlconn.connect();
            
            return urlconn.getContentLength();
        } catch (IOException e) {
            System.out.println(e);
            return 0;
        }
    }  
  
public static boolean login(String userName, String password) 
{
    try {
      String parameters = "user=" + URLEncoder.encode(userName, "UTF-8") + "&password=" + URLEncoder.encode(password, "UTF-8") + "&version=" + URLEncoder.encode(GlobalVar.Version, "UTF-8");
      String result = Utils.excutePost(GlobalVar.AuthURL, parameters); 
      if (!result.contains(":")) {
        if (result.trim().equals("Bad login")) {
          JOptionPane.showMessageDialog( null,
                    "Неправильный логин или пароль!",
                    "Ошибка",
                    JOptionPane.WARNING_MESSAGE);
                 return false;
        } else if (result.trim().equals("Old version")) {
                    JOptionPane.showMessageDialog( null,
                    "Нужно обновить лаунчер!",
                    "Ошибка",
                    JOptionPane.WARNING_MESSAGE);
                        openLink(new URI(GlobalVar.DownloadNewLauncherURL));
                        //System.exit(0); //Close launcher and open download launcher link
                 return false;
        } else {
                    JOptionPane.showMessageDialog( null,
                    result,
                    "Неизвестная ошибка",
                    JOptionPane.ERROR_MESSAGE);
                 return false;
        }
      }
      String[] values = result.split(":");

      GlobalVar.userName = values[2].trim();
      GlobalVar.latestVersion = values[0].trim();
      GlobalVar.downloadTicket = values[1].trim();
      GlobalVar.sessionId = values[3].trim();
      
      return true;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
}
 

public static void ForseUpdate()
{
    //String FullPath =getWorkingDirectory() + File.separator + GlobalVar.itemsServers[GlobalVar.CurrentServer];
    deleteDirectory(GetCurrentClientDir());

}
public static void deleteDirectory(File Folder) 
{
    
    if(!Folder.exists())
    {
        System.out.println("Folder not found. Return "+Folder.getPath());
      return;
    }
    if(Folder.isDirectory())
    {
      for(File f : Folder.listFiles())
      {
           deleteDirectory(f); //this is magic
      }  
      try
      {
           System.out.println("Deleting folder: "+Folder.getPath());  
           Folder.delete();
      }catch(Exception ex)
      {
            System.out.println("Error deleting folder: "+Folder.getName());
      }  
    }
    else
    {
       try
          {
              System.out.println("Deleting file: "+Folder.getName());
              Folder.delete();
          }catch(Exception e)
          {
              System.out.println("Error deleting file: "+Folder.getName());
          }
    }
}
public static void deleteDirectory(String FullPathToFolder) {
    File dir = new File(FullPathToFolder);
  deleteDirectory(dir);
    
}
public static void LogPrint(Exception e){
    LogPrint("EXCEPTION", e.toString());
    e.printStackTrace();
}
public static void LogPrint(String message){
    LogPrint("CUSTOM", message);
} 
public static void LogPrint(String prefix,String message){
    System.out.println((new SimpleDateFormat("dd.MM.yyyy.HH:mm:ss z").format(System.currentTimeMillis()))+"["+prefix+"]:"+message );
}  

public static String GetCurrentClientDir(){
    String FullPath = new String();
      try {
          FullPath = URLDecoder.decode(getWorkingDirectory() + File.separator + GlobalVar.itemsServers[GlobalVar.CurrentServer],"UTF-8");
      } catch (UnsupportedEncodingException ex) {
          Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
      }

    return FullPath;
}
}
