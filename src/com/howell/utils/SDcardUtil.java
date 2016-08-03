package com.howell.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.graphics.Bitmap;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

public class SDcardUtil {
	public static String getSDCardPath(){
		return Environment.getExternalStorageDirectory().getAbsolutePath();
	}
	
	public static String getCertificateCachePath(){
		return getSDCardPath() + File.separator + "eCamera" + File.separator + "CertificateCache" + File.separator;
	}
	
	public static void createCertificateDir(){
		File eCameraDir = new File(getSDCardPath() + "/eCamera");
		if (!eCameraDir.exists()) {
			eCameraDir.mkdirs();
		}
		File CertificateDir = new File(getSDCardPath() + "/eCamera/CertificateCache");
		if (!CertificateDir.exists()) {
			CertificateDir.mkdirs();
		}
	}
	
	public static int freeSpaceOnSd() {  
	    StatFs stat = new StatFs(getSDCardPath());  
	    double sdFreeMB = ((double)stat.getAvailableBlocks() * (double) stat.getBlockSize()) / ( 1024 *1024 );  
	    return (int) sdFreeMB;  
	}  
	
	@SuppressWarnings("unused")
	private void updateFileTime(String filePath) {  
	    File file = new File(filePath);         
	    long newModifiedTime = System.currentTimeMillis();  
	    file.setLastModified(newModifiedTime);  
	}  
	
	public static String saveCreateCertificate(InputStream in,String filename){
		if(in==null){
			Log.e("123", "in==null");
			return null;
		}
		createCertificateDir();
		File file = new File(getSDCardPath() + File.separator + "eCamera" + File.separator + "CertificateCache" + File.separator + filename);
		try {
			file.createNewFile();
			OutputStream outStream = new FileOutputStream(file);  
			
			byte [] bs = new byte[2048];
			while((in.read(bs))!=-1){
				outStream.write(bs);
			}
			outStream.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return file.getAbsolutePath();
	}
	
	
	
}
