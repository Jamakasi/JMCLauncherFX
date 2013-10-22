/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jmclauncherfx;


import javafx.scene.control.ProgressBar;
import jmc.minecraft.utils.ConfigLoaderCore;
import jmc.minecraft.utils.GlobalVar;
import jmc.minecraft.utils.MCGameRuner;
import jmc.minecraft.utils.Updater;


/**
 * 
 * @author DimanA90
 */
public class RunGame {
   
public static void Init(final ProgressBar progressCurrent,final ProgressBar progressBarTotal)
 {

        Thread myThready = new Thread(new Runnable()
        {
            public void run()
            {
            if(GlobalVar.isRememberUserNamePass)
            {
                ConfigLoaderCore cfsaver = new ConfigLoaderCore();
                cfsaver.saveUserConfig();
            }
            Updater gup = new Updater();
            gup.Init(progressCurrent,progressBarTotal);
                MCGameRuner grun = new MCGameRuner();
                grun.LetsGame(true);
            }
        });
        myThready.start();
         
     
 }
}
