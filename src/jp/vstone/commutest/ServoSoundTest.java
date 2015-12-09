package jp.vstone.commutest;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
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
		CRobotUtil.Log(TAG, "Start " + TAG);

		boolean errorled = false;
		
		//VSMDと通信ソケット・メモリアクセス用クラス
		CRobotMem mem = new CRobotMem();
		//CommU用モーション制御クラス
		CCommUMotion motion = new CCommUMotion(mem);
		CRobotPose pose;
		
		if(mem.Connect()){
			//Sota仕様にVSMDを初期化
			motion.InitRobot_CommU();
			
			CRobotUtil.Log(TAG, "Rev. " + mem.FirmwareRev.get());

			Byte[] ids = motion.getDefaultIDs();
			Short[] pos;
			{
				pos = motion.getReadpos();
				for(int i = 0; i < pos.length;i++){
					CRobotUtil.Log(TAG, "Read Pos. ID:" + ids[i] + " , Pos:" + pos[i]);
					if(pos[i] > 1500 || pos[i] < -1500 ){
						CRobotUtil.Log(TAG, "ERROR:" + ids[i]);
					}
				}
			}
			
			//サーボモータを現在位置でトルクOnにする
			CRobotUtil.Log(TAG, "Servo On");
			motion.ServoOn();
			CRobotUtil.wait(1000);
			
			//すべての軸を動作
			for (Short p : pos) {
				p = 0;
			}
			pose = new CRobotPose();
			pose.SetPose( new Byte[]{1,2,3,4,5,6,7,8,9,10,11,12,13,14},  new Short[]{0,0,0,0,0,0,0,0,0,0,0,0,0,0});
			//LEDを点灯
			pose.setLED_CommU(Color.RED,255, 255, Color.RED);
			//遷移時間1000msecで動作開始。
			motion.play(pose,500);
			//補間完了まで待つ
			motion.waitEndinterpAll();
			CRobotUtil.wait(1000);

			pose = new CRobotPose();
			pose.setLED_CommU(Color.GREEN,0, 0, Color.GREEN);
			motion.play(pose,500);
			motion.waitEndinterpAll();
			CRobotUtil.wait(1000);

			pose = new CRobotPose();
			pose.setLED_CommU(Color.BLUE,255, 255, Color.BLUE);
			motion.play(pose,500);
			motion.waitEndinterpAll();
			CRobotUtil.wait(2000);
			
			
			//一部の軸を指定して動作
			//CSotaMotionの定数を利用してID指定する場合
			for(int i = 0 ; i < ids.length  ; i++){
				CRobotUtil.Log(TAG, "Servo Move " + i);
				pose = new CRobotPose();
				pose.SetPose(new Byte[] {(byte)ids[i],}	//id
							,  new Short[]{-200}	//target pos
				);
				motion.play(pose,400);
				motion.waitEndinterpAll();
				CRobotUtil.wait(100);

				pose = new CRobotPose();
				pose.SetPose(new Byte[] {(byte)ids[i],}	//id
							,  new Short[]{200}	//target pos
				);
				motion.play(pose,400);
				motion.waitEndinterpAll();
				CRobotUtil.wait(100);

				pose = new CRobotPose();
				CRobotUtil.Log(TAG, "Servo Move " + i);
				pose.SetPose(new Byte[] {(byte)ids[i],}	//id
							,  new Short[]{0}	//target pos
				);
				motion.play(pose,400);
				motion.waitEndinterpAll();
				CRobotUtil.wait(100);
			}

			pose = new CRobotPose();
			pose.SetPose( new Byte[]{1,2,3,4,5,6,7,8,9,10,11,12,13,14},  new Short[]{0,0,0,0,0,0,0,0,0,0,0,0,0,0});
			pose.setLED_CommU(Color.GREEN,0, 0, Color.GREEN);
			motion.play(pose,1000);
			motion.waitEndinterpAll();
			
			//サーボモータのトルクオフ
			CRobotUtil.Log(TAG, "Servo Off");
			motion.ServoOff();
		}
		

		CRobotUtil.Log(TAG, "Camera Test");
		CPlayWave.PlayWave_wait("./sound/start_cam_test.wav");
		
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
			pose.setLED_CommU(Color.RED, 0, 0, Color.RED);
			motion.play(pose,500);
			motion.waitEndinterpAll();
		}
		else{
			pose = new CRobotPose();
			pose.setLED_CommU(Color.GREEN, 0, 0, Color.GREEN);
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
