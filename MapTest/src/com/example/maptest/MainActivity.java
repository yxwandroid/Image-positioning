package com.example.maptest;

import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
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
		sceneMap.setBitmap(b);

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
			break;
		case R.id.button_delete:
			
			break;
		default:
			break;
		}
		
	}
	
}
