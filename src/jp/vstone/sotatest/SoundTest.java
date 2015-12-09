package jp.vstone.sotatest;
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
		
		CPlayWave.PlayWave_wait("sound/chanto_kao1.wav");
		CRobotUtil.Log(TAG, "Servo Off");
	}
	
}
