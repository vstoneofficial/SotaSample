package jp.vstone.sotasample;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Scanner;

import jp.vstone.RobotLib.*;
/**
 * サーボモータの角度読み出しのサンプル
 * 遷移時間を入力し、Enterを押すと角度を読み出し、保存
 * "e"を入力しEnterを押すと、保存を終了し、再生
 * @author Vstone
 *
 */
public class RecordMotion {
	static final String TAG = "RecordMotion";
	
	public static void main(String args[]){
		CRobotUtil.Log(TAG, "Start " + TAG);
		//遷移時間保存用
		ArrayList<Integer> array_time = new ArrayList<Integer>();
		//目標角度保存用
		ArrayList<Short[]> array_pose = new ArrayList<Short[]>();
		
		//VSMDと通信ソケット・メモリアクセス用クラス
		CRobotMem mem = new CRobotMem();
		//Sota用モーション制御クラス
		CSotaMotion motion = new CSotaMotion(mem);
		if(mem.Connect()){
			//Sota仕様にVSMDを初期化
			motion.InitRobot_Sota();
			CRobotUtil.Log(TAG, "Rev. " + mem.FirmwareRev.get());
			
			//サーボモータトルクOn
			CRobotUtil.Log(TAG, "[e]+Enter : end record,and play motion");
			CRobotUtil.Log(TAG, "[numeric]+Enter : save pose with time[msec]");
			
			while(true){
				CRobotUtil.Log(TAG, "save pose:" + array_time.size() +", input move time[msec]:" );
				Scanner scan = new Scanner(System.in);
				String timestr = scan.next();
				if(timestr.equals("e")){
					break;
				}
				try {
					//遷移時間を保存
					array_time.add(Integer.parseInt(timestr));
				} catch (Exception e) {
					break;
				}
				//取得した角度情報を保存
				//取得した角度はgetDefaultIDsに設定されたIDの順番。Sotaは1～8の順
				Short[] rdpos = motion.getReadpos();
				if(rdpos == null){
					return;
				}
				array_pose.add(rdpos);
			}
			CRobotUtil.Log(TAG, "Play Motion");			
			CRobotUtil.Log(TAG, "Servo On");
			
			//サーボモータを現在位置でトルクON
			motion.ServoOn();
			
			//保存したモーションを順次再生
			for (int i = 0; i < array_time.size();i++) {
				CRobotPose pose = new CRobotPose();
				pose.SetPose(motion.getDefaultIDs()						//id
				,  array_pose.get(i)										//target pos
				);
				motion.play(pose,array_time.get(i));
				motion.waitEndinterpAll();
			}
			
			//サーボモータのトルクオフ
			CRobotUtil.Log(TAG, "Servo Off");
			motion.ServoOff();
		}
	}
}
