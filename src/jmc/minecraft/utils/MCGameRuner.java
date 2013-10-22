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

protected ArrayList<String> ParseGameArguments(){
    String sartemp[] = GlobalVar.minecraftArguments.split(" ");
    String word = new String();
    ArrayList<String> arltemp = new ArrayList<String>();
    
    if(GlobalVar.ExpertSettings){
            Utils.LogPrintConsole("Использован сторонний путь до java: "+GlobalVar.JavaPath);
            Utils.LogPrintConsole("Использованы дополнительные параметры запуска java: "+GlobalVar.JavaVMArg);
            arltemp.add(GlobalVar.JavaPath);
            arltemp.add(GlobalVar.JavaVMArg);
        }else
        {
            Utils.LogPrintConsole("Подбор оптимальных опций ява машины:");
            arltemp.add("java");
            arltemp.add("-Xincgc");
            Utils.LogPrintConsole("Улучшение чистки памяти -Xincgc");
            long Ram = Runtime.getRuntime().maxMemory()/1024L/1024L;
            if (Ram >=1000L){
                Utils.LogPrintConsole("Возможно выделить более 1гб памяти, принудительное выставление -Xms1G");
                arltemp.add("-Xms1G");
            }else{
                if(Ram<512L){
                    Utils.LogPrintConsole("В вашей системе слишком мало свободной памяти,");
                    Utils.LogPrintConsole("для плавной игры рекомендуется не менее 512 мб свободной памяти. Выделено:"+Ram+"Мб");
                    Utils.LogPrintConsole("Рекомендуется закрыть все сторонние программы или воспользоваться расширенными настройками ява машины.");
                }
                Utils.LogPrintConsole("-Xmx"+Ram+"m");
                Utils.LogPrintConsole("-Xms"+Ram+"m");
                arltemp.add("-Xmx"+Ram+"m");
                arltemp.add("-Xms"+Ram+"m");
            }
        }
        arltemp.add("-cp");
        arltemp.add(ClientFolderPath+"bin"+ File.separator+"*");
        arltemp.add("-Dfml.ignoreInvalidMinecraftCertificates=true");
        arltemp.add("-Dfml.ignorePatchDiscrepancies=true");
        arltemp.add(GlobalVar.mainClass);
        
    for (String s : sartemp) 
    {  
        switch (s){
            case "${auth_player_name}":{  arltemp.add(GlobalVar.userName); break;}
            case "${auth_session}":  {arltemp.add(GlobalVar.sessionId);break;}
            case "${version_name}":  {arltemp.add(GlobalVar.id);break;}
            case "${game_directory}":  {arltemp.add(ClientFolderPath);break;}
            case "${game_assets}":  {arltemp.add(ClientFolderPath+"assets");break;}
            default: {arltemp.add(s);break;}
        }
    }
    
Utils.LogPrintConsole(arltemp.toString());
    return arltemp;
}
public void LetsGame(boolean Online)
{
Utils.LogPrintConsoleHead("Запускаю игру");
ArrayList<String> params = ParseGameArguments();


        File log = new File(ClientFolderPath+"game.log");
        
        ProcessBuilder pb = new ProcessBuilder(params);
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
        Process process = pb.start();
        if (process == null){
            Utils.LogPrintConsoleHead("Не удалось запустить игру =(");
            if(GlobalVar.ExpertSettings){
                Utils.LogPrintConsole("Вы используете расширенные настройки!");
                Utils.LogPrintConsole("Возможно указаны неправильные параметры запуска ява машины!");
            }else
            Utils.LogPrintConsole("Возможно выделено неправильное количество памяти");
        }else
        Utils.LogPrintConsoleHead("Теперь лаунчер можно закрыть");
        
    } catch (IOException ex) {
        Utils.LogPrintConsoleHead("Произошла серьезная ошибка при запуске игры. Игра не запущена либо запущена с ошибками");
        Logger.getLogger(MCGameRuner.class.getName()).log(Level.SEVERE, null, ex);
    }
} 
}
