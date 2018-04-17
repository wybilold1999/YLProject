package com.cyanbirds.yljy.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cyanbirds.yljy.R;
import com.cyanbirds.yljy.activity.base.BaseActivity;
import com.cyanbirds.yljy.config.AppConstants;
import com.cyanbirds.yljy.entity.IdentifyCard;
import com.cyanbirds.yljy.manager.AppManager;
import com.cyanbirds.yljy.net.request.GetIdentifyInfoRequest;
import com.cyanbirds.yljy.net.request.OSSImagUploadRequest;
import com.cyanbirds.yljy.net.request.SubmitIdentifyRequest;
import com.cyanbirds.yljy.utils.FileUtils;
import com.cyanbirds.yljy.utils.ProgressDialogUtils;
import com.cyanbirds.yljy.utils.ToastUtil;
import com.facebook.drawee.view.SimpleDraweeView;
import com.umeng.analytics.MobclickAgent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import mehdi.sakout.fancybuttons.FancyButton;

/**
 * 作者：wangyb
 * 时间：2016/11/24 15:39
 * 描述：身份认证
 */
public class IdentifyActivity extends BaseActivity {
	@BindView(R.id.real_name)
	EditText mRealName;
	@BindView(R.id.identify_id)
	EditText mIdentifyId;
	@BindView(R.id.add_img)
	SimpleDraweeView mAddImg;
	@BindView(R.id.btn_submit)
	FancyButton mBtnSubmit;
	@BindView(R.id.identify_state)
	TextView mIdentifyState;
	@BindView(R.id.identify_lay)
	LinearLayout mIdentifyLay;

	private String imgUrl;//身份证照片
	private String regExp = "^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}([0-9]|X)$";

	/**
	 * 相册返回
	 */
	public final static int ALBUMS_RESULT = 102;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_identify);
		ButterKnife.bind(this);
		Toolbar toolbar = getActionBarToolbar();
		if (toolbar != null) {
			toolbar.setNavigationIcon(R.mipmap.ic_up);
		}
		new GetIdentifyInfoTask().request();
	}

	/**
	 * 请求认证信息
	 */
	class GetIdentifyInfoTask extends GetIdentifyInfoRequest {
		@Override
		public void onPostExecute(IdentifyCard identifyCard) {
			if (identifyCard != null && !identifyCard.isIdentify) {//认证中
				mIdentifyLay.setVisibility(View.GONE);
				mIdentifyState.setVisibility(View.VISIBLE);
				mIdentifyState.setText(R.string.identifing);
			} else if (identifyCard != null && identifyCard.isIdentify){
				mIdentifyState.setText(R.string.identify_success);
				mIdentifyLay.setVisibility(View.GONE);
				mIdentifyState.setVisibility(View.VISIBLE);
			} else {
				mIdentifyLay.setVisibility(View.VISIBLE);
				mIdentifyState.setVisibility(View.GONE);
			}
		}

		@Override
		public void onErrorExecute(String error) {
			mIdentifyLay.setVisibility(View.VISIBLE);
			mIdentifyState.setVisibility(View.GONE);
		}
	}

	@OnClick({R.id.add_img, R.id.btn_submit})
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.add_img:
				openAlbums();
				break;
			case R.id.btn_submit:
				if (checkInput()) {
					if (!TextUtils.isEmpty(imgUrl)) {
						new SubmitIdentifyTask().request(mRealName.getText().toString(),
								mIdentifyId.getText().toString(), imgUrl);
					} else {
						ToastUtil.showMessage(R.string.upload_tips);
					}
				}
				break;
		}
	}

	class SubmitIdentifyTask extends SubmitIdentifyRequest {
		@Override
		public void onPostExecute(String s) {
			AlertDialog.Builder builder = new AlertDialog.Builder(IdentifyActivity.this);
			builder.setMessage(R.string.identify_tips);
			builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					finish();
				}
			});
			builder.show();
		}

		@Override
		public void onErrorExecute(String error) {
			ToastUtil.showMessage(error);
		}
	}

	class OSSUploadImgTask extends OSSImagUploadRequest {
		@Override
		public void onPostExecute(String s) {
			imgUrl = AppConstants.OSS_IMG_ENDPOINT + s;
			ProgressDialogUtils.getInstance(IdentifyActivity.this).dismiss();
			ToastUtil.showMessage(R.string.upload_success);
			mAddImg.setImageURI(Uri.parse(imgUrl));
		}

		@Override
		public void onErrorExecute(String error) {
			ToastUtil.showMessage(error);
			ProgressDialogUtils.getInstance(IdentifyActivity.this).dismiss();
		}
	}

	/**
	 * 打开相册
	 */
	private void openAlbums() {
		Intent openAlbumIntent = new Intent(Intent.ACTION_GET_CONTENT);
		openAlbumIntent.setType("image/*");
		startActivityForResult(openAlbumIntent, ALBUMS_RESULT);
	}

	/**
	 * 验证输入
	 */
	private boolean checkInput() {
		String message = "";
		boolean bool = true;
		if (TextUtils.isEmpty(mRealName.getText().toString())) {
			message = getResources().getString(R.string.input_real_name);
			bool = false;
		} else if (TextUtils.isEmpty(mIdentifyId.getText().toString())) {
			message = getResources().getString(R.string.input_id_card);
			bool = false;
		} else {
			Pattern p = Pattern.compile(regExp);
			Matcher matcher = p.matcher(mIdentifyId.getText().toString());
			if (!matcher.matches()) {
				message = getResources().getString(R.string.error_id_card);
				bool = false;
			}
		}
		if (!bool)
			ToastUtil.showMessage(message);
		return bool;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK && requestCode == ALBUMS_RESULT) {
			//上传图片
			Uri uri = data.getData();
			String imgUrl = FileUtils.getPath(this, uri);
			ProgressDialogUtils.getInstance(this).show(R.string.dialog_request_uploda);
			new OSSUploadImgTask().request(AppManager.getFederationToken().bucketName,
					AppManager.getOSSFacePath(), imgUrl);
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
