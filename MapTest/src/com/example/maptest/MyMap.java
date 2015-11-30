package com.example.maptest;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import com.example.maptest.MarkObject.MarkClickListener;
import com.example.opengltest.R;

public class MyMap extends SurfaceView implements SurfaceHolder.Callback {

	private static final String TAG = MyMap.class.getSimpleName();

	private static final long DOUBLE_CLICK_TIME_SPACE = 300;

	/**
	 * 设置最大缩放
	 */
	private float mCurrentScaleMax;
	/**
	 * 当前缩放
	 */
	private float mCurrentScale;
	/**
	 * 设置最小缩放
	 */
	private float mCurrentScaleMin;

	/**
	 *屏幕的宽高
	 */
	private float windowWidth, windowHeight;

	/**
	 * 需要标记的bitmap
	 */
	private Bitmap mBitmap;
	/**
	 * 指针
	 */
	private Paint mPaint;
	/**
	 *   mStartPoint mapCenter表示地图中心在屏幕上的坐标
	 */
	private PointF mStartPoint, mapCenter;
	/**
	 * 记录上一次点击屏幕的时间，以判断双击事件
	 */
	private long lastClickTime;
	private Status mStatus = Status.NONE;

	private float oldRate = 1;
	private float oldDist = 1;
	private float offsetX, offsetY;

	/**
	 * 判断屏幕是否是被铺满true横铺满  false为纵蒲满
	 */
	private boolean isShu = true;

	private enum Status {
		NONE, ZOOM, DRAG//缩小 拖拽
	};

	/**
	 * 存放点的集合
	 */
	private List<MarkObject> markList = new ArrayList<MarkObject>();

	public MyMap(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		init();
	}
	
	
	

	public MyMap(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init();
	}

	@SuppressLint("ClickableViewAccessibility") public MyMap(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init();
	}

	
	/**
	 * 初始化
	 */
	private void init() {
		SurfaceHolder holder = getHolder();
		holder.addCallback(this);
		// 获取屏幕的宽和高
		windowWidth = getResources().getDisplayMetrics().widthPixels;
		windowHeight = getResources().getDisplayMetrics().heightPixels- getStatusBarHeight();
		mPaint = new Paint();

		mStartPoint = new PointF();
		mapCenter = new PointF();
	}

	/**
	 * 对外暴露的方法    
	 * 用来设置需要标记的图片的BitMap
	 * @param bitmap
	 */
	public void setBitmap(Bitmap bitmap) {
		this.mBitmap = bitmap;
		// 设置最小缩放为铺满屏幕，最大缩放为最小缩放的4倍
		mCurrentScaleMin = Math.min(windowHeight / mBitmap.getHeight(),windowWidth / mBitmap.getWidth());//Math.min返回最小的
		mCurrentScale = mCurrentScaleMin;
		mCurrentScaleMax = mCurrentScaleMin * 4;
		mapCenter.set(mBitmap.getWidth() * mCurrentScale / 2,
				mBitmap.getHeight() * mCurrentScale / 2);
		float bitmapRatio = mBitmap.getHeight() / mBitmap.getWidth();//图片横纵坐标比
		float winRatio = windowHeight / windowWidth;//屏幕横纵坐标比
		// 判断屏幕铺满的情况，isShu为true表示屏幕横向被铺满，为false表示屏幕纵向被铺满
		if (bitmapRatio <= winRatio) {
			isShu = true;
		} else {
			isShu = false;
		}
		draw();
	}

	/**
	 * 为当前地图添加标记
	 * 
	 * @param object
	 */
	public void addMark(MarkObject object) {
		markList.add(object);
	//	draw()
	}

	/**
	 * 地图放大
	 */
	public void zoomIn() {
		mCurrentScale *= 1.5f;
		if (mCurrentScale > mCurrentScaleMax) {
			mCurrentScale = mCurrentScaleMax;
		}
		draw();
	}

	/**
	 * 地图缩小
	 */
	public void zoomOut() {
		mCurrentScale /= 1.5f;
		if (mCurrentScale < mCurrentScaleMin) {
			mCurrentScale = mCurrentScaleMin;
		}
		if (isShu) {
			if (mapCenter.x - mBitmap.getWidth() * mCurrentScale / 2 > 0) {
				mapCenter.x = mBitmap.getWidth() * mCurrentScale / 2;
			} else if (mapCenter.x + mBitmap.getWidth() * mCurrentScale / 2 < windowWidth) {
				mapCenter.x = windowWidth - mBitmap.getWidth() * mCurrentScale
						/ 2;
			}
			if (mapCenter.y - mBitmap.getHeight() * mCurrentScale / 2 > 0) {
				mapCenter.y = mBitmap.getHeight() * mCurrentScale / 2;
			}
		} else {

			if (mapCenter.y - mBitmap.getHeight() * mCurrentScale / 2 > 0) {
				mapCenter.y = mBitmap.getHeight() * mCurrentScale / 2;
			} else if (mapCenter.y + mBitmap.getHeight() * mCurrentScale / 2 < windowHeight) {
				mapCenter.y = windowHeight - mBitmap.getHeight()
						* mCurrentScale / 2;
			}

			if (mapCenter.x - mBitmap.getWidth() * mCurrentScale / 2 > 0) {
				mapCenter.x = mBitmap.getWidth() * mCurrentScale / 2;
			}
		}
		draw();
	}

