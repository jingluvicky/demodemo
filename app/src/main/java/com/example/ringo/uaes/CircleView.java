package com.example.ringo.uaes;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class CircleView extends View {

    // int X=500,Y=700;
    int relativeX,relativeY;

    //无参
    public CircleView(Context context) {
        super(context);
    }

    //有参
    public CircleView(Context context, AttributeSet attrs){
        super(context,attrs);
    }

    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);

        int canvasWidth = canvas.getWidth();
        int canvasHeight = canvas.getHeight();


        relativeX = MainTabLocationoutputFragment.X/2 + canvasWidth/2;
        relativeY = canvasHeight/2 - MainTabLocationoutputFragment.Y/2;

        // 创建圆点笔刷
        Paint p = new Paint();
        p.setColor(Color.RED);
        p.setStrokeWidth(4.0f);

        //创建文本笔刷
        Paint p1 = new Paint();
        p1.setColor(Color.BLACK);
        p1.setStrokeWidth(5f);
        p1.setTextSize(18);

        //绘制圆点
        canvas.drawCircle(relativeX,relativeY,20,p);

        String s = "("+MainTabLocationoutputFragment.X+","+MainTabLocationoutputFragment.Y+")";
        canvas.drawText(s,relativeX-10,relativeY-2,p1);

    }

}
