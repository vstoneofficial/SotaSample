package jp.vstone.sotasample;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Arrays;

import jp.vstone.RobotLib.*;
/**
 * ボリュームボタンと電源ボタンの読込サンプル
 * ボリュームボタンを押すごとに手を上げ下げする。
 * 電源ボタンを押すと終了
 * @author Vstone
 *
 */
public class ButtonReadSample {
	static final String TAG = "ButtonReadSample";
	public static void main(String args[]){
		CRobotUtil.Log(TAG, "Start " + TAG);
	
		//VSMDと通信ソケット・メモリアクセス用クラス
		CRobotMem mem = new CRobotMem();
		//Sota用モーション制御クラス
		CSotaMotion motion = new CSotaMotion(mem);
		
		CRobotPose pose;
		
		if(mem.Connect()){
			//Sota仕様にVSMDを初期化
			motion.InitRobot_Sota();
			CRobotUtil.Log(TAG, "Rev. " + mem.FirmwareRev.get());

			//サーボモータを現在位置でトルクOnにする
			CRobotUtil.Log(TAG, "Servo On");
			motion.ServoOn();
			pose = new CRobotPose();
			pose.SetPose(new Byte[]{CSotaMotion.SV_L_SHOULDER,CSotaMotion.SV_R_SHOULDER}, 
					new Short[]{-900,900});
			motion.play(pose,1000);
			motion.waitEndinterpAll();
			

			CRobotUtil.Log(TAG, "Push Volume Down Btn : Left Arm Up");
			CRobotUtil.Log(TAG, "Push Volume Up Btn : Right Arm Up");
			CRobotUtil.Log(TAG, "Push Power Btn (not Long Press!) : End sample");
			
			while(true){
				if(motion.isButton_Power()){
					CRobotUtil.Log(TAG, "Power Btn");
					//終了
					break;
				}
				else if(motion.isButton_VolDown()){
					CRobotUtil.Log(TAG, "VolDown Btn");
					pose = new CRobotPose();
					pose.SetPose(new Byte[]{CSotaMotion.SV_L_SHOULDER,CSotaMotion.SV_R_SHOULDER}, 
							new Short[]{0 , 900});
					motion.play(pose,1000);
					motion.waitEndinterpAll();
				}				
				else if(motion.isButton_VolUp()){
					CRobotUtil.Log(TAG, "VolUp Btn");
					pose = new CRobotPose();
					pose.SetPose(new Byte[]{CSotaMotion.SV_L_SHOULDER,CSotaMotion.SV_R_SHOULDER}, 
							new Short[]{-900 , 0});
					motion.play(pose,1000);
					motion.waitEndinterpAll();
				}
				CRobotUtil.wait(50);
			}

			pose = new CRobotPose();
			pose.SetPose(new Byte[]{CSotaMotion.SV_L_SHOULDER,CSotaMotion.SV_R_SHOULDER}, 
					new Short[]{-900,900});
			motion.play(pose,1000);
			motion.waitEndinterpAll();

			//サーボトルクオフ
			CRobotUtil.Log(TAG, "Servo Off");
			motion.ServoOff();
		}
	}
}
