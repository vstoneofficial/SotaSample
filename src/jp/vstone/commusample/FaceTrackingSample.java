package jp.vstone.commusample;

import java.awt.Color;
import java.io.ObjectInputStream.GetField;

import javax.naming.CommunicationException;

import jp.vstone.RobotLib.CCommUMotion;
import jp.vstone.RobotLib.CPlayWave;
import jp.vstone.RobotLib.CRobotMem;
import jp.vstone.RobotLib.CRobotPose;
import jp.vstone.RobotLib.CRobotUtil;
import jp.vstone.camera.CRoboCamera;
import jp.vstone.camera.CameraCapture;
import jp.vstone.camera.FaceDetectResult;

/**
 * フェイストラッキングをして、笑顔であれば写真を撮るサンプル
 * @author Vstone
 *
 */
public class FaceTrackingSample {

	static final String TAG = "FaceTrackingSample";
	static final int SMILE_POINT = 45;
	
	public static void main(String args[]){
		CRobotUtil.Log(TAG, "Start " + TAG);

		CRobotPose pose;
		//VSMDと通信ソケット・メモリアクセス用クラス
		CRobotMem mem = new CRobotMem();
		//CCommU用モーション制御クラス
		CCommUMotion motion = new CCommUMotion(mem);
		
		CRoboCamera cam = new CRoboCamera("/dev/video0", motion);
		
		if(mem.Connect()){
			//CCommU仕様にVSMDを初期化
			motion.InitRobot_CommU();
			
			CRobotUtil.Log(TAG, "Rev. " + mem.FirmwareRev.get());
			
			//サーボモータを現在位置でトルクOnにする
			CRobotUtil.Log(TAG, "Servo On");
			motion.ServoOn();
			
			//すべての軸を動作
			pose = CCommUMotion.getInitPose();
			pose.setLED_CommU(Color.GREEN, 0, 0, Color.GREEN);
			
			motion.play(pose, 500);
			CRobotUtil.wait(500);
			
			//笑顔推定有効
			cam.setEnableSmileDetect(true);
			//顔検索有効
			cam.setEnableFaceSearch(true);
			//フェイストラッキング開始
			cam.StartFaceTraking();
			//cam.StartFaceDetect();
			
			int photcnt = 0;
			
			while(true){
				FaceDetectResult result = cam.getDetectResult();
				if(result.isDetect()){	
					CRobotUtil.Log(TAG, "[Detect] Smile:" + result.getSmile());
					//
					if(result.getSmile() > SMILE_POINT){
						
						//LEDだけ先に変更
						pose = new CRobotPose();						
						pose.setLED_CommU(Color.ORANGE, 255, 255, Color.GREEN);
						//playに任意のKeyを指定すると、
						motion.play(pose, 100,"FACE_LED");
						
						//写真を取る前のポーズ＋音声
						CPlayWave.PlayWave("./sound/take_a_photo.wav");
						pose = new CRobotPose();						
						pose.SetPose(	new Byte[]{CCommUMotion.SV_L_SHOULDER_P,CCommUMotion.SV_L_SHOULDER_R,CCommUMotion.SV_R_SHOULDER_P,CCommUMotion.SV_R_SHOULDER_R},
								new Short[]{0,0,0,0}
								);
						motion.play(pose,1000);
						
						//フェイストラッキング停止
						cam.StopFaceTraking();
						//撮影用に初期化
						cam.initStill(new CameraCapture(CameraCapture.CAP_IMAGE_SIZE_5Mpixel, CameraCapture.CAP_FORMAT_MJPG));
						
						//撮影時のポーズ＋音声
						CPlayWave.PlayWave("./sound/pasha.wav");
						pose = new CRobotPose();					
						pose.SetPose(	new Byte[]{CCommUMotion.SV_L_SHOULDER_P,CCommUMotion.SV_L_SHOULDER_R,CCommUMotion.SV_R_SHOULDER_P,CCommUMotion.SV_R_SHOULDER_R},
										new Short[]{-300,-400,300,400}
										);
						motion.play(pose,500);			
						//撮影
						cam.StillPicture("./photo"+photcnt);
						photcnt++;
						
						CPlayWave.PlayWave("./sound/nice_photo.wav");
						pose = new CRobotPose();				
						pose.SetPose(	new Byte[]{CCommUMotion.SV_L_SHOULDER_P,CCommUMotion.SV_L_SHOULDER_R,CCommUMotion.SV_R_SHOULDER_P,CCommUMotion.SV_R_SHOULDER_R},
								new Short[]{1450,0,-1450,0}
								);
						motion.play(pose,1000);
						//フェイストラッキング開始
						cam.StartFaceTraking();
					}
					else{
						pose = new CRobotPose();				
						pose.setLED_CommU(Color.GREEN, 255, 255, Color.GREEN);
						motion.play(pose, 500);			
					}
				}
				else{
					CRobotUtil.Log(TAG, "[Not Detect]");
					pose = new CRobotPose();				
					pose.setLED_CommU(Color.BLUE, 0, 0, Color.GREEN);
					motion.play(pose, 500);			
				}
				CRobotUtil.wait(500);
			}
		}
		motion.ServoOff();
	}
}