	// 处理拖拽事件
	private void drag(MotionEvent event) {
		PointF currentPoint = new PointF();
		currentPoint.set(event.getX(), event.getY());
		offsetX = currentPoint.x - mStartPoint.x;
		offsetY = currentPoint.y - mStartPoint.y;
		// 以下是进行判断，防止出现图片拖拽离开屏幕
		if (offsetX > 0
				&& mapCenter.x + offsetX - mBitmap.getWidth() * mCurrentScale
						/ 2 > 0) {
			offsetX = 0;
		}
		if (offsetX < 0
				&& mapCenter.x + offsetX + mBitmap.getWidth() * mCurrentScale
						/ 2 < windowWidth) {
			offsetX = 0;
		}
		if (offsetY > 0
				&& mapCenter.y + offsetY - mBitmap.getHeight() * mCurrentScale
						/ 2 > 0) {
			offsetY = 0;
		}
		if (offsetY < 0
				&& mapCenter.y + offsetY + mBitmap.getHeight() * mCurrentScale
						/ 2 < windowHeight) {
			offsetY = 0;
		}
		mapCenter.x += offsetX;
		mapCenter.y += offsetY;
		draw();
		mStartPoint = currentPoint;
	}

	// 处理多点触控缩放事件
	private void zoomAction(MotionEvent event) {
		float newDist = spacing(event);
		if (newDist > 10.0f) {
			mCurrentScale = oldRate * (newDist / oldDist);
			if (mCurrentScale < mCurrentScaleMin) {
				mCurrentScale = mCurrentScaleMin;
			} else if (mCurrentScale > mCurrentScaleMax) {
				mCurrentScale = mCurrentScaleMax;
			}

			if (isShu) {
				if (mapCenter.x - mBitmap.getWidth() * mCurrentScale / 2 > 0) {
					mapCenter.x = mBitmap.getWidth() * mCurrentScale / 2;
				} else if (mapCenter.x + mBitmap.getWidth() * mCurrentScale / 2 < windowWidth) {
					mapCenter.x = windowWidth - mBitmap.getWidth()
							* mCurrentScale / 2;
				}
				if (mapCenter.y - mBitmap.getHeight() * mCurrentScale / 2 > 0) {
					mapCenter.y = mBitmap.getHeight() * mCurrentScale / 2;
				}
			} else {

				if (mapCenter.y - mBitmap.getHeight() * mCurrentScale / 2 > 0) {
					mapCenter.y = mBitmap.getHeight() * mCurrentScale / 2;
				} else if (mapCenter.y + mBitmap.getHeight() * mCurrentScale
						/ 2 < windowHeight) {
					mapCenter.y = windowHeight - mBitmap.getHeight()
							* mCurrentScale / 2;
				}

				if (mapCenter.x - mBitmap.getWidth() * mCurrentScale / 2 > 0) {
					mapCenter.x = mBitmap.getWidth() * mCurrentScale / 2;
				}
			}
		}
		draw();
		
	}

	// 处理点o击标记的事件
	private void clickAction(MotionEvent event) {

		int clickX = (int) event.getX();
		int clickY = (int) event.getY();

		for (MarkObject object : markList) {
			Bitmap location = object.getmBitmap();
			int objX = (int) (mapCenter.x - location.getWidth() / 2
					- mBitmap.getWidth() * mCurrentScale / 2 + mBitmap
					.getWidth() * object.getMapX() * mCurrentScale);
			int objY = (int) (mapCenter.y - location.getHeight()
					- mBitmap.getHeight() * mCurrentScale / 2 + mBitmap
					.getHeight() * object.getMapY() * mCurrentScale);
			// 判断当前object是否包含触摸点，在这里为了得到更好的点击效果，我将标记的区域放大了
			if (objX - location.getWidth() < clickX
					&& objX + location.getWidth() > clickX
					&& objY + location.getHeight() > clickY
					&& objY - location.getHeight() < clickY) {
				if (object.getMarkListener() != null) {
					object.getMapX();
					object.getMapY();
					object.getMarkListener().onMarkClick(clickX, clickY);
				}
				break;
			}

		}

	}

	// 计算两个触摸点的距离
	private float spacing(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return (float) Math.sqrt(x * x + y * y);
	}
	
	/**
	 *1、Translate————平移变换
	 *2、Scale————缩放变换
	 *3、Rotate————旋转变换
	 *4、Skew————错切变换
	 */

