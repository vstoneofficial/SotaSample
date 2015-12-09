package jp.vstone.sotasample;

import jp.co.nttit.speechrec.Nbest;
import jp.vstone.RobotLib.CPlayWave;
import jp.vstone.RobotLib.CRobotMem;
import jp.vstone.RobotLib.CRobotUtil;
import jp.vstone.RobotLib.CSotaMotion;
import jp.vstone.sotatalk.SpeechRecog;
import jp.vstone.sotatalk.TextToSpeechSota;

public class SpeechRecGetName {
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
			
			CPlayWave.PlayWave(TextToSpeechSota.getTTSFile("お名前はなんですか？"),true);
			Nbest nbest = recog.getRecognitionNbest(20000);
			String str = recog.getName(nbest);
			CRobotUtil.Log(TAG, str);
			CPlayWave.PlayWave(TextToSpeechSota.getTTSFile(str+ "さん！。覚えたよ！") ,true);
		}
	}
}
