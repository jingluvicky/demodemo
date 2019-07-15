package com.example.ringo.uaes;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class coordinateView extends View {

    //无参
    public coordinateView(Context context) {
        super(context);
    }

    //有参
    public coordinateView (Context context, AttributeSet attrs){
        super(context,attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int canvasWidth = canvas.getWidth();
        int canvasHeight = canvas.getHeight();

        int Rec_Distance = 300;

        // 创建笔刷
        Paint p = new Paint();
        p.setColor(Color.BLACK);
        p.setStyle(Paint.Style.FILL);
        p.setStrokeWidth(5f);


        Path path = new Path();
        Path path1 = new Path();

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(5f);

        Paint mPaint = new Paint();
        mPaint.setColor(Color.RED);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(4f);


        //绘制坐标轴
        paint.setColor(Color.BLACK);
        canvas.drawLine(0, canvasHeight/2, canvasWidth-20, canvasHeight/2, paint);//绘制x轴
        canvas.drawLine(canvasWidth/2, canvasHeight, canvasWidth/2, 20, paint);//绘制y轴

        path.moveTo(canvasWidth-20, canvasHeight/2-8);
        path.lineTo(canvasWidth, canvasHeight/2);
        path.lineTo(canvasWidth-20,canvasHeight/2+8);
        path.close();//闭环
        canvas.drawPath(path, p);

        path.moveTo(canvasWidth/2-8, 20);
        path.lineTo(canvasWidth/2, 0);
        path.lineTo(canvasWidth/2+8,20);
        path.close();//闭环
        canvas.drawPath(path, p);

        //绘制车外的区域
        int relativeX_Left = -(Rec_Distance/2+50) + canvasWidth/2;
        int relativeY_Left = -(Rec_Distance/2+113) + canvasHeight/2;
        int relativeX_Right = (Rec_Distance/2+50) + canvasWidth/2;
        int relativeY_Right = (Rec_Distance/2+113) + canvasHeight/2;
        RectF rectF = new RectF(relativeX_Left,relativeY_Left,relativeX_Right,relativeY_Right);
        canvas.drawRoundRect(rectF,Rec_Distance/2,Rec_Distance/2,mPaint);

    }
}

