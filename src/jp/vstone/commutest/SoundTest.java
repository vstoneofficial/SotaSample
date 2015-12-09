package jp.vstone.commutest;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Arrays;
import java.util.Date;

import javax.xml.crypto.Data;

import jp.vstone.RobotLib.*;
/**
 * VSMDを使用し、モーション再生・音声再生するサンプル
 * @author Vstone
 *
 */
public class SoundTest {
	static final String TAG = "SoundTest";
	public static void main(String args[]){
		CRobotUtil.Log(TAG, "Start " + TAG);

		//VSMDと通信ソケット・メモリアクセス用クラス
		CRobotMem mem = new CRobotMem();
		//CommU用モーション制御クラス
		CCommUMotion motion = new CCommUMotion(mem);
		
		//RaspberryPi版はKernelにバグがあるので、2015/3/4現在LEDとカメラの同時使用ができません。
		//カメラを使用される場合、以下でI2Cを無効化してください。
		//mem.I2CSendEN.set(0);
		
		if(mem.Connect()){
			//Sota仕様にVSMDを初期化
			motion.InitRobot_CommU();
			
			CRobotUtil.Log(TAG, "Rev. " + mem.FirmwareRev.get());

			//音声ファイル録音
			CRobotUtil.Log(TAG, "Mic Recording Test");
			CRecordMic mic = new CRecordMic();
			mic.startRecording("test_rec.wav",5000);
			mic.waitend();
			
			CRobotUtil.Log(TAG, "Mic Recording Test End");
			
			//音声ファイル再生
			//raw　Waveファイルのみ対応
			//CPlayWave2 sound = CPlayWave2.CreatePlayWave("test_rec.wav");
			CPlayWave.PlayWave_wait("test_rec.wav");
			CPlayWave.PlayWave_wait("sound/chanto_kao.wav");
		}
	}
}
