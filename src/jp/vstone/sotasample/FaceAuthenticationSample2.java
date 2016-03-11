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
import jp.vstone.sotatalk.MotionAsSotaWish;
import jp.vstone.sotatalk.SpeechRecog;
import jp.vstone.sotatalk.TextToSpeechSota;

/**
 * フェイストラッキングをして、覚えている顔があれば、その名前を呼ぶサンプル
 * 別途クラウドサービスの契約が必要
 * @author Vstone
 *
 */
public class FaceAuthenticationSample2 {

	static final String TAG = "FaceAuthenticationSample2";
	public static void main(String args[]){
		CRobotUtil.Log(TAG, "Start " + TAG);

		MotionAsSotaWish sotawish;
		CRobotPose pose;
		//VSMDと通信ソケット・メモリアクセス用クラス
		CRobotMem mem = new CRobotMem();
		//Sota用モーション制御クラス
		CSotaMotion motion = new CSotaMotion(mem);
		CRoboCamera cam = new CRoboCamera("/dev/video0", motion);
		sotawish = new MotionAsSotaWish(motion);

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
					FaceUser user = cam.getUser(result);
					if(user != null){
						if(user.isNewUser()){
							sotawish.Say("はじめまして。あなたの名前はなんですか？");
							String name = null; 
							if((name = speechrec.getName(15000, 3)) != null){
								sotawish.Say("あなたの名前は," + name + ",でまちがいない？");
								String yesorno= speechrec.getYesorNo(10000, 3);
								if(yesorno != null && yesorno.contains(SpeechRecog.ANSWER_YES)){
									
									user.setName(name);
									if(cam.addUser(user)){
										CPlayWave.PlayWave(TextToSpeechSota.getTTSFile("オッケー！"+ name +"の顔は覚えたよ。") , true);
									}
									else{
										CPlayWave.PlayWave(TextToSpeechSota.getTTSFile("正面じゃなかったから、登録できなかったよ。もう一度撮影するから、じっとしててね。") , true);
									}
									result = cam.getDetectResult();
									user = cam.getUser(result);
									if(user.isNewUser()){
										user.setName(name);
										if(cam.addUser(user)){
											CPlayWave.PlayWave(TextToSpeechSota.getTTSFile("オッケー！"+ name +"の顔は覚えたよ。") , true);
										}
										else{
											CPlayWave.PlayWave(TextToSpeechSota.getTTSFile("登録できなかったよ。") , true);
										}
									}
								}
								else if(yesorno != null && yesorno.contains(SpeechRecog.ANSWER_NO)){
									sotawish.Say("ちがった。ごめんね。");
								}
								else{
									sotawish.Say("ごめん。わからなかったよ。");
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
