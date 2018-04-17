package com.cyanbirds.yljy.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.cyanbirds.yljy.R;
import com.cyanbirds.yljy.activity.base.BaseActivity;
import com.cyanbirds.yljy.manager.AppManager;
import com.facebook.drawee.view.SimpleDraweeView;
import com.umeng.analytics.MobclickAgent;

/**
 * 
 * @ClassName:ImagePreviewActivity
 * @Description:图片预览
 * @author Administrator
 * @Date:2015年6月4日下午4:12:17
 */
public class ImagePreviewActivity extends BaseActivity {

	private SimpleDraweeView mPreviewImg;
	private Uri mUri;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_preview);
		Toolbar toolbar = getActionBarToolbar();
		if (toolbar != null) {
			toolbar.setNavigationIcon(R.mipmap.ic_up);
		}
		setupViews();
		setupData();
	}

	/**
	 * 设置视图
	 */
	private void setupViews() {
		mPreviewImg = (SimpleDraweeView) findViewById(R.id.preview_img);
	}

	/**
	 * 设置数据
	 */
	private void setupData() {
		mUri = getIntent().getData();
		mPreviewImg.setImageURI(mUri);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.send_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.send) {
			if (AppManager.getClientUser().isShowVip) {
				if (AppManager.getClientUser().is_vip) {
					if (AppManager.getClientUser().isShowGold && AppManager.getClientUser().gold_num  < 101) {
						showGoldDialog();
					} else {
						sendImg();
					}
				} else {
					showTurnOnVipDialog();
				}
			} else {
				sendImg();
			}
		}
		return super.onOptionsItemSelected(item);
	}

	private boolean sendImg() {
		Intent intent = new Intent();
		intent.setData(mUri);
		setResult(RESULT_OK,intent);
		finish();
		return true;
	}

	private void showTurnOnVipDialog(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.un_send_msg);
		builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				Intent intent = new Intent(ImagePreviewActivity.this, VipCenterActivity.class);
				startActivity(intent);
			}
		});
		builder.setNegativeButton(R.string.until_single, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.show();
	}

	private void showGoldDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.no_gold_un_send_msg);
		builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				Intent intent = new Intent(ImagePreviewActivity.this, MyGoldActivity.class);
				startActivity(intent);
			}
		});
		builder.setNegativeButton(R.string.until_single, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.show();
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(this.getClass().getName());
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(this.getClass().getName());
		MobclickAgent.onPause(this);
	}
}
