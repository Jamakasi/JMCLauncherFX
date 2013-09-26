/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jmclauncherfx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import jmc.minecraft.utils.ConfigLoaderCore;
import jmc.minecraft.utils.GlobalVar;
import jmc.minecraft.utils.Utils;

/**
 *
 * @author DimanA90
 */
public class JMCLauncherFX extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        
        
        /*
         * Load main configs
         */
        ConfigLoaderCore cf = new ConfigLoaderCore();
        
        cf.LoadCoreConfig();  //Load core properties 
        /*
         * Check online connect
         */
        if(!Utils.isOnline()){
                    GlobalVar.isOnline = false;
        }else {
            GlobalVar.isOnline = true;
            //Проверяем обновление и качаем если надо
                    jmc.minecraft.utils.LauncherUpdater.UpdateLauncher();
        }
        
        
        
        
        cf.LoadUserConfig(); //Load user config
        cf.LoadClientConfig(GlobalVar.itemsServers[GlobalVar.CurrentServer]);//Load first info about client
        
                
        /*
         * 
         */
        Parent root = FXMLLoader.load(getClass().getResource("MainForm.fxml"));
        Scene scene = new Scene(root);
        stage.setTitle(GlobalVar.MainWndTitle);
        stage.setScene(scene);
        
        stage.show();
        
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
        
    }
}