package com.example.ringo.uaes;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

public class DataService extends Service implements SensorEventListener {
	
	private DataService service = this;
	private SensorManager sensorManager;
	private Sensor sensor;
	private ImageView icon;
	private MyBinder binder = new MyBinder();
	
	private Long startTimestamp = 0L;

	private final String pathName = Environment.getExternalStorageDirectory().getPath()+"/SensorW/";
	private String fileName = "";
	private File file, path;
	private FileOutputStream fos;
    public long serialNumber;
    public float[] orientationValue;
	public float[] accelerometerValues; //加速度传感器 x y z m/s^2
	public float[] gyroscopeValues;      //陀螺仪 rad/s x y z
	public float[] magneticValues;       //磁场强度   x y z uH
	public float[] gravityValues;         //重力传感器 x y z m/s^2
	public float proximityValues;      //障碍物距离 cm
	public float lightValues,yaw,pitch,mRoll,a_tmp1,a_tmp2,a_tmp3,vx=0,vy=0,vz=0,sx=0,sy=0,sz=0,counter=0,sum=0;           //光强  lx
	public float[] rotationValues,r;         //方向 x y z m/s^2
	public float[] linearaccelerationValues;  //线性加速度传感器 x y z
	public int  mainRSSIValue;           //主蓝牙模块场强
	public int  assistRSSIValue_1;           //辅模块1场强
	public int  assistRSSIValue_2;           //辅模块2场强
	public int  assistRSSIValue_3;           //辅模块3场强
	public int  assistRSSIValue_4;           //辅模块4场强
	public int  assistRSSIValue_5;           //辅模块5场强
	public int  assistRSSIValue_6;           //辅模块6场强
	public int  assistRSSIValue_7;           //辅模块7场强
	public int  assistRSSIValue_8;           //辅模块8场强
	public int  assistRSSIValue_9;           //辅模块9场强
    public int assistRSSIValidity_1;         //辅模块1有效性
	public int assistRSSIValidity_2;         //辅模块2有效性
	public int assistRSSIValidity_3;         //辅模块3有效性
	public int assistRSSIValidity_4;         //辅模块4有效性
	public int assistRSSIValidity_5;         //辅模块5有效性
	public int assistRSSIValidity_6;         //辅模块6有效性
	public int assistRSSIValidity_7;         //辅模块7有效性
	public int assistRSSIValidity_8;         //辅模块8有效性
	public int assistRSSIValidity_9;         //辅模块9有效性

	public int zoneNum;
	public int motion;
	public int classNum;
	public float[]R=new float[9];
public float walk;


	private static final int FILTERWND = 10;
	public float[][] gravityBuffer;
	public float[]   proximityBuffer;
	public float[] filteredGravity;
	public float  filteredProximity;
	private Handler mHandler;
	private static final long GET_PERIOD = 20;

	public void initSensorDataGroup(){

	    accelerometerValues=new float[3] ; //加速度传感器 x y z m/s^2
		gyroscopeValues=new float[3] ;      //陀螺仪 rad/s x y z
		magneticValues=new float[3] ;      //磁场强度   x y z uH
		gravityValues=new float[3] ;         //重力传感器 x y z m/s^2
		orientationValue=new float[3];
		proximityValues=0 ;     //障碍物距离 cm
		lightValues=0;           //光强  lx
		rotationValues=new float[3] ;      //方向 x y z m/s^2
		linearaccelerationValues=new float[3] ;;  //线性加速度传感器 x y z

			for (int i = 0; i <= 9; i++) {
				if (MainTabScanFragment.Nodes[i]==null)MainTabScanFragment.Nodes[i]=new Node();
				MainTabScanFragment.Nodes[i].RSSI_filtered=0;
			}

			mainRSSIValue = (int) MainTabScanFragment.Nodes[0].RSSI_filtered;           //主蓝牙模块场强
			assistRSSIValue_1 = (int) MainTabScanFragment.Nodes[1].RSSI_filtered;           //辅模块1场强
			assistRSSIValue_2 = (int) MainTabScanFragment.Nodes[2].RSSI_filtered;           //辅模块2场强
			assistRSSIValue_3 = (int) MainTabScanFragment.Nodes[3].RSSI_filtered;           //辅模块3场强
			assistRSSIValue_4 = (int) MainTabScanFragment.Nodes[4].RSSI_filtered;           //辅模块1场强
			assistRSSIValue_5 = (int) MainTabScanFragment.Nodes[5].RSSI_filtered;           //辅模块2场强
			assistRSSIValue_6 = (int) MainTabScanFragment.Nodes[6].RSSI_filtered;           //辅模块3场强
			assistRSSIValue_7 = (int) MainTabScanFragment.Nodes[7].RSSI_filtered;           //辅模块1场强
			assistRSSIValue_8 = (int) MainTabScanFragment.Nodes[8].RSSI_filtered;           //辅模块2场强
			assistRSSIValue_9 = (int) MainTabScanFragment.Nodes[9].RSSI_filtered;           //辅模块3场强

		assistRSSIValidity_1=0;
		assistRSSIValidity_2=0;
		assistRSSIValidity_3=0;
		assistRSSIValidity_4=0;
		assistRSSIValidity_5=0;
		assistRSSIValidity_6=0;
		assistRSSIValidity_7=0;
		assistRSSIValidity_8=0;
		assistRSSIValidity_9=0;

		zoneNum=0;

		gravityBuffer=new float[50][3] ;
		proximityBuffer=new float[50];
		filteredGravity=new float[3] ;
		filteredProximity=5;
		serialNumber=1;

		}



