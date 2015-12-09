package jp.vstone.sotatest;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;

import jp.vstone.RobotLib.*;
import jp.vstone.camera.CRoboCamera;
import jp.vstone.camera.CameraCapture;
import jp.vstone.camera.FaceDetectResult;
/**
 * VSMDを使用し、モーション再生・音声再生するサンプル
 * @author Vstone
 *
 */
public class ServoSoundTest {
	static final String TAG = "MotionSample";
	public static void main(String args[]){
		
		boolean errorled = false;
		
		CRobotUtil.Log(TAG, "Start " + TAG);

		CRobotPose pose = new CRobotPose();
		//VSMDと通信ソケット・メモリアクセス用クラス
		CRobotMem mem = new CRobotMem();
		//Sota用モーション制御クラス
		CSotaMotion motion = new CSotaMotion(mem);
		
		//RaspberryPi版はKernelにバグがあるので、2015/3/4現在LEDとカメラの同時使用ができません。
		//カメラを使用される場合、以下でI2Cを無効化してください。
		//mem.I2CSendEN.set(0);
		
		if(mem.Connect()){
			CRobotUtil.Log(TAG, "connect " + TAG);
			//Sota仕様にVSMDを初期化
			motion.InitRobot_Sota();
			
			CRobotUtil.Log(TAG, "Rev. " + mem.FirmwareRev.get());
			
			//サーボモータを現在位置でトルクOnにする
			CRobotUtil.Log(TAG, "Servo On");
			motion.ServoOn();
			
			//すべての軸を動作
			pose = new CRobotPose();
			pose.SetPose(new Byte[] {1   ,2   ,3   ,4   ,5   ,6   ,7   ,8}	//id
			,  new Short[]{80   , 80 , 80   , 80 ,80   ,80   ,80   ,80}				//target pos
					);
			//LEDを点灯（左目：赤、右目：赤、口：Max、電源ボタン：赤）
			pose.setLED_Sota(Color.RED, Color.RED, 255, Color.RED);
			//遷移時間1000msecで動作開始。
			motion.play(pose,1000);
			//補間完了まで待つ
			motion.waitEndinterpAll();

			CRobotUtil.wait(500);

			Short[] pos = motion.getReadpos();
			Byte[] ids = motion.getDefaultIDs();
			ArrayList<Byte> error = new ArrayList<>();
			
			
			int dif = 70;
			for(int i = 0; i < pos.length;i++){
				CRobotUtil.Log(TAG, "Read Pos. ID:" + ids[i] + " , Pos:" + pos[i]);
				if(pos[i] <  (dif+80) && pos[i] > (-dif + 80)){
					CRobotUtil.Log(TAG, "OK");
				}
				else{
					CRobotUtil.Log(TAG, "Error");
					error.add(ids[i]);
				}
			}
			
			if(error.size() > 0){

				CRobotUtil.Log(TAG, "Error");
				errorled = true;
			}

			//すべての軸を動作
			pose = new CRobotPose();
			pose.SetPose(new Byte[] {1   ,2   ,3   ,4   ,5   ,6   ,7   ,8}	//id
			,  new Short[]{0   , 0 , 0   , 0 ,0   ,0   ,0   ,0}				//target pos
					);
			//LEDを点灯（左目：赤、右目：赤、口：Max、電源ボタン：赤）
			pose.setLED_Sota(Color.RED, Color.RED, 255, Color.RED);
			//遷移時間1000msecで動作開始。
			motion.play(pose,500);
			//補間完了まで待つ
			motion.waitEndinterpAll();	
			CRobotUtil.wait(500);
			
			pos = motion.getReadpos();
			ids = motion.getDefaultIDs();
			
			error = new ArrayList<>();

			for(int i = 0; i < pos.length;i++){
				CRobotUtil.Log(TAG, "Read Pos. ID:" + ids[i] + " , Pos:" + pos[i]);
				if(pos[i] <  dif && pos[i] > -dif){
					CRobotUtil.Log(TAG, "OK");
				}
				else{
					CRobotUtil.Log(TAG, "Error");
					error.add(ids[i]);
				}
			}

			
			if(error.size() > 0){

				CRobotUtil.Log(TAG, "Error");
				errorled = true;
			}

			if(errorled){

				CPlayWave.PlayWave_wait("./sound/error_servo.wav");
				pose.setLED_Sota(Color.RED, Color.RED, 255, Color.RED);
				motion.play(pose,500);
			}
			
			
			
			CRobotUtil.wait(1000);
			pose.setLED_Sota(Color.GREEN, Color.GREEN, 255, Color.GREEN);
			motion.play(pose,500);
			motion.waitEndinterpAll();
			CRobotUtil.wait(1000);
			
			pose.setLED_Sota(Color.BLUE, Color.BLUE, 255, Color.BLUE);
			motion.play(pose,500);
			motion.waitEndinterpAll();
			CRobotUtil.wait(2000);
			

			if(errorled){
				pose.setLED_Sota(Color.RED, Color.RED, 255, Color.RED);
				motion.play(pose,500);
			}
			
			//一部の軸を指定して動作
			//CSotaMotionの定数を利用してID指定する場合
			for(int i = 1 ; i <= 8 ; i++){

				CRobotUtil.Log(TAG, "Servo Move " + i);
				pose.SetPose(new Byte[] {(byte)i,}	//id
							,  new Short[]{200}	//target pos
				);
				motion.play(pose,300);
				motion.waitEndinterpAll();
				CRobotUtil.wait(100);

				pose.SetPose(new Byte[] {(byte)i,}	//id
							,  new Short[]{-200}	//target pos
				);
				motion.play(pose,300);
				motion.waitEndinterpAll();
				CRobotUtil.wait(100);

				pose.SetPose(new Byte[] {(byte)i,}	//id
							,  new Short[]{0}	//target pos
				);
				motion.play(pose,300);
				motion.waitEndinterpAll();
				CRobotUtil.wait(100);
			}
			
			pose.SetPose(new Byte[] {1   ,2   ,3   ,4   ,5   ,6   ,7   ,8}	//id
						,  new Short[]{0   ,-900   ,0   ,900   ,0   ,0   ,0   ,0}	//target pos
			);
			pose.setLED_Sota(Color.BLUE, Color.BLUE, 255, Color.BLUE);
			motion.play(pose,1000);
			motion.waitEndinterpAll();
			
			//サーボモータのトルクオフ
			CRobotUtil.Log(TAG, "Servo Off");
			motion.ServoOff();
		}
		

		if(errorled){
			pose.setLED_Sota(Color.RED, Color.RED, 255, Color.RED);
			motion.play(pose,500);
		}
		
		
		
		
		
		CRobotUtil.Log(TAG, "Camera Test");
		CPlayWave.PlayWave_wait("./sound/start_cam_test.wav");

	    /**
	     * jpegデータを保存 
	     */
		/*
		{
		    String deviceName="/dev/video0";
			SotaCameraStill cap = new SotaCameraStill(SotaCameraStill.CAP_IMAGE_SIZE_HD_1080, SotaCameraStill.CAP_FORMAT_MJPG);
			try {
				cap.openDevice(deviceName);
				cap.snap();
				cap.snap();
				CRobotUtil.wait(2000);
				cap.snap();
				cap.saveImage("v1");
				cap.snap();
				cap.snap();
				CRobotUtil.wait(2000);
				cap.snap();
				cap.saveImage("v2");
				cap.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}*/
		
		/**
		 * QRコードを読み込み
		 */
		{
		    String deviceName="/dev/video0";
		    CameraCapture cap = new CameraCapture(CameraCapture.CAP_IMAGE_SIZE_VGA,CameraCapture.CAP_FORMAT_BYTE_GRAY);
		    
			try{
				cap.openDevice(deviceName);
	        	cap.snap();
			}catch( Exception e ){
				e.printStackTrace();
			}
			boolean cam_OK = false;
			for(int i = 0; i < 50 ; i++){
				try{
		        	CRobotUtil.wait(100);
		        	cap.snap();
		        	
				    //. 画像読み込み
		        	BufferedImage image = cap.RawtoBufferedImage();	
				    LuminanceSource source = new BufferedImageLuminanceSource( image );
				    BinaryBitmap bitmap = new BinaryBitmap( new HybridBinarizer( source ) );
				    
				    //デコード
				    //Reader reader = new MultiFormatReader();
				    Reader reader = new QRCodeReader();
				    Result result = reader.decode( bitmap );
			
				    //バーコードフォーマット
				    //BarcodeFormat format = result.getBarcodeFormat();
			
				    //バーコードコンテンツ（読み取り結果）
				    String text = result.getText();
				    if(text.equals("http://www.vstone.co.jp/")){
				    	cam_OK = true;
						break;
				    }
				}catch( Exception e ){
					e.printStackTrace();
				}
			}
			cap.close();
			if(cam_OK){
				CPlayWave.PlayWave_wait("./sound/QR_OK.wav");
			}
			else{
				CPlayWave.PlayWave_wait("./sound/QR_ERROR.wav");
				errorled = true;
			}
		}
		
		

		motion.ServoOn();

		CPlayWave.PlayWave_wait("./sound/start_face_test.wav");
		
		CRoboCamera cam = new CRoboCamera("/dev/video0",motion);
		cam.setEnableFaceSearch(true);
		cam.StartFaceTraking();
		int detectcnt = 0;
		for(int i = 0 ; i < 20;i++){
			CRobotUtil.wait(500);
			FaceDetectResult result = cam.getDetectResult();
			if(result.isDetect()){
				detectcnt++;
				if(detectcnt > 5){
					
					break;
				}
			}
		}

		if(detectcnt > 5){
			CPlayWave.PlayWave_wait("./sound/face_ok.wav");			
		}
		else{
			CPlayWave.PlayWave_wait("./sound/face_error.wav");			
		}	
		cam.StopFaceTraking();

		motion.ServoOff();
		
		
	    
		if(errorled){
			pose = new CRobotPose();
			pose.setLED_Sota(Color.RED, Color.RED, 255, Color.RED);
			motion.play(pose,500);
			motion.waitEndinterpAll();
		}
		else{
			pose = new CRobotPose();
			pose.setLED_Sota(Color.GREEN, Color.GREEN, 255, Color.GREEN);
			motion.play(pose,500);
			motion.waitEndinterpAll();
		}
		
		//音声ファイル録音
		CRobotUtil.Log(TAG, "Mic Recording Test");
		CPlayWave.PlayWave_wait("./sound/start_rec_test.wav");
		CRecordMic mic = new CRecordMic();
		mic.startRecording("./test_rec.wav",5000);
		mic.waitend();
		
		CRobotUtil.Log(TAG, "Spk Play Test");
		
		//音声ファイル再生
		CPlayWave.PlayWave_wait("./test_rec.wav");
		File v =  new File("./test_rec.wav");
		v.delete();
		
		//音声ファイル再生
		//raw　Waveファイルのみ対応
		CPlayWave.PlayWave("./sound/end_test.wav");
		CRobotUtil.wait(2000);
		
	}
}
