// FPSを計測するためのクラス

package test.sample.TSNdkOpenGL04;

import java.util.LinkedList;

import android.os.SystemClock;

public class FPSClass{
	private int sampleNum;	//　サンプル数

	private float fps;		//　fps
	private long oldTime;	//　前回getFPSを呼び出した時間
	private long diffTime;	//　前回の時間差分
	private long sumTime;	//　経過時間の和
	private LinkedList<Long> timeList;	//　経過時間のテーブル


	//　コンストラクタ
	FPSClass(){
		sampleNum = 10;
		init();
	}

	FPSClass( int sampleNum ){
		this.sampleNum = sampleNum;
		if( this.sampleNum < 1 ) this.sampleNum = 1;	//　１つは用意する
		init();
	}

	//　初期化
	private void init(){
		fps = 0.0f;
		oldTime = 0l;
		diffTime = 0l;
		sumTime = 0l;
		timeList = new LinkedList<Long>();
		for( int i=0 ; i < sampleNum ; i++ ){
			timeList.add( 0l );
		}
	}

	//　fps取得
	public float getFPS(){
		return fps;
	}

	//　fps計算
	public void calcFPS(){
		long nowTime = SystemClock.uptimeMillis();
		diffTime = nowTime - oldTime;
		oldTime = nowTime;

		//　経過時間を加算
		sumTime += diffTime;
		timeList.add( diffTime );	//　リストに追加
		sumTime -= timeList.poll();	//　一番古い時間を消して経過時間から削除

		//　平均時間
		long tmp = sumTime / sampleNum;

		// FPS
		if( tmp != 0l ){
			fps = 1000.0f / tmp;
		} else {
			fps = 0.0f;
		}
	}
}