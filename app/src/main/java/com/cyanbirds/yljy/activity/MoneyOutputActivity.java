package com.cyanbirds.yljy.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.cyanbirds.yljy.R;
import com.cyanbirds.yljy.activity.base.BaseActivity;
import com.cyanbirds.yljy.config.ValueKey;
import com.cyanbirds.yljy.entity.OutputMoney;
import com.cyanbirds.yljy.eventtype.SnackBarEvent;
import com.cyanbirds.yljy.manager.AppManager;
import com.cyanbirds.yljy.net.request.OutputMoneyRequest;
import com.cyanbirds.yljy.utils.PreferencesUtils;
import com.cyanbirds.yljy.utils.ProgressDialogUtils;
import com.cyanbirds.yljy.utils.ToastUtil;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import mehdi.sakout.fancybuttons.FancyButton;

/**
 * 作者：wangyb
 * 时间：2017/9/9 11:39
 * 描述：
 */
public class MoneyOutputActivity extends BaseActivity {
	@BindView(R.id.edt_name)
	EditText mEdtName;
	@BindView(R.id.edt_bank)
	EditText mEdtBank;
	@BindView(R.id.edt_bank_no)
	EditText mEdtBankNo;
	@BindView(R.id.edt_money)
	EditText mEdtMoney;
	@BindView(R.id.btn_get)
	FancyButton mBtnGet;

	private float mMoneyCount;
	private View mInputView;
	private TextView mMoney;
	private EditText mPwd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_money_output);
		ButterKnife.bind(this);
		Toolbar toolbar = getActionBarToolbar();
		if (toolbar != null) {
			toolbar.setNavigationIcon(R.mipmap.ic_up);
		}
		setupData();
	}

	private void setupData() {
		mMoneyCount = getIntent().getFloatExtra(ValueKey.DATA, 0);
	}

	@OnClick(R.id.btn_get)
	public void onViewClicked() {
		if (mMoneyCount <= 0 ) {
			ToastUtil.showMessage(R.string.money_count_zero);
		} else if (!AppManager.getClientUser().is_vip){
			showVipDialog();
		} else if (AppManager.getClientUser().gold_num < 101) {
			showGoldDialog();
		} else if (checkInput()) {
			initDialogView();
			showInputDialog();
		}
	}

	/**
	 * 验证输入
	 */
	private boolean checkInput() {
		String message = "";
		boolean bool = true;
		if (TextUtils.isEmpty(mEdtName.getText().toString())) {
			message = getResources().getString(R.string.input_name);
			bool = false;
		} else if (TextUtils.isEmpty(mEdtBank.getText().toString())) {
			message = getResources().getString(R.string.input_bank);
			bool = false;
		} else if (TextUtils.isEmpty(mEdtBankNo.getText().toString())) {
			message = getResources().getString(R.string.input_bank_no);
			bool = false;
		} else if (TextUtils.isEmpty(mEdtMoney.getText().toString())) {
			message = getResources().getString(R.string.input_output_money);
			bool = false;
		} else if (Double.parseDouble(mEdtMoney.getText().toString()) > mMoneyCount) {
			message = getResources().getString(R.string.output_money_beyond);
			bool = false;
		}
		if (!bool)
			ToastUtil.showMessage(message);
		return bool;
	}

	private void showInputDialog(){
		mMoney.setText("￥" + mEdtMoney.getText().toString());
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setCancelable(false);
		builder.setView(mInputView);
		builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				if (mPwd.getText().toString().length() == 6) {
					OutputMoney outputMoney = new OutputMoney();
					outputMoney.nickname = mEdtName.getText().toString();
					outputMoney.bank = mEdtBank.getText().toString();
					outputMoney.bankNo = mEdtBankNo.getText().toString();
					outputMoney.money = mEdtMoney.getText().toString();
					outputMoney.pwd = mPwd.getText().toString();
					new OutputMoneyTask().request(outputMoney);
					ProgressDialogUtils.getInstance(MoneyOutputActivity.this).show(R.string.wait);
				} else {
					ToastUtil.showMessage("密码长度不够");
				}
			}
		});
		builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.show();
	}

	private void initDialogView(){
		mInputView = LayoutInflater.from(this).inflate(R.layout.dialog_input_pwd, null);
		mMoney = (TextView) mInputView.findViewById(R.id.input_money_count);
		mPwd =  (EditText) mInputView.findViewById(R.id.input_pwd);
	}

	class OutputMoneyTask extends OutputMoneyRequest {
		@Override
		public void onPostExecute(String s) {
			ToastUtil.showMessage(R.string.output_success);
			ProgressDialogUtils.getInstance(MoneyOutputActivity.this).dismiss();
			PreferencesUtils.setMyMoney(MoneyOutputActivity.this, mMoneyCount - Float.parseFloat(mEdtMoney.getText().toString()));
			EventBus.getDefault().post(new SnackBarEvent());
			finish();
		}

		@Override
		public void onErrorExecute(String error) {
			ProgressDialogUtils.getInstance(MoneyOutputActivity.this).dismiss();
			ToastUtil.showMessage(R.string.output_faile);
		}
	}

	private void showVipDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.output_money_vip);
		builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				Intent intent = new Intent(MoneyOutputActivity.this, VipCenterActivity.class);
				startActivity(intent);
			}
		});
		builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.show();
	}

	private void showGoldDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.output_money_gold);
		builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				Intent intent = new Intent(MoneyOutputActivity.this, MyGoldActivity.class);
				startActivity(intent);
			}
		});
		builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.show();
	}
}
