/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jmc.api;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author DimanA90
 */
public class configMan {
        //private Hashtable<String,Object> props = new Hashtable<>();
        public JSONObject props = null;
        private boolean debug = false;
        
        public void configMan(){
            this.props = new JSONObject();
        }
  /**
  * <p>Create or update parametr</p>
  * Create pair parametr, if parametr exist then update parametr
  * Example setProperties("name","Dmitriy")
  * @param key Key of pair, string type
  * @param Value Value of pair, string type
  */
     public void setProperty(String key,Object Value){
            if(debug)System.out.println("ConfigMan:SetProperty("+key+","+Value.toString()+")");
         this.props.put(key, Value);
     }
     public Object GetProperty(String key){
         if(debug)System.out.println("ConfigMan:GetProperty("+key+")");
         return this.props.get(key);
     }
     public void Clear(){
         if(debug)System.out.println("ConfigMan:Clear()");
         this.props.clear();
     }
     public void Remove(String key){
         if(debug)System.out.println("ConfigMan:Remove("+key+")");
         this.props.remove(key);
     }
     public void SetDebug(boolean stat){
         this.debug = stat;
     }
     @Override
     public String toString(){
         if(debug)System.out.println("ConfigMan:toString()");
         return props.toJSONString();
     }
     public void SaveToJsonFile(String path) throws IOException ,UnsupportedEncodingException{
                SaveToJsonFile(new File(URLDecoder.decode(path,"UTF-8")));
     }
     public void SaveToJsonFile(File file) throws IOException{
        if(debug)System.out.println("ConfigMan:SaveToJsonFile("+file.getAbsolutePath()+")");
        if ((!file.exists()) && (!file.mkdirs())) throw new RuntimeException("Path to file could not be created: " + file);
            FileWriter filewr = new FileWriter(file);
	try {
                filewr.write(props.toJSONString());
	} catch (IOException e) {
		throw new IOException("IO error while save file"+file.getAbsolutePath());
	} finally{
                    filewr.flush();
                    filewr.close();
        }
     }
     public boolean LoadFromJsonFile(String path) throws UnsupportedEncodingException{
         if(LoadFromJsonFile(new File(URLDecoder.decode(path,"UTF-8")))){
             return true;
         }else return false;
     }
     public boolean LoadFromJsonFile(File file){
         if(debug)System.out.println("ConfigMan:LoadFromJsonFile("+file.getAbsolutePath()+")");
         if(file.isFile())
         {
            JSONParser parser = new JSONParser();
            this.props.clear();
            try {
                InputStream in = new FileInputStream(file);
                Object obj = parser.parse(new InputStreamReader(in));
		JSONObject jsonObject = (JSONObject) obj;
                this.props = jsonObject;
                in.close();
                obj = null;
                jsonObject = null;
                return true;

            } catch ( IOException | ParseException e) {
		return false;
            } 
         } else return false;
     }
}
