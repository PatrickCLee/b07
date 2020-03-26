package tw.org.iii.brad.brad07;
//ball的撞邊框彈跳,試著寫打磚塊
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
    private float ballW, ballH, ballX, ballY, dx, dy;//球的寬高為float因畫在畫布上(onDraw)時可傳入float;dx,dy為移動的距離
    private boolean isInit;
    private Timer timer;
    private GestureDetector gd; //手勢偵測,也是個View

    public MyView(Context context) {    //建構做針對物件屬性的特徵
        super(context);
//        setBackgroundColor(Color.YELLOW);
        setBackgroundResource(R.drawable.bg);//此招會將圖片縮放後放入視窗

//        Log.v("brad", " ==> " + (context instanceof MainActivity)); //後面塞字串,故弄個箭頭,此處證明context就是MainActivity
        activity = (MainActivity) context; //此行之後這個View只活在此Activity底下
        resources = activity.getResources();


        viewW = getWidth(); viewH = getHeight();
        Log.v("brad",viewW + " 1; " + viewH);//此處還沒有處理畫面故寬高尚未初始,onDraw時才成像,才有寬高

        timer = new Timer(); //與系統有關的可塞建構式

        gd = new GestureDetector(new MyGDListener());//橫線表示現在可用但以後可能無法

    }
//------------------以下是手勢滑動------------------------------------
    @Override
    public boolean onTouchEvent(MotionEvent event) {  //所有觸摸事件的源頭
//        Log.v("brad",event.getX() + " : " + event.getY()); //抓螢幕上的位置
        //return super.onTouchEvent(event);     //原始code,不會觸發gd內的code
        //return gd.onTouchEvent(event);    //交給下方寫的手勢偵測,但因此處還要限制範圍,故改為true

        float ex = event.getX(), ey = event.getY();
        if (ex>=1350 && ex<=1750 && ey >=550 && ey <= 950){ //設定在畫面右下角的範圍內才能控制
            if(event.getAction() == MotionEvent.ACTION_DOWN){
                //按下
            }else if(event.getAction() == MotionEvent.ACTION_MOVE){
                //移動
            }else if(event.getAction() == MotionEvent.ACTION_UP){
                //起手
            }
            Log.v("brad",event.getX() + " : " + event.getY());
        }
        return true;
    }

    private class MyGDListener extends GestureDetector.SimpleOnGestureListener{
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            Log.v("brad","onFling()" + velocityX + " " + velocityY);

            if(Math.abs(velocityX) > Math.abs(velocityY)){ //取絕對值,X的絕對值大於Y的絕對值時就是要左右
                //左右
                if(velocityX > 100){
                    // right
                }
                if(velocityX < -100){
                    // left
                }
            }
            if(Math.abs(velocityX) < Math.abs(velocityY)){ //反之
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
//            return super.onDown(e); //原始code,onTouch有否持續偵測是看此處的回傳
            return true;    //改為true後才看得到onFling
        }
    }
//------------------以上是手勢滑動------------------------------------

    private void init(){    //init針對知道寬高後做的事;用此方法(搭配boolean)可以確保其中的code只做一次
        isInit = true;

        paint = new Paint();
        paint.setAlpha(127);
        ballBmp = BitmapFactory.decodeResource(resources,R.drawable.ball);

        viewW = getWidth(); viewH = getHeight();
        Log.v("brad",viewW + " 2; " + viewH);//在onDraw後出現畫面

        ballW = viewW / 12f; ballH = ballW; //做float,將球的寬高做成視窗的1/8,此處只有抓到想要設定的值,尚未真正造成影響

        Matrix matrix = new Matrix(); //負責轉換的物件
        matrix.postScale(ballW / ballBmp.getWidth(), ballH / ballBmp.getHeight());
        //原值 乘上sx 等於 你要設定的值, 故sx等於你要設定的值除以原值
        ballBmp = Bitmap.createBitmap(ballBmp,0,0,      //這兩行叫做裁切,傳入matrix(以剛才設定的matrix比例去做才切)
                ballBmp.getWidth(), ballBmp.getHeight(),matrix,false);
        //若有多樣物件要設定,則設定完一樣後 matrix.reset();就可繼續用,不需再new一個matrix

        ballX = ballY = 0;
        dx = dy = 18;
        timer.schedule(new RefreshView(),0,17);
        timer.schedule(new BallTask(), 1*1000, 30);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(!isInit)init();      //init在此
        canvas.drawBitmap(ballBmp,ballX,ballY,paint);//畫在畫布上;此招圖片不會縮放,paint在影像中可處理透明度

    }

    private class RefreshView extends TimerTask{    //fps 照時間固定更新畫面,此處設為fps60(見上方127行
        @Override
        public void run() {
            postInvalidate();
        }
    }

    private class BallTask extends TimerTask{

        @Override
        public void run() {
            if(ballX < 0 || ballX + ballW > viewW){ //撞牆
                dx *= -1;
            }
            if(ballY < 0 || ballY + ballH > viewH){ //撞牆
                dy *= -1;
            }
            ballX += dx;
            ballY += dy;
            //postInvalidate();//執行序不是用invalidate,要用postInvalidate,此處之所以不做,
            //原因同java課時說的,若有多顆球就會瘋狂更新畫面
        }
    }
}
