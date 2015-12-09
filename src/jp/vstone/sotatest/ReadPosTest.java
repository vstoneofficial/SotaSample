package jp.vstone.sotatest;

import jp.vstone.RobotLib.*;
/**
 * 関節角度を読みだした結果を表示するサンプルプログラム
 * @author Vstone
 */
public class ReadPosTest {
	static final String TAG = "ButtonReadSample";
	public static void main(String args[]){
		CRobotUtil.Log(TAG, "Start " + TAG);
	
		//VSMDと通信ソケット・メモリアクセス用クラス
		CRobotMem mem = new CRobotMem();
		//Sota用モーション制御クラス
		CSotaMotion motion = new CSotaMotion(mem);
		if(mem.Connect()){
			//Sota仕様にVSMDを初期化
			motion.InitRobot_Sota();
			CRobotUtil.Log(TAG, "Rev. " + mem.FirmwareRev.get());
			
			while(true){
				//いずれかのボタンを押すと終了
				if(motion.isButton_Power()){
					break;
				}
				if(motion.isButton_VolDown()){
					break;
				}				
				else if(motion.isButton_VolUp()){
					break;
				}
				
				Short[] pos = motion.getReadpos();
				System.out.print("\r");
				for(int i = 0; i < pos.length ; i++){
					System.out.print(String.format("S%d=%3d, ",i+1,pos[i]));
				}
				
				CRobotUtil.wait(100);
			}
			
			CRobotUtil.Log(TAG, "Servo Off");
			motion.ServoOff();
		}
	}
}
