/* NDKOpenGL.c */

#include <jni.h>
#include <android/log.h>
#include <GLES/gl.h>
#include <stdlib.h>
#include <math.h>

#define DEBUG

#ifdef DEBUG

#define  LOG_TAG    "TS Ndk OpenGL"
#define  LOGI(...)  __android_log_print( ANDROID_LOG_INFO , LOG_TAG , __VA_ARGS__ )
#define  LOGE(...)  __android_log_print( ANDROID_LOG_ERROR, LOG_TAG , __VA_ARGS__ )
#define  LOGD(...)  __android_log_print( ANDROID_LOG_DEBUG , LOG_TAG , __VA_ARGS__ )
#define  LOGV(...)  __android_log_print( ANDROID_LOG_VERBOSE , LOG_TAG , __VA_ARGS__ )
#define  LOGW(...)  __android_log_print( ANDROID_LOG_WARN , LOG_TAG , __VA_ARGS__ )

#else

#define  LOGI(...)  {}
#define  LOGE(...)  {}
#define  LOGD(...)  {}
#define  LOGV(...)  {}
#define  LOGW(...)  {}

#endif


static int drawWidth = 320;
static int drawHeight = 480;
#define drawNum 1000
#define ONE 0x10000

GLfixed vertex[drawNum*2*3];
//int vertex[drawNum*2*3];
unsigned char colors[drawNum*4*3];
//int colors[drawNum*4*3];


//　宣言
void setTriangle( int x, int y, int size, int num );
void drawTriangle( int num );

void Java_test_sample_TSNdkOpenGL04_TSNdkOpenGL04_ndkOpenGLInit( JNIEnv*  env )
{
	char *p;

#ifdef DEBUG
	//　OpenGL情報を拾ってくる
	p = (char *)glGetString(GL_VERSION);
	LOGI( "VERSION = %s", p );

	p = (char *)glGetString(GL_VENDOR);
	LOGI( "VENDOR = %s", p );

	p = (char *)glGetString(GL_RENDERER);
	LOGI( "RENDERER = %s", p );

	p = (char *)glGetString(GL_EXTENSIONS);
	LOGI( "EXTENSIONS = %s", p );
#endif

	glClearColor( 0.0f, 0.0f, 0.0f, 1.0f );		//　サーフェイスクリア色の指定　RGBA


	glShadeModel( GL_FLAT );
	glHint( GL_PERSPECTIVE_CORRECTION_HINT, GL_FASTEST );
}

//　サーフェイス変更
void Java_test_sample_TSNdkOpenGL04_TSNdkOpenGL04_ndkSurfaceChanged( JNIEnv*  env, jobject obj,  jint width, jint height )
{
	// 記憶してるだけ
	drawWidth = width;
	drawHeight = height;


    //　射影行列の指定
    
    glMatrixMode( GL_PROJECTION );	//　射影行列（プロジェクションモード）
    glLoadIdentity();	//　単位行列のセット
	glViewport( 0, 0, width, height );
	glScissor( 0, 0, width, height );
    glOrthof( 0.0f, (float)width, 0.0f, (float)height, -1.0f, 1.0f );
//    glOrthox( 0, width, 0, height, -1, 1 );


	glMatrixMode(GL_MODELVIEW);
	glLoadIdentity();

    glEnableClientState( GL_VERTEX_ARRAY );	//　頂点配列の許可
    glEnableClientState( GL_COLOR_ARRAY );	//　色情報配列の許可
    glDisable( GL_TEXTURE_2D );	//　テクスチャは使わない
	glEnable( GL_BLEND );
	glBlendFunc( GL_SRC_ALPHA, GL_ONE_MINUS_SRC_COLOR );

	LOGI( "ndkSurfaceChanged = %d, %d", drawWidth, drawHeight );
}


//　描画
void Java_test_sample_TSNdkOpenGL04_TSNdkOpenGL04_ndkOpenGLDraw( JNIEnv*  env )
{
//	LOGI( "ndkOpenGLDraw" );

	glClear( GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT );	//　描画バッファクリア＆背景塗りつぶし

    //　射影行列の指定
    glDisable( GL_TEXTURE_2D );	//　テクスチャは使わない

   	int x = 0;
   	int y = 0;
   	int i = 0, j = 0;
   	int size = 1;
 
   	for( i=0 ; i<drawNum ; i++ ){
   		if( drawWidth != 0 ){
    		x = rand() % drawWidth;
    	}
    	if( drawHeight != 0 ){
    		y = rand() % drawHeight;
    	}
    	size = ( rand() % 10 )*2 + 4;
//    	size = 10;
    	setTriangle( x*ONE, y*ONE, size*ONE, i );
    	
    	for( j=0 ; j<4*3 ; j++ ){
    		if( ( j % 4 ) == 3 ){
    			colors[i*4*3 + j] = 0x80;
//    			colors[i*4*3 + j] = (int)0.5f*ONE;
//    			colors[i*4*3 + j] = (int)1.0f*ONE;
    		} else {
    			colors[i*4*3 + j] = 0xff;
//    			colors[i*4*3 + j] = (int)0.5f*ONE;
    		}
    	}
    }
    drawTriangle( drawNum );
}

//　三角登録
void setTriangle( int x, int y, int size, int num ){
	vertex[num*6] = x;
	vertex[num*6+1] = y;
	vertex[num*6+2] = x - size/2;
	vertex[num*6+3] = y - size;
	vertex[num*6+4] = x + size/2;
	vertex[num*6+5] = y - size;
}

//　三角描画
void drawTriangle( int num ){
//    LOGI( "X,Y = %d,%d", x, y );
    
	glVertexPointer( 2, GL_FIXED, 0, vertex );	//　表示座標のセット
	glColorPointer( 4, GL_UNSIGNED_BYTE, 0, colors );	//　カラーのセット
//	glColorPointer( 4, GL_FIXED, 0, colors );	//　カラーのセット
    glDrawArrays( GL_TRIANGLES, 0, num*3 );	//　登録数*3だけ描画する
}
