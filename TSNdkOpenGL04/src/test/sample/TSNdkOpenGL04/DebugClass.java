// デバッグ表示するためのクラス

package test.sample.TSNdkOpenGL04;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;
import javax.microedition.khronos.opengles.GL11Ext;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.opengl.GLUtils;

class DebugClass{
	public final static int ONE = 0x10000;	// 固定小数点で=1

	private Bitmap mBitmap = null;
    private Canvas mCanvas = null;
	private Paint mPaint = null;

	private int mTextureID;
    private static final int WIDTH = 128;
    private static final int HEIGHT = 32;

    private int mColor = Color.argb( 255, 255, 255, 255 );
    private int mFontSize = 16;
    private int drawLine = 0;
    
    private int mWidth = 0;
    private int mHeight = 0;
    
    //　コンストラクタ
    DebugClass( GL10 gl ){
    	mPaint = new Paint();
    	mPaint.setAntiAlias( true );
    	
        int[] textures = new int[1];
        gl.glGenTextures(1, textures, 0);
        mTextureID = textures[0];
        gl.glBindTexture(GL10.GL_TEXTURE_2D, mTextureID );
    	
        //　テクスチャのパラメータ設定
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER,
                GL10.GL_NEAREST);	//　縮小するときピクセルの中心に最も近いテクスチャ要素で補完
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER,
                GL10.GL_LINEAR);	//　拡大するときピクセルの中心付近の線形で補完

        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S,
                GL10.GL_CLAMP_TO_EDGE);	//　s座標の1を超える端処理をループにしない
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T,
                GL10.GL_CLAMP_TO_EDGE);	//　t座標の1を超える端処理をループにしない

        gl.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE,
                GL10.GL_REPLACE);	//　テクスチャとモデルの合成方法の指定（この場合置き換え）

        this.onResume();
		this.setFontSize( mFontSize );
		this.setColor( mColor );
        drawLine = 0;
    }
 
    
    //　初期化
    public void init(){
//        mBitmap.eraseColor( Color.argb( 128, 255, 255, 255 ) );
        mBitmap.eraseColor( 0 );
        drawLine = 0;
    }
    
    
    public void setSize( int width, int height ){
    	this.mWidth = width;
    	this.mHeight = height;
    }
    
    
    //　メッセージ登録
    public void setString( String str ){
    	if( str != null )
    	this.setString( 0, drawLine, mFontSize, mColor, str );
    }
    
    //　座標つきメッセージ登録
    public void setString( int x, int y, String str ){
    	if( str != null )
    	this.setString( x, y, mFontSize, mColor, str );
    }

    //　座標、サイズつきメッセージ登録
    public void setString( int x, int y, int size, String str ){
    	if( str != null )
    	this.setString( x, y, size, mColor, str );
    }

    
    //　座標、サイズ、色指定つきメッセージ登録
    public void setString( int x, int y, int size, int color, String str ){
    	if( str != null ){
    		this.setFontSize( size );
    		this.setColor( color );
    		drawLine = y + size;

    		if( mCanvas != null ){
    			mCanvas.drawText( str, x, drawLine, mPaint );
    		}
    	}
    }
    
    //　フォントサイズ
    public void setFontSize( int fontSize ){
    	mFontSize = fontSize;
		mPaint.setTextSize( fontSize );
    }
    
    //　カラー
    public void setColor( int color ){
    	mColor = color;
		mPaint.setColor( color );
    }
    
    
    //　終了
    public void end( GL10 gl ){
        gl.glBindTexture( GL10.GL_TEXTURE_2D, mTextureID );
        if( mBitmap != null ){
        	GLUtils.texImage2D( GL10.GL_TEXTURE_2D, 0, mBitmap, 0 );
        }

/*        
        if( mBitmap != null ){
    		mBitmap.recycle();
    		mBitmap = null;
    	}
    	
        if( mCanvas != null ){
        	mCanvas = null;
        }
*/
    }
    
    //　描画
    public void draw( GL10 gl ){
    	this.end( gl );	//　閉じる
    	
    	//　テクスチャに貼り付ける
    	gl.glEnable( GL10.GL_TEXTURE_2D );	//　テクスチャを使う
        gl.glActiveTexture( GL10.GL_TEXTURE0 );
        gl.glBindTexture( GL10.GL_TEXTURE_2D, mTextureID );

        //　半透明
        gl.glEnable( GL10.GL_BLEND );
    	gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE);
    	gl.glDisable( GL10.GL_DEPTH_TEST );	//　深度テストを行なわない

    	// 座標と、幅・高さを指定
        int[] rect = {0, HEIGHT, WIDTH, -HEIGHT};	//　上下逆転に注意
        // バインドされているテクスチャのどの部分を使うかを指定
        ((GL11)gl).glTexParameteriv( GL10.GL_TEXTURE_2D, GL11Ext.GL_TEXTURE_CROP_RECT_OES, rect, 0);

        //　表示位置は左上になる
        ((GL11Ext)gl).glDrawTexfOES( 0, mHeight - HEIGHT, 0, WIDTH, HEIGHT );
    }
    
    public void onResume(){
    	if( mBitmap != null ){
    		mBitmap.recycle();
    		mBitmap = null;
    	}
        mBitmap = Bitmap.createBitmap( WIDTH, HEIGHT, Bitmap.Config.ARGB_4444 );

        if( mCanvas != null ){
        	mCanvas = null;
        }
        mCanvas = new Canvas( mBitmap );
    }

    public void onPause(){
        if( mBitmap != null ){
    		mBitmap.recycle();
    		mBitmap = null;
    	}
        if( mCanvas != null ){
        	mCanvas = null;
        }
    }
}