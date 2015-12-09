package jp.vstone.sotasample;

import java.awt.Color;
import java.io.ObjectInputStream.GetField;

import jp.vstone.RobotLib.CPlayWave;
import jp.vstone.RobotLib.CRobotMem;
import jp.vstone.RobotLib.CRobotPose;
import jp.vstone.RobotLib.CRobotUtil;
import jp.vstone.RobotLib.CSotaMotion;
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
		//Sota用モーション制御クラス
		CSotaMotion motion = new CSotaMotion(mem);
		
		CRoboCamera cam = new CRoboCamera("/dev/video0", motion);
		
		if(mem.Connect()){
			//Sota仕様にVSMDを初期化
			motion.InitRobot_Sota();
			
			CRobotUtil.Log(TAG, "Rev. " + mem.FirmwareRev.get());
			
			//サーボモータを現在位置でトルクOnにする
			CRobotUtil.Log(TAG, "Servo On");
			motion.ServoOn();
			
			//すべての軸を動作
			pose = new CRobotPose();
			pose.SetPose(new Byte[] {1   ,2   ,3   ,4   ,5   ,6   ,7   ,8}	//id
					,  new Short[]{0   ,-900,0   ,900 ,0   ,0   ,0   ,0}				//target pos
					);
			//LEDを点灯（左目：赤、右目：赤、口：Max、電源ボタン：赤）
			pose.setLED_Sota(Color.BLUE, Color.BLUE, 255, Color.GREEN);
			
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
						pose.setLED_Sota(Color.ORANGE, Color.ORANGE, 255, Color.GREEN);
						//playに任意のKeyを指定すると、
						motion.play(pose, 100,"FACE_LED");
						
						//写真を取る前のポーズ＋音声
						CPlayWave.PlayWave("./sound/take_a_photo.wav");
						pose = new CRobotPose();																							//@<BlockInfo>jp.vstone.block.pose,208,80,208,80,False,2,コメント@</BlockInfo>
						pose.SetPose(	new Byte[]{1,2,3,4,5},
										new Short[]{-1,71,-895,1,769}
										);
						motion.play(pose,1000);
						
						//フェイストラッキング停止
						cam.StopFaceTraking();
						//撮影用に初期化
						cam.initStill(new CameraCapture(CameraCapture.CAP_IMAGE_SIZE_5Mpixel, CameraCapture.CAP_FORMAT_MJPG));
						
						//撮影時のポーズ＋音声
						CPlayWave.PlayWave("./sound/pasha.wav");
						pose = new CRobotPose();						
						//頭を動かさずに撮影する　->　頭の角度を指定しない																	//@<BlockInfo>jp.vstone.block.pose,272,80,272,80,False,1,コメント@</BlockInfo>
						pose.SetPose(	new Byte[]{1,2,3,4,5},
										new Short[]{1,69,-21,3,-35}
										);
						motion.play(pose,1000);			
						//撮影
						cam.StillPicture("./photo"+photcnt);
						photcnt++;
						
						CPlayWave.PlayWave("./sound/nice_photo.wav");
						pose = new CRobotPose();					
						//頭を動かさずに撮影する　->　頭の角度を指定しない
						pose.SetPose(	new Byte[] {1   ,2   ,3   ,4   ,5}
									,  new Short[]{0   ,-900,0   ,900 ,0}
								);
						motion.play(pose,1000);
						//フェイストラッキング開始
						cam.StartFaceTraking();
					}
					else{
						pose.setLED_Sota(Color.GREEN, Color.GREEN, 255, Color.GREEN);
						motion.play(pose, 500);			
					}
				}
				else{
					CRobotUtil.Log(TAG, "[Not Detect]");
					pose.setLED_Sota(Color.BLUE, Color.BLUE, 255, Color.GREEN);
					motion.play(pose, 500);			
				}
				CRobotUtil.wait(500);
			}
		}
		motion.ServoOff();
	}
}
