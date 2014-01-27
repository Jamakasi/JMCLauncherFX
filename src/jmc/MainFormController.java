/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jmc;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;

import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import jmc.api.configMan;


/**
 *
 * @author DimanA90
 */
public class MainFormController implements Initializable {
    
    @FXML

    public WebView WebNews;  // web for show news
    public WebView WebClientinfo;
    public ComboBox ComboBoxClients;  // combo box with clients list
    public TextField UsernameField;  //Field with user name
    public PasswordField UserpassField; //Field with user password
    public CheckBox CheckBoxSavelogin;
    public Button ButtonLogin;
    public Button ButtonSetting;
    
    //Window two
    public ProgressBar ProgressCurrent = new ProgressBar();
    public ProgressBar ProgressGlobal;
    public TextArea EditFieldLog;
    
    public AnchorPane MainPane;
    public AnchorPane PaneUpdater;
    
    public configMan launcherConf;    
    
    @FXML
    /*
     * Open registration link
     */
    public void LabelRegistrationOpenURL(){
        try {
              Utils.openLink(new URL(GlobalVar.RegURL).toURI());
            } catch (Exception e) {
              Utils.LogPrint("web", "Error open link to registration");
            }
    }
    /*
     * Get username from username field
     */
    public void GetUsername(){
        GlobalVar.userName =  UsernameField.getText();
        GlobalVar.password = UserpassField.getText();
    }
    /*
     * Set bool state remember username and pass
     */
    public void SetCheckboxRemember(){
        //GlobalVar.isRememberUserNamePass = CheckBoxSavelogin.isSelected();
    }
    /*
     * Get index from combobox with client names and loaf config info about client
     */
    public void SetClientIdFromCombo(){
        GlobalVar.CurrentServer = ComboBoxClients.getSelectionModel().getSelectedIndex();
        ConfigLoaderCore cfs = new ConfigLoaderCore();
        cfs.LoadClientConfig(GlobalVar.itemsServers[GlobalVar.CurrentServer]);
        
        WebEngine webEngineClientInfo = WebClientinfo.getEngine();
        
        
        if(GlobalVar.isOnline)
        {
           webEngineClientInfo.load(GlobalVar.DownloadClientRootURL+"/"+GlobalVar.itemsServers[GlobalVar.CurrentServer]+"/"+"info.html");
        }else
        {
            try 
            {
                    StringBuffer sb = new StringBuffer();
                    BufferedReader br = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream("/Configs/clientinfo/"+GlobalVar.itemsServers[GlobalVar.CurrentServer]+".html"), "UTF-8"));
                    for (int c = br.read(); c != -1; c = br.read()) sb.append((char)c);
                webEngineClientInfo.loadContent(sb.toString());
            }catch(Exception e)
            {
                webEngineClientInfo.loadContent("<html><body text=\"#333333\" bgcolor=\"#cccccc\" ><center><p><font size=\"5\">ERROR LOADING INFO</font></p></center></body></html>");
            }
        }
    }
    /*
     * Run updater/ run game button
     */
    public void ButtonRunGamePressed(){
        ConfigLoaderCore cfg = new ConfigLoaderCore();
        cfg.loadCurrentClientConfig(); // На всякий случай
        GetUsername();  //Берем актуальные данные
        FormStageTwo();
    }
    private void FormStageTwo()
    {
        GlobalVar.isRememberUserNamePass = CheckBoxSavelogin.isSelected(); //Стоит ли галка о запоминании данных
        if(GlobalVar.isOnline)// is Online?
        {
            if(Utils.login(GlobalVar.userName, GlobalVar.password))//Login succes ?
            {
               MainPane.setVisible(false);     //Скрываем основную форму
               PaneUpdater.setVisible(true);   //Показываем форму загрузки\запуска
               RunGame.Init(ProgressCurrent,ProgressGlobal); //Запускаем обновление, потом игру
            }else
            {//Ничего не делаем
         
            };         
        }else  //Offline, run game offline
        {
               MainPane.setVisible(false);      //Скрываем основную форму
               PaneUpdater.setVisible(true);    //Показываем форму загрузки\запуска
               ProgressCurrent.setProgress(-0.1);  //Устанавливаем думалку
               ProgressGlobal.setProgress(-0.1);    //Устанавливаем думалку
               GlobalVar.sessionId = "000000";
                MCGameRuner grun = new MCGameRuner();
                grun.LetsGame(false);
                ProgressCurrent.setVisible(false); //Скрываем думалку т.к. игрок может и не закрыть форму
                ProgressGlobal.setVisible(false);   //Скрываем думалку т.к. игрок может и не закрыть форму
        }
    }
    public void ButtonSettingsPressed(){
        //Тут должно быть открытие настроек
        try{
        Stage stageopt = new Stage(StageStyle.UTILITY);
        Parent opt = FXMLLoader.load(getClass().getResource("OptionsWindow.fxml"));
        Scene scene = new Scene(opt);
        stageopt.setTitle("Настройки");
        stageopt.setScene(scene);
        stageopt.initModality(Modality.APPLICATION_MODAL);
        stageopt.showAndWait();
        
        //stageopt.show();
        }catch(Exception e){
            
        }
    }
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        launcherConf = new configMan();
        launcherConf.LoadFromJsonFile("");
        WebEngine webEngineNews = WebNews.getEngine(); 
        if(GlobalVar.isOnline){
        webEngineNews.load(GlobalVar.NewsURL);
        }else  webEngineNews.loadContent("<html><body text=\\\"#333333\\\" bgcolor=\"\\#cccccc\\\" ><center><p><font size=\"5\">Offline</font></p></center></body></html>");
        ObservableList<String> ClientNames = FXCollections.observableArrayList(GlobalVar.ClientNames);
        ComboBoxClients.setItems(ClientNames);
        ComboBoxClients.getSelectionModel().select(GlobalVar.CurrentServer);
        UsernameField.setText(GlobalVar.userName);
        UserpassField.setText(GlobalVar.password);
        
    }    
}