	@Override
	public void onCreate() {
		Log.d("Func", "onCreate()");
		super.onCreate();
		boolean bTemp=false;
		initSensorDataGroup();
		mHandler=new Handler();
		r = new float[9];

		sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);


		try {
			SimpleDateFormat format = new SimpleDateFormat("MM.dd HH_mm_ss");
			fileName =format.format(new Date()) + ".txt";
			path = new File(pathName);
			file = new File(pathName + fileName);
			if (!path.exists())
				bTemp=path.mkdir();

			Log.e("---",file.getAbsolutePath().toString());
			if(bTemp==true)
			{
				Log.d("Success", "New File");
			}

			if (!file.exists())
				file.createNewFile();
				fos = new FileOutputStream(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		//if (MainTabLocationFragment.isRecord) {
			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					// double[][] sample=featureExtraction.feature(linearaccelerationValues[0],linearaccelerationValues[1]);
					//   classNum= svmClassification.classDef(sample);
					//	Log.d("class1",classNum+"   ");
					for (int i = 0; i <= 9; i++) {
						if (MainTabScanFragment.Nodes[i]==null){MainTabScanFragment.Nodes[i]=new Node();
						MainTabScanFragment.Nodes[i].RSSI_filtered=0;}
					}
					if(MainTabScanFragment.Nodes!=null) {
						mainRSSIValue = (int) MainTabScanFragment.Nodes[0].RSSI_filtered;           //主蓝牙模块场强
						assistRSSIValue_1 = (int) MainTabScanFragment.Nodes[1].RSSI_filtered;           //辅模块1场强
						assistRSSIValue_2 = (int) MainTabScanFragment.Nodes[2].RSSI_filtered;           //辅模块2场强
						assistRSSIValue_3 = (int) MainTabScanFragment.Nodes[3].RSSI_filtered;           //辅模块3场强
						assistRSSIValue_4 = (int) MainTabScanFragment.Nodes[4].RSSI_filtered;           //辅模块1场强
						assistRSSIValue_5 = (int) MainTabScanFragment.Nodes[5].RSSI_filtered;           //辅模块2场强
						assistRSSIValue_6 = (int) MainTabScanFragment.Nodes[6].RSSI_filtered;           //辅模块3场强
						assistRSSIValue_7 = (int) MainTabScanFragment.Nodes[7].RSSI_filtered;           //辅模块1场强
						assistRSSIValue_8 = (int) MainTabScanFragment.Nodes[8].RSSI_filtered;           //辅模块2场强
						assistRSSIValue_9 = (int) MainTabScanFragment.Nodes[9].RSSI_filtered;           //辅模块3场强
					}
					if (MainTabLocationFragment.isRecord)
						writeSensorDataTofile();

					mHandler.postDelayed(this, GET_PERIOD);
				}
			}, GET_PERIOD);
		//}
		/*Notification.Builder builder = new Notification.Builder(this);
		builder.setContentTitle("SensorDataService");
		builder.setContentText("Recording " + fileName + "...");
		//builder.setSmallIcon(R.mipmap.ic_launcher);
		Notification notification = builder.build();
		startForeground(36, notification);*/

	}


	public void writeSensorDataTofile(){
		zoneNum=MainTabLocationFragment.getZone();
		motion=MainTabScanFragment.curMotion;
		//int assistRSSIValue_1_filtered=(int)MainTabConnectInfoFragment.readNode(1);
		//int assistRSSIValue_2_filtered=(int)MainTabConnectInfoFragment.readNode(2);
	//	int mainRSSIValue_filtered=(int)MainTabConnectInfoFragment.readNode(0);

		//Log.d("ZoneNumber",zoneNum+"  ");
		if (startTimestamp == 0) {
			startTimestamp = System.currentTimeMillis();}

		    long tTemp=System.currentTimeMillis()-startTimestamp;
			//serialNumber|Time|
		    String ss =Long.toString(serialNumber)+'\t'; //SerialNumber
			ss += Long.toString(tTemp) + "\t"; //Timestamp
		    //加速度传感器 x y z m/s^2
		    ss += Float.toString(accelerometerValues[0])+'\t'+Float.toString(accelerometerValues[1])+'\t'+Float.toString(accelerometerValues[2])+'\t';
             //陀螺仪 rad/s x y z
		    ss += Float.toString(gyroscopeValues[0])+'\t'+Float.toString(gyroscopeValues[1])+'\t'+Float.toString(gyroscopeValues[2])+'\t';
		    //磁场强度   x y z uH
	     	ss += Float.toString(magneticValues[0])+'\t'+Float.toString(magneticValues[1])+'\t'+Float.toString(magneticValues[2])+'\t';
            //重力传感器 x y z m/s^2
		     ss += Float.toString(gravityValues[0])+'\t'+Float.toString(gravityValues[1])+'\t'+Float.toString(gravityValues[2])+'\t';
			//障碍物距离 cm
			 ss += Float.toString(proximityValues)+'\t';
			//光强  lx
			ss += Float.toString(lightValues)+'\t';
		    //方向 x y z m/s^2
			ss += Float.toString(rotationValues[0])+'\t'+Float.toString(rotationValues[1])+'\t'+Float.toString(rotationValues[2])+'\t';
            //线性加速度传感器 x y z
		    ss += Float.toString(linearaccelerationValues[0])+'\t'+Float.toString(linearaccelerationValues[1])+'\t'+Float.toString(linearaccelerationValues[2])+'\t';
		    //主蓝牙模块场强
		    ss +=Integer.toString(mainRSSIValue)+'\t';
			//辅模块1场强
			ss +=Integer.toString(assistRSSIValue_1)+'\t';
			//辅模块2场强
			ss +=Integer.toString(assistRSSIValue_2)+'\t';
			//辅模块3场强
			ss +=Integer.toString(assistRSSIValue_3)+'\t';
			//辅模块4场强
			ss +=Integer.toString(assistRSSIValue_4)+'\t';
			//辅模块5场强
			ss +=Integer.toString(assistRSSIValue_5)+'\t';
			//辅模块6场强
			ss +=Integer.toString(assistRSSIValue_6)+'\t';
			//辅模块7场强
			ss +=Integer.toString(assistRSSIValue_7)+'\t';
			//辅模块8场强
			ss +=Integer.toString(assistRSSIValue_8)+'\t';
			//辅模块9场强
			ss +=Integer.toString(assistRSSIValue_9)+'\t';
			//辅模块2场强
			ss +=Integer.toString(assistRSSIValidity_1)+'\t'+Integer.toString(assistRSSIValidity_2)+'\t'+Integer.toString(assistRSSIValidity_3)+'\t'+Integer.toString(assistRSSIValidity_4)+'\t'+Integer.toString(assistRSSIValidity_5)+'\t'+Integer.toString(assistRSSIValidity_6)+'\t'+Integer.toString(assistRSSIValidity_7)+'\t'+Integer.toString(assistRSSIValidity_8)+"\t";
			//Zone
			ss +=Integer.toString(zoneNum)+'\t'+Integer.toString(motion)+"\n";
			byte [] buffer = ss.getBytes();
			try {
				fos.write(buffer);
			} catch (IOException e) {
				e.printStackTrace();
			}

			serialNumber++;

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d("Func", "onStartCommand()");
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		Log.d("Func", "onDestory()");
		sensorManager.unregisterListener(this);
		try {
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		super.onDestroy();
	}

	public void onSensorChanged(SensorEvent event) {

		String s = "";
		switch (event.sensor.getType()) {
		case Sensor.TYPE_ACCELEROMETER:
			accelerometerValues[0]=event.values[0]-(float)0.098;
			accelerometerValues[1]=event.values[1]+(float)0.03;
			accelerometerValues[2]=event.values[2]-(float)0.1;

			counter=counter+1;
			sum=(accelerometerValues[0]+sum)/counter;


			//Log.d("acc1","acc0="+ accelerometerValues[2]+"   average="+sum+" counter="+counter);


			s = "0";
			sensorManager.getRotationMatrix(R,null,accelerometerValues,magneticValues);
			sensorManager.getOrientation(R,orientationValue);
			orientationValue[0]=(float)Math.toDegrees(orientationValue[0])+180;
			MainTabScanFragment.acceleroValue=accelerometerValues;
			a_tmp1 = (float)(Math.round(accelerometerValues[0]*1000)/1000);

			a_tmp2 = (float)(Math.round(accelerometerValues[1]*1000)/1000);

			a_tmp3 = (float)(Math.round(accelerometerValues[2]*1000)/1000);


			//Log.d("ori",orientationValue[0]+"  ");
			break;
		case Sensor.TYPE_GYROSCOPE:
			gyroscopeValues[0]=event.values[0];
			gyroscopeValues[1]=event.values[1];
			gyroscopeValues[2]=event.values[2];
			MainTabScanFragment.gyroValue=gyroscopeValues;
			s = "1";
			break;
		case Sensor.TYPE_STEP_COUNTER:
			walk=event.values[0];
			//Log.d("stepornot",walk+"  ");
			break;

		case Sensor.TYPE_MAGNETIC_FIELD:
			magneticValues[0]=event.values[0];
			magneticValues[1]=event.values[1];
			magneticValues[2]=event.values[2];
			s = "2";
			//sensorManager.getRotationMatrix(R,null,accelerometerValues,magneticValues);
			//sensorManager.getOrientation(R,orientationValue);
			break;
		case Sensor.TYPE_GRAVITY:
			gravityValues[0]=event.values[0];
			gravityValues[1]=event.values[1];
			gravityValues[2]=event.values[2];
			//Log.d("gravity",gravityValues[0]+"  "+gravityValues[1]+" "+gravityValues[2]);
			MainTabScanFragment.gravityValue=gravityValues;
			for(int i=FILTERWND-1;i>0;i--)
			{
				gravityBuffer[i][0]=gravityBuffer[i-1][0];//x
				gravityBuffer[i][1]=gravityBuffer[i-1][1];//y
				gravityBuffer[i][2]=gravityBuffer[i-1][2];//z

			}
			gravityBuffer[0][0]=event.values[0];
			gravityBuffer[0][1]=event.values[1];
			gravityBuffer[0][2]=event.values[2];
			updateFilteredGravity();
			SensorManager.getRotationMatrix(r, null, gravityValues, magneticValues);
			//values从这里返回
			float[]values=new float[3];
			SensorManager.getOrientation(r, values);
			//提取数据
			float azimuth = (float)Math.toDegrees(values[0]);
			if (azimuth<0) {
				azimuth=azimuth+360;
			}
			float pitch = (float)Math.toDegrees(values[1]);
			float roll = (float)Math.toDegrees(values[2]);

			//Log.d("rollyawpitch","Azimuth：" + (int)azimuth + "\tPitch：" + (int)pitch + "\tRoll：" + (int)roll);

			yaw = (float) azimuth;
			pitch = (float) pitch;
			mRoll = (float) roll;
			float cosx,sinx,cosy,siny,cosz,sinz;
			cosx= (float)Math.cos(mRoll);
			sinx =(float) Math.sin(mRoll);
			cosy =(float) Math.cos(pitch);
			siny = (float)Math.sin(pitch);
			cosz = (float)Math.cos(yaw);
			sinz = (float)Math.sin(yaw);

			float mat0,mat1,mat2,mat3,mat4,mat5,mat6,mat7,mat8;
			mat0 = cosz * cosy;
			mat1 = -cosy * sinz;
			mat2 = siny;
			mat3 = sinz*cosx + (cosz*sinx * siny);
			mat4 = cosz*cosx - (sinz*sinx * siny);
			mat5 = -sinx * cosy;
			mat6 = (sinz*sinx) - (cosz*cosx * siny);
			mat7 = (cosz*sinx) + (sinz*cosx * siny);
			mat8 = cosy * cosx;

			float aX,aY,aZ;
			aX = a_tmp1 * mat0 + a_tmp2 * mat3 + a_tmp3 * mat6;
			aY = a_tmp1 * mat1 + a_tmp2 * mat4 + a_tmp3 * mat7;
			aZ = a_tmp1 * mat2 + a_tmp2 * mat5 + a_tmp3 * mat8;

			vx=(float)vx+aX*20/1000;
			vy=(float)vy+aY*20/1000;
			vz=(float)vz+aZ*20/1000;
			if (Math.abs(aX)<10){aX=0;};
			if (Math.abs(aZ)<10){aZ=0;};
			if (Math.abs(aY)<10){aY=0;};
			sx=sx+vx*20/1000;
			sy=sy+vy*20/1000;
			sz=sz+vz*20/1000;
			//Log.d("acc","X= "+(int)aX+"  Y="+(int)aY+"  Z="+(int)aZ);
			//Log.d("Velocity","X= "+(int)vx+"  Y="+(int)vy+"  Z="+(int)vz);

			//Log.d("distance","X= "+(int)sx+"  Y="+(int)sy+"  Z="+(int)sz);


			s = "3";
			break;
		case Sensor.TYPE_PROXIMITY:
			proximityValues=event.values[0];
			for(int i=FILTERWND-1;i>0;i--)
			{
				proximityBuffer[i]=proximityBuffer[i-1];

			}
			proximityBuffer[0]=event.values[0];
			updateFilteredProximity();
			s = "4";
			MainTabScanFragment.distanceValue=proximityValues;
			break;
		case Sensor.TYPE_LIGHT:
			lightValues=event.values[0];
			MainTabScanFragment.light=lightValues;
			s = "5";
			break;
		case Sensor.TYPE_ROTATION_VECTOR:
			rotationValues[0]=event.values[0];
			rotationValues[1]=event.values[1];
			rotationValues[2]=event.values[2];
			s = "6";
			break;
		case Sensor.TYPE_LINEAR_ACCELERATION:
			linearaccelerationValues[0]=event.values[0];
			linearaccelerationValues[1]=event.values[1];
			linearaccelerationValues[2]=event.values[2];
			MainTabScanFragment.linearAccValue=linearaccelerationValues;
			s = "7";
			break;


		default:
			break;
		}

	}


	public void updateFilteredGravity(){

		float tmpX=0;
		float tmpY=0;
		float tmpZ=0;
        for(int i=0;i<FILTERWND;i++)
		{
			tmpX+=gravityBuffer[i][0];
			tmpY+=gravityBuffer[i][1];
			tmpZ+=gravityBuffer[i][2];
		}
		filteredGravity[0]=tmpX/FILTERWND;
		filteredGravity[1]=tmpY/FILTERWND;
		filteredGravity[2]=tmpZ/FILTERWND;

	}

	public void updateFilteredProximity()
	{
		filteredProximity=proximityBuffer[0];

	}

	@Override
	public IBinder onBind(Intent intent) {
		Log.d("Func", "onBind()");
		return binder;
	}
	
	@Override
	public boolean onUnbind(Intent intent) {
		Log.d("Func", "onUnbind()");
		return super.onUnbind(intent);
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {


	}
	
	class MyBinder extends Binder {
		public void setSensors(boolean[] switches) {
			Log.d("Func", "setSensors()");
			for (int i=0; i<switches.length; ++i) 
				if (switches[i]) {
				switch(i) {
				case 0:
					sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
					break;
				case 1:
					sensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
					break;
				case 2:
					sensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
					break;
				case 3:
					sensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
					break;
				case 4:
					sensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
					break;
				case 5:
					sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
					break;
				case 6:
					sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
					break;
				case 7:
					sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
					break;
				default:
					break;
				}
				sensorManager.registerListener(service, sensor, SensorManager.SENSOR_DELAY_FASTEST);
			}
			Log.d("Func", "setSensors() Finish");
		}
	}

}
