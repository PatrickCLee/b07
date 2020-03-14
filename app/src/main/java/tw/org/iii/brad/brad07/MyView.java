package tw.org.iii.brad.brad07;
//試著寫打磚塊
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import java.util.Timer;
import java.util.TimerTask;

public class MyView extends View {
    private Bitmap ballBmp;
    private MainActivity activity;
    private Resources resources;
    private Paint paint;
    private int viewW, viewH;
    private float ballW, ballH, ballX, ballY, dx, dy;
    private boolean isInit;
    private Timer timer;
    private GestureDetector gd;

    public MyView(Context context) {
        super(context);
//        setBackgroundColor(Color.YELLOW);
        setBackgroundResource(R.drawable.bg);//此招會將圖片縮放後放入視窗

        activity = (MainActivity) context; //此行之後這個View只活在此Activity底下
        resources = activity.getResources();


        viewW = getWidth(); viewH = getHeight();
        Log.v("brad",viewW + " 1; " + viewH);//此處還沒有處理畫面故寬高尚未初始

        timer = new Timer(); //與系統有關的可塞建構式

        gd = new GestureDetector(new MyGDListener());//橫線表示現在可用但以後可能無法

//        Log.v("brad", " ==> " + (context instanceof MainActivity)); //後面塞字串,故
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {  //所有觸摸事件的源頭
//        Log.v("brad",event.getX() + " : " + event.getY());
        //return super.onTouchEvent(event);     //原始code
        //return gd.onTouchEvent(event);

        float ex = event.getX(), ey = event.getY();
        if (ex>=1350 && ex<=1750 && ey >=550 && ey <= 950){ //設定在畫面右下角的範圍內才能控制
            if(event.getAction() == MotionEvent.ACTION_DOWN){

            }else if(event.getAction() == MotionEvent.ACTION_MOVE){

            }else if(event.getAction() == MotionEvent.ACTION_UP){

            }
            Log.v("brad",event.getX() + " : " + event.getY());
        }
        return true;
    }

    private class MyGDListener extends GestureDetector.SimpleOnGestureListener{
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            Log.v("brad","onFling()" + velocityX + " " + velocityY);

            if(Math.abs(velocityX) > Math.abs(velocityY)){
                //左右
                if(velocityX > 100){
                    // right
                }
                if(velocityX < -100){
                    // left
                }
            }
            if(Math.abs(velocityX) < Math.abs(velocityY)){
                //上下
                if(velocityY < -100){
                    // up
                }
                if (velocityY > 100){
                    // down
                }

            }

            return super.onFling(e1, e2, velocityX, velocityY);

        }

        @Override
        public boolean onDown(MotionEvent e) {
            Log.v("brad","onDown()");
//            return super.onDown(e); //原始code,onTouch有否持續偵測試看此處的回傳
            return true;    //改為true後才看得到onFling
        }
    }

    private void init(){    //用此方法(搭配boolean)可以確保其中的code只做一次
        isInit = true;

        paint = new Paint();
        paint.setAlpha(127);
        ballBmp = BitmapFactory.decodeResource(resources,R.drawable.ball);

        viewW = getWidth(); viewH = getHeight();
        Log.v("brad",viewW + " 2; " + viewH);//在onDraw後出現畫面

        ballW = viewW / 12f; ballH = ballW; //做float,將球的寬高做成視窗的1/8

        Matrix matrix = new Matrix(); //負責轉換的物件
        matrix.postScale(ballW / ballBmp.getWidth(), ballH / ballBmp.getHeight());
        //原值 乘上sx 等於 你要設定的值, 故sx等於你要設定的值除以原值
        ballBmp = Bitmap.createBitmap(ballBmp,0,0,      //這兩行叫做裁切
                ballBmp.getWidth(), ballBmp.getHeight(),matrix,false);
        //若有多樣物件要設定,則設定完一樣後 matrix.reset();就可繼續用,不需再new一個matrix

        ballX = ballY = 100;
        dx = dy = 18;
        timer.schedule(new RefreshView(),0,17);
        timer.schedule(new BallTask(), 1*1000, 30);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(!isInit)init();
        canvas.drawBitmap(ballBmp,ballX,ballY,null);//此招圖片不會縮放,paint在影像中可處理透明度

    }

    private class RefreshView extends TimerTask{
        @Override
        public void run() {
            postInvalidate();
        }
    }

    private class BallTask extends TimerTask{

        @Override
        public void run() {
            if(ballX < 0 || ballX + ballW > viewW){
                dx *= -1;
            }
            if(ballY < 0 || ballY + ballH > viewH){
                dy *= -1;
            }
            ballX += dx;
            ballY += dy;
            //postInvalidate();//做完後再更新畫面
        }
    }
}
