/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jmc.minecraft.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author DimanA90
 */
public class MCGameRuner {
private String ClientFolderPath = Utils.GetCurrentClientDir() + File.separator + ".minecraft" + File.separator;
private String AppDataPath = Utils.GetCurrentClientDir() + File.separator ;

public void LetsGame(boolean Online)
{
Utils.LogPrint("gameruner", "gameroot:"+ClientFolderPath);
Utils.LogPrint("gameruner", "gamefakeappdata:"+ClientFolderPath);
ArrayList<String> params = new ArrayList<String>();

        if(GlobalVar.ExpertSettings){
            params.add(GlobalVar.JavaPath);
            params.add(GlobalVar.JavaVMArg);
        }else
        {
            params.add("java");
        }
        params.add("-cp");
        params.add(ClientFolderPath+"bin"+ File.separator+"*");
        if(GlobalVar.oldminecarft)
        {
             params.add("net.minecraft.client.Minecraft");
             params.add(GlobalVar.userName);
             if (Online)
             {
                params.add(GlobalVar.sessionId);
             }
        }else
        {
            params.add("-Dfml.ignoreInvalidMinecraftCertificates=true");  //Игнорим сертефикаты всегда
             if(GlobalVar.newminecraftisfml){
                params.add("net.minecraft.launchwrapper.Launch");  
                params.add("--tweakClass");
                params.add("cpw.mods.fml.common.launcher.FMLTweaker"); 
             }else
             {
                params.add("net.minecraft.client.main.Main"); 
             }
             params.add("--username");
             params.add(GlobalVar.userName);
             if (Online)
             {
                params.add("--session");
                params.add(GlobalVar.sessionId);
             }
             params.add("--version");
             params.add("1.6.2"/*GlobalVar.Version*/);
             params.add("--gameDir");
             params.add(ClientFolderPath);
             params.add("--assetsDir");
             params.add(ClientFolderPath+"assets");
        }
        
        
        File log = new File(ClientFolderPath+"game.log");
        
        ProcessBuilder pb = new ProcessBuilder(params);
        //Map<String, String> env = pb.environment();
        pb.environment().clear();
        if(Utils.getPlatform()==Utils.OS.windows)
        {
            pb.environment().put("PATH", ClientFolderPath+"bin"+ File.separator+"natives");
            pb.environment().put("APPDATA", AppDataPath);  //То что надо
        }else if (Utils.getPlatform()==Utils.OS.linux) {  //Unix like
            pb.environment().put("LD_LIBRARY_PATH", ClientFolderPath+"bin"+ File.separator+"natives");
            pb.environment().put("user.home", AppDataPath);
        }else if(Utils.getPlatform()==Utils.OS.macos){
            pb.environment().put("DYLD_LIBRARY_PATH", ClientFolderPath+"bin"+ File.separator+"natives");
            pb.environment().put("user.home", AppDataPath);
        }
        pb.directory(new File(ClientFolderPath));
        pb.redirectErrorStream(true);
        pb.redirectOutput(log);     
    try {
        pb.start();
        //Process process = pb.start();
    } catch (IOException ex) {
        Logger.getLogger(MCGameRuner.class.getName()).log(Level.SEVERE, null, ex);
    }
} 
}
