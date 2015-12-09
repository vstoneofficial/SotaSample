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
import jp.vstone.camera.FaceDetectLib.FaceUser;
import jp.vstone.sotatalk.SpeechRecog;
import jp.vstone.sotatalk.TextToSpeechSota;

/**
 * フェイストラッキングをして、覚えている顔があれば、その名前を呼ぶサンプル
 * 別途クラウドサービスの契約が必要
 * @author Vstone
 *
 */
public class FaceAuthenticationSample {

	static final String TAG = "FaceAuthenticationSample";
	static final int SMILE_POINT = 45;
	
	public static void main(String args[]){
		CRobotUtil.Log(TAG, "Start " + TAG);

		CRobotPose pose;
		//VSMDと通信ソケット・メモリアクセス用クラス
		CRobotMem mem = new CRobotMem();
		//Sota用モーション制御クラス
		CSotaMotion motion = new CSotaMotion(mem);
		CRoboCamera cam = new CRoboCamera("/dev/video0", motion);
		
		SpeechRecog speechrec = new SpeechRecog(motion);
		
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
			
			int detectcnt = 0;
			while(true){
				FaceDetectResult result = cam.getDetectResult();

				if(result.isDetect()){	
					detectcnt++;
				}
				else{
					detectcnt = 0;
				}
				
				if(detectcnt > 3){	
					FaceUser user = cam.getUser(1000);
					if(user != null){
						if(user.isNewUser()){
							CPlayWave.PlayWave(TextToSpeechSota.getTTSFile("はじめまして。あなたの名前はなんですか？") , true);
							int retrycnt = 3;
							for(int i = 0; i < retrycnt ;i++){
								String name = SpeechRecog.getName(speechrec.getRecognitionNbest(10000));
								if(name != null){
									CPlayWave.PlayWave(TextToSpeechSota.getTTSFile("あなたの名前は," + name + ",でまちがいない？") , true);
									
									String resp = SpeechRecog.getYesorNo(speechrec.getRecognitionNbest(10000));
									if(resp.contains(SpeechRecog.ANSWER_YES)){
										user.setName(name);
										cam.addUser(user);
										CPlayWave.PlayWave(TextToSpeechSota.getTTSFile("オッケー！"+ name +"の顔は覚えたよ。") , true);
										break;
									}
									else if(resp.contains(SpeechRecog.ANSWER_NO)){
										CPlayWave.PlayWave(TextToSpeechSota.getTTSFile("違った？もう一度名前をおしえて？") , true);
									}
									else{
										result = cam.getDetectResult();
										if(result.isDetect()){
											CPlayWave.PlayWave(TextToSpeechSota.getTTSFile("聞こえなかったよ。" + name + ",でまちがいない？") , true);
										}
										else 
											break;
									}
								}
								else if(i < (retrycnt - 1)){
									result = cam.getDetectResult();
									if(result.isDetect()){
										CPlayWave.PlayWave(TextToSpeechSota.getTTSFile("もう一度教えて。あなたの名前はなんですか？") , true);
									}
									else 
										break;
								}
							}
						}
						else{
							CPlayWave.PlayWave(TextToSpeechSota.getTTSFile("やっほー！。" + user.getName() + "！。") , true);
							CPlayWave.PlayWave(TextToSpeechSota.getTTSFile("元気？") , true);
						}
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
