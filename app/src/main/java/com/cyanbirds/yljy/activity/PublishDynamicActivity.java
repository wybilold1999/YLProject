package com.cyanbirds.yljy.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.cyanbirds.yljy.R;
import com.cyanbirds.yljy.activity.base.BaseActivity;
import com.cyanbirds.yljy.adapter.PublishImageAdapter;
import com.cyanbirds.yljy.config.AppConstants;
import com.cyanbirds.yljy.config.ValueKey;
import com.cyanbirds.yljy.entity.Picture;
import com.cyanbirds.yljy.eventtype.PubDycEvent;
import com.cyanbirds.yljy.manager.AppManager;
import com.cyanbirds.yljy.net.request.OSSImagUploadRequest;
import com.cyanbirds.yljy.net.request.PublishDynamicRequest;
import com.cyanbirds.yljy.utils.FileAccessorUtils;
import com.cyanbirds.yljy.utils.ImageUtil;
import com.cyanbirds.yljy.utils.ProgressDialogUtils;
import com.cyanbirds.yljy.utils.ToastUtil;
import com.google.gson.Gson;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 作者：wangyb
 * 时间：2016/9/13 22:49
 * 描述：
 */
public class PublishDynamicActivity extends BaseActivity {
	@BindView(R.id.dynamic_text_content)
	EditText mDynamicTextContent;
	@BindView(R.id.recyclerview)
	RecyclerView mRecyclerview;

	private PublishImageAdapter mAdapter;
	private List<String> photoList;
	private List<Picture> ossImgUrls;
	private int count = 0;//OSS上传的时候修改piclist中的url值

	public static final int CHOOSE_IMG_RESULT = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_publish_dynamic);
		Toolbar mToolbar = getActionBarToolbar();
		if (mToolbar != null) {
			mToolbar.setNavigationIcon(R.mipmap.ic_up);
		}
		ButterKnife.bind(this);
		setupView();
		setupData();
	}

	private void setupView() {
		mRecyclerview.setLayoutManager(new GridLayoutManager(this, 3));
	}

	private void setupData() {
		photoList = new ArrayList<>();
		mAdapter = new PublishImageAdapter(photoList){
			@Override
			public void openGallery() {
				Intent intent = new Intent(PublishDynamicActivity.this, PhotoChoserActivity.class);
				intent.putStringArrayListExtra(ValueKey.IMAGE_URL, (ArrayList<String>) photoList);
				startActivityForResult(intent, CHOOSE_IMG_RESULT);
			}
		};
		mRecyclerview.setAdapter(mAdapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.publish_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.publish) {
			if (TextUtils.isEmpty(mDynamicTextContent.getText().toString())) {
				ToastUtil.showMessage("还是说点什么吧");
				return false;
			}
			ProgressDialogUtils.getInstance(this).show(R.string.dialog_request_uploda);
			if (photoList != null && photoList.size() > 0) {
				ossImgUrls = new ArrayList<>();
				for (String path : photoList) {
					ossImgUrls.add(ImageUtil.getPicInfoForPath(path));
					String imgUrl = ImageUtil.compressImage(path, FileAccessorUtils.IMESSAGE_IMAGE);
					new OSSUploadImgTask().request(AppManager.getFederationToken().bucketName,
							AppManager.getOSSFacePath(), imgUrl);
				}
			} else {
				new PublishDynamicTask().request("", mDynamicTextContent.getText().toString());
			}
			return true;
		}
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("您还未发表动态，确定退出？");
		builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				finish();
			}
		});
		builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.show();
		return true;
	}

	class OSSUploadImgTask extends OSSImagUploadRequest {
		@Override
		public void onPostExecute(String s) {
			count++;
			if (count <= ossImgUrls.size()) {
				ossImgUrls.get(count - 1).path = AppConstants.OSS_IMG_ENDPOINT + s;
				if (count == photoList.size()) {
					Gson gson = new Gson();
					String picUrls = gson.toJson(ossImgUrls);
					new PublishDynamicTask().request(picUrls, mDynamicTextContent.getText().toString());
				}
			}
		}

		@Override
		public void onErrorExecute(String error) {
			ProgressDialogUtils.getInstance(PublishDynamicActivity.this).dismiss();
		}
	}

	class PublishDynamicTask extends PublishDynamicRequest {
		@Override
		public void onPostExecute(String s) {
			ProgressDialogUtils.getInstance(PublishDynamicActivity.this).dismiss();
			ToastUtil.showMessage(R.string.publish_success);
			EventBus.getDefault().post(new PubDycEvent(s));
			finish();
		}

		@Override
		public void onErrorExecute(String error) {
			ProgressDialogUtils.getInstance(PublishDynamicActivity.this).dismiss();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK && requestCode == CHOOSE_IMG_RESULT) {
			List<String> imgUrls = data.getStringArrayListExtra(ValueKey.IMAGE_URL);
			if (imgUrls != null && !imgUrls.isEmpty()) {
				photoList.clear();
				photoList.addAll(imgUrls);
				mAdapter.notifyDataSetChanged();
			}
		}
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
