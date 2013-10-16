/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jmclauncherfx;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.swing.JOptionPane;
import jmc.minecraft.utils.ConfigLoaderCore;
import jmc.minecraft.utils.GlobalVar;
import jmc.minecraft.utils.Utils;


/**
 * FXML Controller class
 *
 * @author DimanA90
 */
public class OptionsWindowController implements Initializable {

    public Button ButtonOpenClientFolder;
    public Button ButtonForceUpdate;
    public Button ButtonBrowse;   //Куда ставить майн
    public RadioButton RadioDefault;
    public RadioButton RadioPortable;
    public RadioButton RadioInstall;
    public CheckBox CheckBoxJavaSetting;
    final FileChooser fileChooser = new FileChooser();
    final DirectoryChooser dirchoser = new DirectoryChooser();
    public Label LablePath;
    public TextField EditFieldJavaPath;
    public TextField EditFieldArg;
    public Button ButtonSaveJavaSetting;
    public Button ButtonSelectJava;
    
    /*
     * Открытие папки с клиентами
     */
public void BrowseClientFolder(){
        try{
        Utils.openLink(new URL("file://" + Utils.GetCurrentClientDir() + File.separator).toURI() );
        }catch(Exception e){
            Utils.LogPrint("Error obrowse client folder");
        }
    }
    /*
     * Принудительное обновление текущего клиента
     */
public void ButtonForseUpdate(){
        Utils.ForseUpdate();
    }
public void ButtonBrowsePathToInstall(){
        dirchoser.setTitle("Выберите папку куда будут станавливаться все клиенты");
    
        File file = dirchoser.showDialog(new Stage());
        if (file != null) 
                    {
                        try {
                            GlobalVar.LauncherStandalonePath = file.getAbsolutePath()+File.separator;  //Заносим путь в глобалку
                            GlobalVar.LauncherMode = "installed";
                            ConfigLoaderCore cf = new ConfigLoaderCore();
                            //cf.saveModeConfig();
                            LablePath.setText(file.getAbsolutePath());
                            ShowInfoToRestart();
                            } catch (Exception ex) {
                            Utils.LogPrint("problem accessing folder"+file.getAbsolutePath());
                            }
                    }
    }
public void RadioDefault(){
        DeleteModeConf();
            LablePath.setVisible(false);
            ButtonBrowse.setVisible(false);
            RadioPortable.setSelected(false);
            RadioInstall.setSelected(false);
        ShowInfoToRestart(); 
}
public void RadioPortable(){
     GlobalVar.LauncherMode = "portable";
        ConfigLoaderCore cf = new ConfigLoaderCore();
            cf.saveModeConfig();
            LablePath.setVisible(false);
            ButtonBrowse.setVisible(false);
            RadioDefault.setSelected(false);
            RadioInstall.setSelected(false);
        ShowInfoToRestart();
}
public void RadioInstall(){
            LablePath.setVisible(true);
            ButtonBrowse.setVisible(true);
            RadioDefault.setSelected(false);
            RadioPortable.setSelected(false);
}

private void DeleteModeConf(){
   String path = ConfigLoaderCore.class.getProtectionDomain().getCodeSource().getLocation().getPath();
                path = path.substring(0, path.length()-4)+".json";
        File file;
        try {
            file = new File(URLDecoder.decode(path,"UTF-8"));
            Utils.LogPrint(URLDecoder.decode(path,"UTF-8"));
            file.delete();
        } catch (UnsupportedEncodingException ex) {
            //Logger.getLogger(OptionsWindowController.class.getName()).log(Level.SEVERE, null, ex);
        }
}
private void ShowInfoToRestart(){
    JOptionPane.showMessageDialog( null,
                    "Лаунчер будет закрыт для применения новых настроек.",
                    "Применение настроек",
                    JOptionPane.WARNING_MESSAGE);

    System.exit(0);
}
public void SaveJavaConfig(){
        GlobalVar.ExpertSettings = CheckBoxJavaSetting.isSelected();
        GlobalVar.JavaPath = EditFieldJavaPath.getText();
        GlobalVar.JavaVMArg = EditFieldArg.getText();
        ConfigLoaderCore cfg = new ConfigLoaderCore();
        cfg.saveCurrentClientConfig();
}
public void CheckBoxJavaSetting(){
    if(CheckBoxJavaSetting.isSelected()){
            JOptionPane.showMessageDialog( null,
                    "Используйте эти настройки только если точно знаете что делаете!",
                    "!!! ПРЕДУПРЕЖДЕНИЕ !!!",
                    JOptionPane.WARNING_MESSAGE);
        }else{
        GlobalVar.ExpertSettings = CheckBoxJavaSetting.isSelected();
        /*ConfigLoaderCore cfg = new ConfigLoaderCore();
        cfg.saveCurrentClientConfig();*/
        SaveJavaConfig();
        }
        EditFieldJavaPath.setDisable(!CheckBoxJavaSetting.isSelected());
        EditFieldArg.setDisable(!CheckBoxJavaSetting.isSelected());
        ButtonSelectJava.setDisable(!CheckBoxJavaSetting.isSelected());
        ButtonSaveJavaSetting.setDisable(!CheckBoxJavaSetting.isSelected());
}
public void ButtonBrowseJavaPath(){
    fileChooser.setTitle("Выберите исполняемый файл ява машины для текущего клиента");
    
        File file = fileChooser.showOpenDialog(new Stage());
        if (file != null) 
                    {
                        try {
                            EditFieldJavaPath.setText(file.getAbsolutePath());   //Заносим путь в глобалку
                            
                            } catch (Exception ex) {
                            Utils.LogPrint("problem accessing select java bin file"+file.getAbsolutePath());
                            }
                    }
    }
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        ConfigLoaderCore cf = new ConfigLoaderCore();
        cf.LoadModeConfig();
        if(GlobalVar.LauncherMode.contains("default")){
            RadioDefault.setSelected(true);
            LablePath.setVisible(false);
            ButtonBrowse.setVisible(false);
        }else if(GlobalVar.LauncherMode.contains("portable")){
            RadioPortable.setSelected(true);
            LablePath.setVisible(false);
            ButtonBrowse.setVisible(false);
        }else if(GlobalVar.LauncherMode.contains("installed")){
            RadioInstall.setSelected(true);
            LablePath.setVisible(true);
            LablePath.setText("Установлено в:"+GlobalVar.LauncherStandalonePath);
            ButtonBrowse.setVisible(true);
        }
        
        //Page two
        CheckBoxJavaSetting.setSelected(GlobalVar.ExpertSettings);
        EditFieldJavaPath.setDisable(!CheckBoxJavaSetting.isSelected());
        EditFieldArg.setDisable(!CheckBoxJavaSetting.isSelected());
        ButtonSelectJava.setDisable(!CheckBoxJavaSetting.isSelected());
        ButtonSaveJavaSetting.setDisable(!CheckBoxJavaSetting.isSelected());
        EditFieldJavaPath.setText(GlobalVar.JavaPath);
        EditFieldArg.setText(GlobalVar.JavaVMArg);
    }    
}
