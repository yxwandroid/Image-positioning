package com.example.maptest;

import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.example.maptest.MarkObject.MarkClickListener;
import com.example.opengltest.R;

public class MainActivity extends Activity implements OnClickListener {

	private MyMap sceneMap;
	private Button btnin;
	private Button btnout;
	private Button btnadd;
    private Button btndelete;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		getListData();
		btnin=(Button) findViewById(R.id.button_in);
		btnout=(Button) findViewById(R.id.button_out);
		btnadd=(Button) findViewById(R.id.button_add);
		btndelete=(Button) findViewById(R.id.button_delete);
		btnin.setOnClickListener(this);
		btnout.setOnClickListener(this);
		btnadd.setOnClickListener(this);
		btndelete.setOnClickListener(this);	
		sceneMap = (MyMap) findViewById(R.id.sceneMap);
		Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.test);
		height2 = b.getHeight();
		width2 = b.getWidth();
		sceneMap.setBitmap(b);
		 ViewTreeObserver vto = sceneMap.getViewTreeObserver();
		 hasMeasured = false;
	     vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
	            public boolean onPreDraw(){
					if (hasMeasured == false){

	                     height = sceneMap.getMeasuredHeight();
	                     width = sceneMap.getMeasuredWidth();
	     //获取到宽度和高度后，可用于计算                    
	                    hasMeasured = true;
	                }
	                return true;
	            }
	        });
//		sceneMap.setOnTouchListener(new OnTouchListener() {
//			
//			@Override
//			public boolean onTouch(View v, MotionEvent event) {
//				 float startx;
//				float starty;
//				switch (event.getAction() & MotionEvent.ACTION_MASK) {
//
//	              
//	                case MotionEvent.ACTION_UP:
//	                case MotionEvent.ACTION_CANCEL:
//	                    startx = event.getRawX();
//	                    starty = event.getRawY();
//	                    
//	                    //获得最后的坐标
//	                    float x2 = event.getX();
//	                    float y2 = event.getY();
//	                    //获得坐标在图片相对位置的的百分比
//	                   float rewx= x2/width;
//	                   float rewy=y2/height;
//	                   Log.d("yxw", startx+" "+starty+" x y "+x2+" "+y2+" 百分比"+rewx+" "+rewy+"  height+width"+height+width+  " 图片的 "+height2+" "+width2);
//	             for (int i = 0; i < createList.size(); i++) {
//	            	 MarkObject markObject = createList.get(i);
//	            	 markObject.setMapX(rewx);
//	            	 markObject.setMapY(rewy);
//	            	
//	            	 sceneMap.addMark(markObject);
//	            	 createList.remove(markObject);
//				}
//	                   
//	                   
//	                   break;
//	            }
//	          
//	            return false;
//
//			}
//		});
	

	for (int i = 0; i < arrayList.size(); i++) {
		MarkObject markObject = arrayList.get(i);
		markObject.setmBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.icon_marka));
		markObject.setMarkListener(new MarkClickListener() {

			@Override
			public void onMarkClick(int x, int y) {
				Toast.makeText(MainActivity.this, "点击覆盖物"+x+"  "+y, Toast.LENGTH_SHORT)
						.show();
			}
		});
		sceneMap.addMark(markObject);
	}
		
	}

	
	
	
	ArrayList<MarkObject> arrayList=new ArrayList<MarkObject>();
	private int height;
	private int width;
	private boolean hasMeasured;
	public ArrayList<MarkObject> getListData(){
		
		MarkObject markObject= new MarkObject();
		markObject.setMapX(0.35f);
		markObject.setMapY(0.5f);
		
		MarkObject markObject2 = new MarkObject();
		markObject2.setMapX(0.4f);
		markObject2.setMapY(0.6f);
		MarkObject markObject3 = new MarkObject();
		markObject3.setMapX(0.2f);
		markObject3.setMapY(0.3f);
		MarkObject markObject4 = new MarkObject();
		markObject4.setMapX(0.5f);
		markObject4.setMapY(0.5f);
		MarkObject markObject5 = new MarkObject();
		markObject5.setMapX(0.7f);
		markObject5.setMapY(0.7f);
		arrayList.add(markObject);
		arrayList.add(markObject2);
		arrayList.add(markObject3);
		arrayList.add(markObject4);
		arrayList.add(markObject5);
		return null;
	}
	
	ArrayList<MarkObject> createList=new ArrayList<MarkObject>();
	private int height2;
	private int width2;
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button_in:
			sceneMap.zoomIn();
			break;
		case R.id.button_out:
			sceneMap.zoomOut();
			break;
		case R.id.button_add:
		
		 sceneMap.addNewDian();
//			MarkObject markObject = new MarkObject();
//			markObject.setmBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.icon_marka));
//			 markObject.setMarkListener(new MarkClickListener() {
//
// 				@Override
// 				public void onMarkClick(int x, int y) {
// 					Toast.makeText(MainActivity.this, "点击覆盖物"+x+"  "+y, Toast.LENGTH_SHORT)
//						.show();
// 				}
// 			});
//			createList.add(markObject);
//			sceneMap.zoomOut();
			//sceneMap.addMark(markObject);
			break;
		case R.id.button_delete:
			
			break;
		default:
			break;
		}
		
	}
	
}
