/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jmc.api;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


/**
 *
 * @author DimanA90
 */
public class archiveMan {
    
    private static String[] Errors = null;
    /**
     * Get array of error files
     * @return String[]
     */
    public static String[] GetErrors(){
        return Errors;
    }
    /**
     * Unzip it
     * @param zipFile input zip file
     * @param output zip file output folder
     * @return true if no errors or false if get errors
     */
    public static boolean unZip(String zipFile, String outputFolder){
        byte[] buffer = new byte[1024];
        boolean status = false;
        Errors = null;
        File outPutFolder = null;
        ZipInputStream zis = null;
        FileOutputStream outputFileStream = null;
        
        try{
           outPutFolder = new File(outputFolder);
           if(!outPutFolder.exists()){
                   outPutFolder.mkdirs();
           }
           //get the zip file content
           zis = new ZipInputStream(new FileInputStream(zipFile));
           //get the zipped file list entry
           ZipEntry ze = zis.getNextEntry();

           while(ze!=null)
           {
              try{
                    String fileName = ze.getName();
                    File newFile = new File(outputFolder + File.separator + fileName);
                     //create all non exists folders
                     //else you will hit FileNotFoundException for compressed folder
                     new File(newFile.getParent()).mkdirs();

                     outputFileStream = new FileOutputStream(newFile);             
                     int len;
                     while ((len = zis.read(buffer)) > 0) {
                         outputFileStream.write(buffer, 0, len);
                     }
                     ze = zis.getNextEntry();
              }catch(SecurityException se){ //for new File | new FileOutputStream
                    Errors[Errors.length+1] = ze.getName();
              }catch(IOException ioe){
                    Errors[Errors.length+1] = ze.getName();
              }finally{
                  if(outputFileStream != null)outputFileStream.close();  
              }
           }
       }catch(IOException ex){
          ex.printStackTrace(); 
       }finally{
            try{
                if(zis != null)
                {
                    zis.closeEntry();
                    zis.close();
                    outPutFolder = null;
                    
                    status = true;  //No errors
                }
            }catch(IOException e){
                
            }
        }
      return status;
    }    
}
