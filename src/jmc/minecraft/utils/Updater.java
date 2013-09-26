/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jmc.minecraft.utils;

import java.io.EOFException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javafx.scene.control.ProgressBar;

import javax.swing.JProgressBar;

import static jmc.minecraft.utils.Utils.getWorkingDirectory;

/**
 *
 * @author DimanA90
 */
public class Updater {
     /*
     * 1) Генерируем список файлов на загрузку. Сверяем мд5 архива у клиента и на сервере, не совпал добаляем в filelist
     * 2) Размер файлов
     * 3) Качаем файлы
     * 4) Удаляем необходимые директории относительно списка загрузок
     * 5) Распаковываем архивы по filelist

     */
private String[] FileList = new String[255];  //File list Список архивов
private long[] FileSize = new long[255];
private URL path ; //Url path to current client on server УРЛ к папке с файлами клиента на сервере
private URL[] URLList = new URL[255]; //Full url list Полный путь до каждого архива
private int FileCount = 0; 
private String ClientFolderPath; //Full path to curr client Полный путь до текущего клиента

private static long totalsize = 0;
private static double CurentTotalProgress =0.0;

public void Init(ProgressBar progressCurr,ProgressBar progressBarTotal)
{
    
    GenerateFileList();  //1
    

    DownloadFilesSize(); //2
    

    DownloadFiles(progressCurr,progressBarTotal);     //3
    

    UnpackArchives(progressCurr,progressBarTotal);    //5
}
/*
 * 1) Генерируем список файлов на загрузку. Сверяем мд5 архива у клиента и на сервере, не совпал добаляем в filelist
 */
private void GenerateFileList()
{
    //ClientFolderPath = Utils.getWorkingDirectory() + File.separator + GlobalVar.itemsServers[GlobalVar.CurrentServer] + File.separator;
    ClientFolderPath = Utils.GetCurrentClientDir() + File.separator + ".minecraft" + File.separator;
    
    File dir = new File(ClientFolderPath);
        if (!dir.exists()) 
                  {
                    dir.mkdirs();
                  }
    
                   if(GlobalVar.debug) System.out.println("Client root folder: "+ClientFolderPath);
    try {
        path = new URL((GlobalVar.DownloadClientRootURL+GlobalVar.itemsServers[GlobalVar.CurrentServer])+ '/');
                   if(GlobalVar.debug) System.out.println("Download root: "+path.toString());
        } catch (MalformedURLException ex) {
            Logger.getLogger(Updater.class.getName()).log(Level.SEVERE, null, ex);
        }
    mdsummcheker mdchk = new mdsummcheker();

    for(int i=0;i<GlobalVar.ArchivesList.length;i++)
    {
        if(!mdchk.CheckMD5Matches(GlobalVar.ArchivesList[i]))
        {
            try 
            {
                FileList[FileCount]=GlobalVar.ArchivesList[i];
                URLList[FileCount] = new URL(path,FileList[FileCount]);
                FileCount++;
            } catch (MalformedURLException ex) 
            {
                //System.out.println("Исключение на итерации "+ FileCount + "\nFile name = "+FileList[FileCount]);
                Logger.getLogger(Updater.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception e)
            {
                System.out.println("Error on generate links "+e);
            }
          
        }
    }
}
/*
 * 2) Размер файлов
 */
protected void DownloadFilesSize()
{
    for(int i=0;i<FileCount;i++)
    {
        long size = (Utils.downloadFilesSize(URLList[i].toString()))*2/1024;
        totalsize +=size;
        FileSize[i] = size/2;
    }
}
/*
 * 3) Качаем файлы
 */
protected void DownloadFiles(ProgressBar progressCurr,ProgressBar progressBarTotal)
{
    //System.out.println(FileCount);
    for(int i=0;i<FileCount;i++)
    {
        System.out.println(i+" Качаю "+ URLList[i].toString()+"\n"+ClientFolderPath);
        downloadFiles(URLList[i].toString(), ClientFolderPath,FileList[i], 65536,FileSize[i], progressCurr, progressBarTotal);
    }
}
/*
 * 4) Удаляем необходимые директории относительно списка загрузок
 * УПС
 */

/*
 * 5) Распаковываем архивы по filelist
 */
protected void UnpackArchives(ProgressBar progressCurr,ProgressBar progressBarTotal)
{
    for(int i=0;i<FileCount;i++)
    {
        System.out.println(i+" Unzip "+ FileList[i]);
        try {
                   Utils.deleteDirectory(Utils.GetCurrentClientDir()+ File.separator+".minecraft"+ File.separator+FileList[i].substring(0, FileList[i].length()-4));   //Рекурсивно вычищаем ее
            UnZip(FileList[i],FileSize[i],progressCurr,progressBarTotal);
        } catch (PrivilegedActionException ex) {
            Logger.getLogger(Updater.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}


private void downloadFiles(String strURL, String strPath, String filename, int buffSize, long fileSize, ProgressBar progressCurr,ProgressBar progressBarTotal ) {
        try {
            progressCurr.setProgress(0);
            
            URL connection = new URL(strURL);
            HttpURLConnection urlconn;
            urlconn = (HttpURLConnection) connection.openConnection();
            urlconn.setRequestMethod("GET");
            urlconn.connect();
            InputStream in = null;
            in = urlconn.getInputStream();
            OutputStream writer = new FileOutputStream(strPath+filename);
            byte buffer[] = new byte[buffSize];
            int c = in.read(buffer);
            int dwnloaded =0;
            double oldpercent = 0.0;
            while (c > 0) {
                writer.write(buffer, 0, c);
                dwnloaded +=c;
                try{
                progressCurr.setProgress((double)((dwnloaded/1024)*100/fileSize)/100);
                //progressCurr.setString("Скачивается "+filename + "  "+progressCurr.getProgress()+"%  " +(dwnloaded/1048576)+"/"+(fileSize/1024)+"Mb");
                  CurentTotalProgress += ((dwnloaded/1024)*100/totalsize)-oldpercent ;
                progressBarTotal.setProgress(CurentTotalProgress/100);
                    oldpercent = (dwnloaded/1024)*100/totalsize;
                }catch(ArithmeticException ae){
                   //И такое тоже бывает
                }
                c = in.read(buffer);
            }
            writer.flush();
            writer.close();
            in.close();
        } catch (IOException e) {
            System.out.println("Неведомая хуйня при загрузке файла в "+ strPath+filename);
            System.out.println(e);
        }
}

/**
 * Разархивирует файл client.zip из папки bin в .minecraft
 * @author ddark008
 * @throws PrivilegedActionException
 */
private static void UnZip(String ZipName,long fileSize, ProgressBar progressCurr,ProgressBar progressBarTotal) throws PrivilegedActionException
  {
     
    String szZipFilePath;
    String szExtractPath;
    String path = (String)AccessController.doPrivileged(new PrivilegedExceptionAction<Object>() {
        public Object run() throws Exception {
          return Utils.GetCurrentClientDir() + File.separator + ".minecraft" + File.separator;
        }
      }); 
    int i;
    
    szZipFilePath = path + ZipName;
      
    File f = new File(szZipFilePath);
    
    if(!f.exists())
    {
      System.out.println(
	"\nNot found: " + szZipFilePath);
    }
      
    if(f.isDirectory())
    {
      System.out.println(
	"\nNot file: " + szZipFilePath);
    }
    
    //System.out.println("Enter path to extract files: ");
    szExtractPath = path;
    
    File f1 = new File(szExtractPath);
    if(!f1.exists())
    {
      System.out.println(
	"\nNot found: " + szExtractPath);
    }
      
    if(!f1.isDirectory())
    {
      System.out.println(
	"\nNot directory: " + szExtractPath);
    }
    
    ZipFile zf; 
    Vector zipEntries = new Vector();
       
    try
    {  
      zf = new ZipFile(szZipFilePath); 
      
      
      Enumeration en = zf.entries();
      
      while(en.hasMoreElements())
      {
        zipEntries.addElement(
	  (ZipEntry)en.nextElement());
      }
      int sizetemp = 1;
      double oldpercentTotal = 0.0;
      double currFilePercent = 0.0;
      for (i = 0; i < zipEntries.size(); i++)
      {
        ZipEntry ze = 
	  (ZipEntry)zipEntries.elementAt(i);
        
        
        sizetemp +=ze.getCompressedSize();
        try{
        currFilePercent =     ((sizetemp /1024) *100/fileSize)/100;
        progressCurr.setProgress(currFilePercent);
        //progressCurr.setString("Извлечение "+ZipName + "  "+progressCurr.getValue()+"%");
        CurentTotalProgress += (  ((sizetemp/1024)*100/totalsize)-oldpercentTotal  )/100 ;
        progressBarTotal.setProgress(CurentTotalProgress);
        oldpercentTotal = ((sizetemp/1024)*100/totalsize);
        }catch(ArithmeticException ae){
            
        }
        
        extractFromZip(szZipFilePath, szExtractPath,
	  ze.getName(), zf, ze);
      }
      
      zf.close();
      //System.out.println("Done!");
    }
    catch(Exception ex)
    {
      System.out.println(ex.toString());
    }
    f.delete();
  }
  
  // ============================================
  // extractFromZip
  // ============================================
  private static void extractFromZip(
    String szZipFilePath, String szExtractPath,
    String szName,
    ZipFile zf, ZipEntry ze)
  {
    if(ze.isDirectory())
      return;
      
    String szDstName = slash2sep(szName);
    
    String szEntryDir;
    
    if(szDstName.lastIndexOf(File.separator) != -1)
    {
      szEntryDir =
        szDstName.substring(
	  0, szDstName.lastIndexOf(File.separator));
    }
    else	  
      szEntryDir = "";
    
    //System.out.print(szDstName);
    long nSize = ze.getSize();
    long nCompressedSize = 
      ze.getCompressedSize();
    
    //System.out.println(" " + nSize + " (" +      nCompressedSize + ")");  
  
    try
    {
       File newDir = new File(szExtractPath +
	 File.separator + szEntryDir);
	 
       newDir.mkdirs();	 
       
       FileOutputStream fos = 
	 new FileOutputStream(szExtractPath +
	 File.separator + szDstName);

       InputStream is = zf.getInputStream(ze);
       byte[] buf = new byte[1024];

       int nLength;
       
       while(true)
       {
         try
         {
	   nLength = is.read(buf);
         }	 
         catch (EOFException ex)
         {
	   break;
	 }  
	 
         if(nLength < 0) 
	   break;
         fos.write(buf, 0, nLength);
       }
       
       is.close();
       fos.close();
    }   
    catch(Exception ex)
    {
      System.out.println(ex.toString());
      //System.exit(0);
    }
  }  
  // ============================================
  // slash2sep
  // ============================================
  private static String slash2sep(String src)
  {
    int i;
    char[] chDst = new char[src.length()];
    String dst;
    
    for(i = 0; i < src.length(); i++)
    {
      if(src.charAt(i) == '/')
        chDst[i] = File.separatorChar;
      else
        chDst[i] = src.charAt(i);
    }
    dst = new String(chDst);
    return dst;
  }
//end
}
