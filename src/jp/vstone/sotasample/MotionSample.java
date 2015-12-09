package jp.vstone.sotasample;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Arrays;

import jp.vstone.RobotLib.*;
/**
 * VSMDを使用し、モーション再生・音声再生するサンプル
 * @author Vstone
 *
 */
public class MotionSample {
	static final String TAG = "MotionSample";
	public static void main(String args[]){
		CRobotUtil.Log(TAG, "Start " + TAG);

		CRobotPose pose;
		//VSMDと通信ソケット・メモリアクセス用クラス
		CRobotMem mem = new CRobotMem();
		//Sota用モーション制御クラス
		CSotaMotion motion = new CSotaMotion(mem);
		
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
			pose.setLED_Sota(Color.RED, Color.RED, 255, Color.RED);
			
			//遷移時間1000msecで動作開始。
			CRobotUtil.Log(TAG, "play:" + motion.play(pose,1000));
			
			//補間完了まで待つ
			motion.waitEndinterpAll();

			//一部の軸を指定して動作
			//CSotaMotionの定数を利用してID指定する場合
			pose = new CRobotPose();
			pose.SetPose(new Byte[] {CSotaMotion.SV_HEAD_R, CSotaMotion.SV_L_SHOULDER,CSotaMotion.SV_L_ELBOW,CSotaMotion.SV_R_ELBOW}	//id
						,  new Short[]{200, 700 ,-200,200}	//target pos
			);
			pose.setLED_Sota(Color.GREEN, Color.GREEN, 255, Color.GREEN);
			motion.play(pose,1000);
			motion.waitEndinterpAll();
			
			//音声ファイル再生
			//raw　Waveファイルのみ対応
			CPlayWave.PlayWave("sound/cursor10.wav");
			CRobotUtil.wait(2000);

			pose = new CRobotPose();
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
	}
}
