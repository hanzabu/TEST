package test.sample.TSNdkOpenGL04;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class TSNdkOpenGL04 extends Activity implements GLSurfaceView.Renderer{
	// NDKload
	static {
        System.loadLibrary("NDKOpenGL");
    }
	//　native
    public native void ndkOpenGLInit();		//　初期化
    public native void ndkSurfaceChanged( int w, int h );	//　画面変更
    public native void ndkOpenGLDraw();		//　描画

    //　View
    private GLSurfaceView glView = null;

    //　デバッグクラス
	private DebugClass dc = null;
	private StringBuffer sb;	// デバッグ用
	private long oldTime = 0;	//　時間記憶用
	private final long waitTime = 300;	//　待機時間
	private boolean realDebugFlag = false;	//　毎フレームFPS表示するかのフラグ
	private boolean debugDrawFlag = true;	//　デバッグ描画フラグ
	private boolean debugLogFlag = false;	//　デバッグログフラグ
	private String debugString = "TSNdkOpenGL04";
	
	//　FPS測定クラス
	private FPSClass fps;

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        glView = new GLSurfaceView( this );
        glView.setRenderer( this );

        setContentView( glView );

		// FPS測定用
		fps = new FPSClass( 20 );	//　とりあえず
		//　テキストバッファの生成
		sb = new StringBuffer( 100 );
    }

    @Override
    public void onResume(){
    	super.onResume();
    	if( glView != null ){
    		glView.onResume();
    	}
    	if( dc != null ){
    		dc.onResume();
    	}
    }

    @Override
    public void onPause(){
    	super.onPause();
    	if( glView != null ){
    		glView.onPause();
    	}
    	if( dc != null ){
    		dc.onPause();
    	}
    }

    //　初期化
    public void onSurfaceCreated( GL10 gl, EGLConfig config ){
		//　デバッグ生成
		dc = new DebugClass( gl );

		ndkOpenGLInit();	//　初期化
    }


    //　サーフェース変更
    public void onSurfaceChanged( GL10 gl, int width, int height ){
    	dc.setSize( width, height );	//　デバッグ表示位置の変更
    	ndkSurfaceChanged( width, height );	//　画面変更
    }


    //　描画
    public void onDrawFrame( GL10 gl ){
		fps.calcFPS();	// fps計算

		this.debugSet();	//　デバッグ描画用意

		ndkOpenGLDraw();		//　描画

		//　デバッグ描画
    	this.debugDraw( gl );
    }

    //　デバッグ描画
    private void debugDraw( GL10 gl ){
		if( debugDrawFlag ){
			dc.draw( gl );
		}
    }
    
	//　デバッグ描画用意
    private void debugSet(){
    	boolean debugsub = false;
    	long nowTime = 0;
    	
    	//　デバッグ描画
		if( debugDrawFlag ){
			if( realDebugFlag ){
				debugSetSub();	//　毎フレームセット
			} else {
				nowTime = SystemClock.uptimeMillis();
				if( ( nowTime - oldTime ) > waitTime ){
					debugsub = true;
					debugSetSub();
				}
			}
		}
		
		//　ログ出力
		if( debugLogFlag ){

			if( realDebugFlag ){
				if( !debugDrawFlag ){
					sb.delete( 0, sb.length() );
					sb.append( "FPS : " ).append( String.format( "%3.2f", fps.getFPS() ) );
				}
				Log.d( debugString, sb.toString() );

			} else {

				if( !realDebugFlag ){
					nowTime = SystemClock.uptimeMillis();
					if( ( nowTime - oldTime ) > waitTime ){
						debugsub = true;
					}
				}
				if( debugsub ){
					oldTime = nowTime;
					if( !debugDrawFlag ){
						sb.delete( 0, sb.length() );
						sb.append( "FPS : " ).append( String.format( "%3.2f", fps.getFPS() ) );
					}
					Log.d( debugString, sb.toString() );
				}
			}
		}
    }
    
    //　デバッグ内容テクスチャセット
    private void debugSetSub(){
		dc.init();

		sb.delete( 0, sb.length() );
		sb.append( "FPS : " ).append( String.format( "%3.2f", fps.getFPS() ) );
		dc.setString( sb.toString() );
    }
    
    //　デバッグ設定メニュー
    @Override
    public boolean onCreateOptionsMenu( Menu menu ){
    	super.onCreateOptionsMenu(menu);
    	
    	menu.add( 0, 0, Menu.NONE, "Draw OnOff" );
    	menu.add( 0, 1, Menu.NONE, "Log OnOff" );
    	menu.add( 0, 2, Menu.NONE, "Timing change" );

    	return true;
    }
    
    //　デバッグ設定
    @Override
    public boolean onOptionsItemSelected( MenuItem item ){
    	switch( item.getItemId() ){
    	case 0 :
    		if( debugDrawFlag ){
    			debugDrawFlag = false;
    		} else {
    			debugDrawFlag = true;
    		}
    		break;

    	case 1 :
    		if( debugLogFlag ){
    			debugLogFlag = false;
    		} else {
    			debugLogFlag = true;
    		}
    		break;

    	case 2 :
    		if( realDebugFlag ){
    			realDebugFlag = false;
    		} else {
    			realDebugFlag = true;
    		}
    		break;
    	}
    	
    	return true;
    }
}