	/**
	 * 界面绘制界面的方法
	 */
	private void draw() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Canvas canvas = getHolder().lockCanvas();
				if (canvas != null && mBitmap != null) {
					canvas.drawColor(Color.GRAY);
					Matrix matrix = new Matrix();
					//Scale————缩放变换
					matrix.setScale(mCurrentScale, mCurrentScale,
							mBitmap.getWidth() / 2, mBitmap.getHeight() / 2);
					matrix.postTranslate(mapCenter.x - mBitmap.getWidth() / 2,
							mapCenter.y - mBitmap.getHeight() / 2);
					canvas.drawBitmap(mBitmap, matrix, mPaint);
					for (MarkObject object : markList) {
						Bitmap location = object.getmBitmap();
						matrix.setScale(1.0f, 1.0f);
						// 使用Matrix使得Bitmap的宽和高发生变化，在这里使用的mapX和mapY都是相对值
						matrix.postTranslate(
			mapCenter.x - location.getWidth() / 2- mBitmap.getWidth() * mCurrentScale/ 2 + mBitmap.getWidth()* object.getMapX() * mCurrentScale,
			mapCenter.y - location.getHeight()- mBitmap.getHeight() * mCurrentScale/ 2 + mBitmap.getHeight()* object.getMapY() * mCurrentScale);
		    canvas.drawBitmap(location, matrix, mPaint);
					}

				}
				if (canvas != null) {
					getHolder().unlockCanvasAndPost(canvas);
				}
			}
		}).start();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			if (event.getPointerCount() == 1) {
				// 如果两次点击时间间隔小于一定值，则默认为双击事件
				if (event.getEventTime() - lastClickTime < DOUBLE_CLICK_TIME_SPACE) {
					zoomIn();
				} else {
					
					//Toast.makeText(getContext(), "显示"+event.getX()+"  "+event.getY(), 1).show();
					mStartPoint.set(event.getX(), event.getY());
					mStatus = Status.DRAG;
				}
			}

			lastClickTime = event.getEventTime();
			break;

		case MotionEvent.ACTION_POINTER_DOWN:
			float distance = spacing(event);
			if (distance > 10f) {
				mStatus = Status.ZOOM;
				oldDist = distance;
			}
			break;

		case MotionEvent.ACTION_MOVE:

			if (mStatus == Status.DRAG) {
				drag(event);
			} else if (mStatus == Status.ZOOM) {
				zoomAction(event);
			}
			break;
		case MotionEvent.ACTION_UP:
        case MotionEvent.ACTION_CANCEL:
        	  //获得最后的坐标
            float x2 = event.getX();
            float y2 = event.getY();
            Log.d("yxw", "down"+x2+y2);
            float rewx= x2/this.mBitmap.getWidth()/mCurrentScale;
            float rewy=y2/this.mBitmap.getHeight()/mCurrentScale;
          //  Log.d(" x y "+x2+" "+y2+" 百分比"+rewx+" "+rewy+"  height+width"+height+width+  " 图片的 "+height2+" "+width2);
        for (int i = 0; i < createList.size(); i++) {
     	 MarkObject markObject = createList.get(i);
     	 markObject.setMapX(rewx);
     	 markObject.setMapY(rewy);
     	
     	 this.addMark(markObject);
     	 createList.remove(markObject);
        }
        
        
			if (mStatus != Status.ZOOM) {
				clickAction(event);
			}

		case MotionEvent.ACTION_POINTER_UP:
			oldRate = mCurrentScale;
			mStatus = Status.NONE;
			break;
		default:
			break;
		}

		return true;
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
	draw();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		if (mBitmap != null) {
			mBitmap.recycle();
		}
		for (MarkObject object : markList) {
			if (object.getmBitmap() != null) {
				object.getmBitmap().recycle();
			}
		}
	}

	
	 boolean hasMeasured = false;


	 
	
	List<MarkObject> createList=new ArrayList<MarkObject>();
	public void addNewDian(){

		MarkObject markObject = new MarkObject();
		markObject.setmBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.icon_marka));
		 markObject.setMarkListener(new MarkClickListener() {

				@Override
				public void onMarkClick(int x, int y) {
					Toast.makeText(getContext(), "点击覆盖物"+x+"  "+y, Toast.LENGTH_SHORT)
					.show();
				}
			});
		
		createList.add(markObject);
		this.zoomOut();
	}
	/**
	 *  获得状态栏高度
	 * @return
	 */
	private int getStatusBarHeight() {
		Class<?> c = null;
		Object obj = null;
		Field field = null;
		int x = 0;
		try {
			c = Class.forName("com.android.internal.R$dimen");
			obj = c.newInstance();
			field = c.getField("status_bar_height");
			x = Integer.parseInt(field.get(obj).toString());
			return getResources().getDimensionPixelSize(x);
		} catch (Exception e1) {
			e1.printStackTrace();
			return 75;
		}
	}

}
