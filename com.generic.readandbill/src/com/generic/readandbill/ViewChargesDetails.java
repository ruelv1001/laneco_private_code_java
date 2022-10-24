package com.generic.readandbill;

import com.generic.readandbill.database.Consumer;
import com.generic.readandbill.database.ConsumerDataSource;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

public class ViewChargesDetails extends Activity implements OnClickListener,OnFocusChangeListener {  
	protected Consumer consumer;
    protected ConsumerDataSource dsConsumer;
    protected Bundle extras;
    private int listPosition;
    private TextView mAccountNumber;
    protected Button mCancel;
    protected Button mConfirm;
    private TextView mName;
    
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_view);
        if (savedInstanceState != null) {
            this.extras = savedInstanceState;
        } else {
            this.extras = getIntent().getExtras();
        }
        this.dsConsumer = new ConsumerDataSource(this);
        this.listPosition = this.extras.getInt("pos");
        initControls();
    }
    protected void initControls() {
        this.consumer = this.dsConsumer.getConsumer(Long.valueOf(this.extras.getLong("id")));
        this.mAccountNumber = (TextView) findViewById(R.id.tvsvACCTNO);
        this.mConfirm.setEnabled(false);
        this.mConfirm.setOnClickListener(this);
        this.mCancel.setOnClickListener(this);
    }
	@Override
	public void onFocusChange(View arg0, boolean arg1) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		
	}
}
  