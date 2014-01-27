/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jmc.api;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author DimanA90
 */
public class fsMan {
  private static File workDir = null;
  private static String appFolder = null;
  
  public static void setAppFolder(String foldername){
      appFolder = foldername;
  }
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
        if (workDir == null) workDir = getWorkingDirectory(appFolder);
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
     * Delete folder with files
     * 
     * 
     */
    public static void deleteDirectory(File Folder) 
    {
        if(!Folder.exists()){
            System.out.println("Folder not found. Return "+Folder.getPath());
        return;
        }
        if(Folder.isDirectory())
        {
            for(File f : Folder.listFiles())
                {
                deleteDirectory(f); //this is magic
                }  
            try{
                Folder.delete();
            }catch(Exception ex){
                System.out.println("Error deleting folder: "+Folder.getName());
            }  
        }
        else
        {try
          {
              Folder.delete();
          }catch(Exception e)
          {
              System.out.println("Error deleting file: "+Folder.getName());
          }
        }
    }
    /*
     * 
     */
    public static String getAppPath(){
        String path = null;
        if(path == null){
            try {
                path = URLDecoder.decode(getWorkingDirectory() + File.separator + GlobalVar.itemsServers[GlobalVar.CurrentServer],"UTF-8");
            } catch (UnsupportedEncodingException ex) {
               // Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return path;
    }
    public static void deleteDirectory(String FullPathToFolder) 
    {
        String path = new String();
      try {
          path = URLDecoder.decode(FullPathToFolder,"UTF-8");
      } catch (UnsupportedEncodingException ex) {
          Logger.getLogger(fsMan.class.getName()).log(Level.SEVERE, null, ex);
      }
        File dir = new File(path);
        deleteDirectory(dir);
    }
}
