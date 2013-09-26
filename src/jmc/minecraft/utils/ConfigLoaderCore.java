/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jmc.minecraft.utils;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;

import java.io.InputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLDecoder;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author DimanA90
 */
/*
 * Load core config for launcher
 */

public class ConfigLoaderCore  {
  
public void saveUserConfig()
{      
JSONObject obj = new JSONObject();
	obj.put("UserName", GlobalVar.userName);
        obj.put("Password", GlobalVar.password);
	obj.put("LastClient", GlobalVar.CurrentServer);
	try {
 
		FileWriter file = new FileWriter(new File(URLDecoder.decode(Utils.getWorkingDirectory()+File.separator+"UserInfo.json","UTF-8")));
		file.write(obj.toJSONString());
		file.flush();
		file.close();
 
	} catch (IOException e) {
		//Utils.LogPrint(e);
	}
}
/*
 * Load user config for launcher
 */    
public void LoadUserConfig()
{
JSONParser parser = new JSONParser();
	try {
                InputStream in = new FileInputStream(new File(URLDecoder.decode(Utils.getWorkingDirectory()+File.separator+"UserInfo.json","UTF-8")));
		Object obj = parser.parse(new InputStreamReader(in));
		JSONObject jsonObject = (JSONObject) obj;
 
                GlobalVar.userName = (String) jsonObject.get("UserName");
                GlobalVar.password = (String) jsonObject.get("Password");
                GlobalVar.setCurrentServer((long) jsonObject.get("LastClient"));

	} catch (IOException e) {
		//Utils.LogPrint(e);
	} catch (ParseException e) {
		Utils.LogPrint(e);
	} 
}
public void saveCurrentClientConfig()
{      
JSONObject obj = new JSONObject();
	obj.put("JavaPath", GlobalVar.JavaPath);
        obj.put("VMArg", GlobalVar.JavaVMArg);
	obj.put("ExpertMode", GlobalVar.ExpertSettings);
	try {
 
		FileWriter file = new FileWriter(new File(Utils.GetCurrentClientDir() + File.separator, "config.json"));
		file.write(obj.toJSONString());
		file.flush();
		file.close();
 
	} catch (IOException e) {
		Utils.LogPrint(e);
	}
}
public void loadCurrentClientConfig()
{ 
JSONParser parser = new JSONParser();
	try {
                InputStream in = new FileInputStream(new File(Utils.GetCurrentClientDir() + File.separator, "config.json"));
		Object obj = parser.parse(new InputStreamReader(in));
		JSONObject jsonObject = (JSONObject) obj;
 
                GlobalVar.JavaPath = (String) jsonObject.get("JavaPath");
                GlobalVar.JavaVMArg = (String) jsonObject.get("VMArg");
                GlobalVar.ExpertSettings = (boolean) jsonObject.get("ExpertMode");
                System.out.println(GlobalVar.JavaPath);
                System.out.println(GlobalVar.JavaVMArg);

	} catch (IOException e) {
		//Utils.LogPrint(e);
                GlobalVar.ExpertSettings = false;
	} catch (ParseException e) {
		Utils.LogPrint(e);
                GlobalVar.ExpertSettings = false;
	}     
}   


