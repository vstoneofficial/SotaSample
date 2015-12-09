package jp.vstone.sotatest;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import jp.vstone.camera.CameraCapture;

public class StillTest {
	
	
	public static void main(String[] args){
	    String deviceName="/dev/video0";
	    
	    /**
	     * YUYVデータで取得  	
	     */
	    if(false){
			CameraCapture cap = new CameraCapture(CameraCapture.CAP_IMAGE_SIZE_VGA, CameraCapture.CAP_FORMAT_YUV2);
			try {
				cap.openDevice(deviceName);
			    try{Thread.sleep(50);}catch(InterruptedException e){}
				cap.snap();
				cap.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			try{
		      File file = new File("v3.yuv");
		      BufferedOutputStream fis = new BufferedOutputStream(new FileOutputStream(file));
		      fis.write(cap.getImageRawData(),0,cap.getImageRawData().length);
		      fis.close();
	
		    }catch(IOException e){
		      System.out.println(e);
		    }
	    }

		/**
		 *  BGRデータを保存
		 */
	    if(false){
		    int width = 640;
		    int height = 480;
		    CameraCapture cap = new CameraCapture(width, height, CameraCapture.CAP_FORMAT_3BYTE_BGR);
			try {
				cap.openDevice(deviceName);
			    try{Thread.sleep(50);}catch(InterruptedException e){}
				cap.snap();
				cap.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			try{
				cap.saveImage("v8");
		    }catch(IOException e){
		      System.out.println(e);
		    }
		}

		/**
		 * グレースケールデータを保存 
		 */
	    if(false){
	    	CameraCapture cap = new CameraCapture(CameraCapture.CAP_IMAGE_SIZE_VGA, CameraCapture.CAP_FORMAT_BYTE_GRAY);
			try {
				cap.openDevice(deviceName);
			    try{Thread.sleep(50);}catch(InterruptedException e){}
				cap.snap();
				cap.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			try{
				cap.saveImage("v8_gray");
		    }catch(IOException e){
		      System.out.println(e);
		    }
		}
	    
	    /**
	     * jpegデータを保存 
	     */
	    {
	    	CameraCapture cap = new CameraCapture(CameraCapture.CAP_IMAGE_SIZE_VGA, CameraCapture.CAP_FORMAT_MJPG);
			try {
				cap.openDevice(deviceName);
			    try{Thread.sleep(1000);}catch(InterruptedException e){}
				cap.snap();
			    try{Thread.sleep(1000);}catch(InterruptedException e){}
				cap.snap();
			    try{Thread.sleep(1000);}catch(InterruptedException e){}
				cap.snap();
			    try{Thread.sleep(1000);}catch(InterruptedException e){}
				cap.snap();
			    try{Thread.sleep(1000);}catch(InterruptedException e){}
				cap.snap();
			    try{Thread.sleep(1000);}catch(InterruptedException e){}
				cap.snap();
				cap.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			try{
				cap.saveImage("v8");
		    }catch(IOException e){
		      System.out.println(e);
		    }
		}
	}
	
}

