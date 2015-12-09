package jp.vstone.sotatest;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;

import jp.vstone.RobotLib.*;
import jp.vstone.camera.CameraCapture;
/**
 * VSMDを使用し、モーション再生・音声再生するサンプル
 * @author Vstone
 *
 */
public class MicTest {
	static final String TAG = "MotionSample";
	public static void main(String args[]){
		CRobotUtil.Log(TAG, "Start " + TAG);

		//音声ファイル録音
		CRobotUtil.Log(TAG, "Mic Recording Test");
		CPlayWave.PlayWave_wait("sound/start_rec_test.wav");
		CRecordMic mic = new CRecordMic();
		mic.startRecording("./test_rec.wav",5000);
		CRobotUtil.Log(TAG, "wait end");
		mic.waitend();
		
		CRobotUtil.Log(TAG, "Spk Play Test");
		//音声ファイル再生
		CPlayWave.PlayWave_wait("./test_rec.wav");
	    
		//音声ファイル再生
		//raw　Waveファイルのみ対応
		CPlayWave.PlayWave("sound/end_test.wav");
		CRobotUtil.wait(2000);
		
	}
}
