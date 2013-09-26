/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jmc.minecraft.utils;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author DimanA90
 */
public class LauncherPreloader {

    /**
     * @param args the command line arguments
     */
public static void main(String[] args) {
 
 System.out.println("Path :"+LauncherPreloader.class.getProtectionDomain().getCodeSource().getLocation().getPath());
 String pathJFX = System.getProperty("java.home")+File.separator+"lib"+File.separator+"jfxrt.jar";
 File AppPath = new File(LauncherPreloader.class.getProtectionDomain().getCodeSource().getLocation().getPath());     

        ArrayList<String> params = new ArrayList<String>();

            params.add("java");
 
        params.add("-classpath");
    try {
        params.add(URLDecoder.decode(AppPath.getPath()+";"+pathJFX,"UTF-8"));
    } catch (UnsupportedEncodingException ex) {
        //Logger.getLogger(LauncherPreloader.class.getName()).log(Level.SEVERE, null, ex);
    }
        params.add("jmclauncherfx.JMCLauncherFX");
        

        ProcessBuilder pb = new ProcessBuilder(params);
        jmc.minecraft.utils.ConfigLoaderCore cf = new jmc.minecraft.utils.ConfigLoaderCore();

   if(cf.LoadModeConfig())
   {
     if(GlobalVar.LauncherMode.contains("installed"))
     {
        //pb.environment().clear();
        if(Utils.getPlatform()==Utils.OS.windows)
        {
            pb.environment().put("APPDATA", GlobalVar.LauncherStandalonePath);  
        }else if (Utils.getPlatform()==Utils.OS.linux) 
        {  
            pb.environment().put("user.home", GlobalVar.LauncherStandalonePath);
        }else if(Utils.getPlatform()==Utils.OS.macos)
        {
            pb.environment().put("user.home", GlobalVar.LauncherStandalonePath);
        }
     }else if(GlobalVar.LauncherMode.contains("portable"))
     {
        //pb.environment().clear();
        File filetmp = new File(LauncherPreloader.class.getProtectionDomain().getCodeSource().getLocation().getPath());
        System.out.println(filetmp.getAbsolutePath());
        String path = (new File(LauncherPreloader.class.getProtectionDomain().getCodeSource().getLocation().getPath())).getAbsolutePath();
        String patht = path.substring(0, path.length()-filetmp.getName().length());
        if(Utils.getPlatform()==Utils.OS.windows)
        {
            pb.environment().put("APPDATA", patht);  
        }else if (Utils.getPlatform()==Utils.OS.linux) 
        {  
            pb.environment().put("user.home", patht);
        }else if(Utils.getPlatform()==Utils.OS.macos)
        {
            pb.environment().put("user.home", patht);
        } 
     }
        
    }    
        //pb.directory(new File(ClientFolderPath));
        pb.redirectErrorStream(true);
        String logpath = new String();
        try{
            logpath =URLDecoder.decode(LauncherPreloader.class.getProtectionDomain().getCodeSource().getLocation().getPath()+".log","UTF-8");
        }catch(Exception e){
            
        }
        File log = new File(logpath);
        pb.redirectErrorStream(true);
        pb.redirectOutput(log);     
    try {
        pb.start();
    } catch (IOException ex) {
        Logger.getLogger(LauncherPreloader.class.getName()).log(Level.SEVERE, null, ex);
    }
 }
}