   /*
    * Load current client info
    */ 
public void LoadClientConfig(String clientName)
{

       JSONParser parser = new JSONParser();
	try {
                InputStream in = this.getClass().getResourceAsStream("/Configs/clientinfo/"+clientName+".json");
		Object obj = parser.parse(new InputStreamReader(in));
		JSONObject jsonObject = (JSONObject) obj;
 
                GlobalVar.oldminecarft =(boolean) jsonObject.get("RunAsOlderMinecraft");
                GlobalVar.newminecraftisfml = (boolean) jsonObject.get("RunMinecraftAsFML16x");
                GlobalVar.clientinfo = (String) jsonObject.get("info");
                    JSONArray ZipListTemp = (JSONArray) jsonObject.get("DownloadZipList");
                    GlobalVar.ArchivesList = new String[ZipListTemp.size()];  //Init array size
                ZipListTemp.toArray(GlobalVar.ArchivesList);  //Puts strings array

	} catch (IOException e) {
		Utils.LogPrint("Load client info config error",e.getMessage());
	} catch (ParseException e) {
		Utils.LogPrint("Parse client info config error",e.getMessage());
	} 
}

public void LoadCoreConfig()
{
JSONParser parser = new JSONParser();
	try {
                InputStream in = this.getClass().getResourceAsStream("/Configs/CoreConfig.json");
		Object obj = parser.parse(new InputStreamReader(in));
		JSONObject jsonObject = (JSONObject) obj;
 
                GlobalVar.Version = (String) jsonObject.get("Version");
                GlobalVar.WorkDir = (String) jsonObject.get("LauncherRootDir");
                GlobalVar.LauncherFileName = (String) jsonObject.get("LauncherFileName");
                GlobalVar.MainWndTitle = (String) jsonObject.get("WindowTitle");
                GlobalVar.HostUrl = (String) jsonObject.get("HostUrl");
                GlobalVar.NewsURL = (String) jsonObject.get("NewsUrl");
                    JSONArray ClNametemp = (JSONArray) jsonObject.get("clientnames");
                    GlobalVar.ClientNames = new String[ClNametemp.size()];  //Init array size
                ClNametemp.toArray(GlobalVar.ClientNames);  //Puts strings array
                    JSONArray ClDirNametemp = (JSONArray) jsonObject.get("clientdirnames");
                    GlobalVar.itemsServers = new String[ClDirNametemp.size()]; //Init array size
                ClDirNametemp.toArray(GlobalVar.itemsServers); //Puts strings array
                GlobalVar.AuthURL = (String) jsonObject.get("AuthUrl");
                GlobalVar.RegURL = (String) jsonObject.get("RegUrl"); 
                GlobalVar.DownloadClientRootURL = (String) jsonObject.get("ClientDownloadRootFolder");

	} catch (IOException e) {
		Utils.LogPrint(e);
	} catch (ParseException e) {
		Utils.LogPrint(e);
	} 
}
public boolean LoadModeConfig()
{
        JSONParser parser = new JSONParser();
	try {
            String path = ConfigLoaderCore.class.getProtectionDomain().getCodeSource().getLocation().getPath();
                path = path.substring(0, path.length()-4)+".json";
                File filetemp = new File(URLDecoder.decode(path,"UTF-8"));
                if (filetemp.exists()){
                InputStream in = new FileInputStream(filetemp);
		Object obj = parser.parse(new InputStreamReader(in));
		JSONObject jsonObject = (JSONObject) obj;
 
                GlobalVar.LauncherStandalonePath = (String) jsonObject.get("InstallPath");
                GlobalVar.LauncherMode = (String) jsonObject.get("LauncherMode");
                }
                
             return true;
	} catch (IOException e) {
		//Utils.LogPrint(e);
                return false;
	} catch (ParseException e) {
		Utils.LogPrint(e);
                return false;
	} 
}
public void saveModeConfig()
{      
JSONObject obj = new JSONObject();
        obj.put("InstallPath", GlobalVar.LauncherStandalonePath);
        obj.put("LauncherMode", GlobalVar.LauncherMode);
	try 
        {
                String path = ConfigLoaderCore.class.getProtectionDomain().getCodeSource().getLocation().getPath();
                path = path.substring(0, path.length()-4)+".json";
    File filetemp = new File(URLDecoder.decode(path,"UTF-8"));
		FileWriter file = new FileWriter(filetemp);
		file.write(obj.toJSONString());
		file.flush();
		file.close();
 
	} catch (IOException e) 
        {
		Utils.LogPrint("preloaderconfwriter error", e.getMessage());
	}
}

}
