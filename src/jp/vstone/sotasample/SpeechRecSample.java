package jp.vstone.sotasample;

import jp.co.nttit.speechrec.Nbest;
import jp.vstone.RobotLib.CPlayWave;
import jp.vstone.RobotLib.CRobotMem;
import jp.vstone.RobotLib.CRobotUtil;
import jp.vstone.RobotLib.CSotaMotion;
import jp.vstone.sotatalk.SpeechRecog;
import jp.vstone.sotatalk.TextToSpeechSota;

public class SpeechRecSample {
	static final String TAG = "SpeechRecSample";
	public static void main(String[] args) {
		//VSMDと通信ソケット・メモリアクセス用クラス
		CRobotMem mem = new CRobotMem();
		//Sota用モーション制御クラス
		CSotaMotion motion = new CSotaMotion(mem);
		SpeechRecog recog = new SpeechRecog(motion);
		
		if(mem.Connect()){
			//Sota仕様にVSMDを初期化
			motion.InitRobot_Sota();
			while(true){
				String str = recog.getRecognition(20000);
				CPlayWave.PlayWave(TextToSpeechSota.getTTSFile(str),true);
			
				//str.matches("");//正規表現
				if(str.contains("おわり")){//含むか			
					CPlayWave.PlayWave(TextToSpeechSota.getTTSFile("シューりょー"),true);
					break;
				}
			}
		}
	}
}
