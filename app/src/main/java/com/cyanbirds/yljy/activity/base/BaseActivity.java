package com.cyanbirds.yljy.activity.base;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.cyanbirds.yljy.R;


/**
 * 
 * @ClassName:CSSuperActivity
 * @Description:通用父类Activity
 * @Author:wangyb
 * @Date:2015年5月4日下午5:17:01
 *
 */
public class BaseActivity extends AppCompatActivity {

	private Toolbar mActionBarToolbar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActivityStackManager.getScreenManager().pushActivity(this);

		ActionBar ab = getSupportActionBar();
		if (ab != null) {
			ab.setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public void setContentView(int layoutResID) {
		super.setContentView(layoutResID);
		getActionBarToolbar();
	}

	protected Toolbar getActionBarToolbar() {
		if (mActionBarToolbar == null) {
			mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
			if (mActionBarToolbar != null) {
				setSupportActionBar(mActionBarToolbar);
			}
		}
		return mActionBarToolbar;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * 显示软键盘
	 */
	public void showSoftKeyboard(View view) {
		if (view.requestFocus()) {
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
		}
	}

	/**
	 * 隐藏软键盘
	 * 
	 * @param
	 */
	public void hideSoftKeyboard() {
		if (getCurrentFocus() != null) {
			InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
			inputMethodManager.hideSoftInputFromWindow(getCurrentFocus()
					.getWindowToken(), 0);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		ActivityStackManager.getScreenManager().popActivity(this);
	}

	/**
	 * 退出所有activity栈
	 */
	public static void finishAll() {
		ActivityStackManager.getScreenManager().popAllActivityExceptOne(null);
	}

	/**
	 * 退出程序
	 */
	public static void exitApp() {
		finishAll();
//		System.exit(0);
	}
}
