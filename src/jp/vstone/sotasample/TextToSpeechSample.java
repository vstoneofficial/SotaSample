package jp.vstone.sotasample;

import jp.co.nttit.speechrec.Nbest;
import jp.vstone.RobotLib.CPlayWave;
import jp.vstone.RobotLib.CRobotUtil;
import jp.vstone.sotatalk.SpeechRecog;
import jp.vstone.sotatalk.TextToSpeechSota;

public class TextToSpeechSample {
	static final String TAG = "SpeechRecSample";
	public static void main(String[] args) {
		CPlayWave.PlayWave(TextToSpeechSota.getTTS("やっほー"),true);
		CPlayWave.PlayWave(TextToSpeechSota.getTTS("僕の名前はSotaです。"),true);
		byte[] data = TextToSpeechSota.getTTS("これから、よろしくね！");
		if(data == null){
			CRobotUtil.Log(TAG,"ERROR");
		}
		CPlayWave.PlayWave(data,true);

		CPlayWave.PlayWave(TextToSpeechSota.getTTS("僕の名前はSotaです。僕の名前はSotaです。僕の名前はSotaです。僕の名前はSotaです。僕の名前はSotaです。僕の名前はSotaです。僕の名前はSotaです。僕の名前はSotaです。"),true);
	}
}
