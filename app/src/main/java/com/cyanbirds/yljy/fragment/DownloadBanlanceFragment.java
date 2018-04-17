package com.cyanbirds.yljy.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.cyanbirds.yljy.R;
import com.cyanbirds.yljy.db.MyGoldDaoManager;
import com.cyanbirds.yljy.entity.Gold;
import com.cyanbirds.yljy.manager.AppManager;
import com.cyanbirds.yljy.utils.ProgressDialogUtils;
import com.cyanbirds.yljy.utils.ToastUtil;
import com.umeng.analytics.MobclickAgent;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import mehdi.sakout.fancybuttons.FancyButton;

/**
 * @author: wangyb
 * @datetime: 2015-12-20 11:33 GMT+8
 * @email: 395044952@qq.com
 * @description:
 */
public class DownloadBanlanceFragment extends Fragment{

	@BindView(R.id.money)
	TextView mMoney;
	@BindView(R.id.btn_get)
	FancyButton mBtnGet;
	@BindView(R.id.edt_name)
	EditText mEdtName;
	@BindView(R.id.edt_bank)
	EditText mEdtBank;
	@BindView(R.id.edt_bank_no)
	EditText mEdtBankNo;

	private View rootView;
	private static Handler mHandler = new Handler();

	private double banlance;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		if (rootView == null) {
			rootView = inflater.inflate(R.layout.fragment_download_banlance, null);
			ButterKnife.bind(this, rootView);
			setupViews();
			setupEvent();
			setupData();
			setHasOptionsMenu(true);
		}
		ViewGroup parent = (ViewGroup) rootView.getParent();
		if (parent != null) {
			parent.removeView(rootView);
		}
		return rootView;
	}

	private void setupViews() {
	}

	private void setupEvent() {
	}

	private void setupData() {

	}

	@OnClick(R.id.btn_get)
	public void onClick() {
		/*if (AppManager.getClientUser().is_download_vip) {
			if (banlance < 100) {
				showDialog(R.string.get_balance_tips);
			} else if (AppManager.getClientUser().is_download_vip) {
				ProgressDialogUtils.getInstance(getActivity()).show(R.string.dialog_get_balance);
				mHandler.postDelayed(new Runnable() {
					@Override
					public void run() {
						ProgressDialogUtils.getInstance(getActivity()).dismiss();
						showDialog(R.string.get_balance_success);
						Gold gold = MyGoldDaoManager.getInstance(getActivity()).getMyGold();
						gold.banlance = 0;
						MyGoldDaoManager.getInstance(getActivity()).updateGold(gold);
					}
				}, 8000);
			}
		} else {
			ToastUtil.showMessage(R.string.turn_on_download_vip);
		}*/
		if (banlance < 100) {
			ToastUtil.showMessage(R.string.get_balance_tip);
//			showDialog(R.string.get_balance_tips);
		} else if (AppManager.getClientUser().is_download_vip) {
			if (checkInput()) {
				ProgressDialogUtils.getInstance(getActivity()).show(R.string.dialog_get_balance);
				mHandler.postDelayed(new Runnable() {
					@Override
					public void run() {
						ProgressDialogUtils.getInstance(getActivity()).dismiss();
						showDialog(R.string.get_balance_sus);
						Gold gold = MyGoldDaoManager.getInstance(getActivity()).getMyGold();
						gold.banlance = 0;
						MyGoldDaoManager.getInstance(getActivity()).updateGold(gold);
					}
				}, 6000);
			}
		} else {
			ToastUtil.showMessage(R.string.turn_on_download_vip);
		}

	}

	private void showDialog(int resourceId) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setMessage(resourceId);
		builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.show();
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
		}
		if (!bool)
			ToastUtil.showMessage(message);
		return bool;
	}

	@Override
	public void onResume() {
		super.onResume();
		banlance = MyGoldDaoManager.getInstance(getActivity()).getGoldCount();
		mMoney.setText(String.valueOf(banlance) + "元");

		MobclickAgent.onPageStart(this.getClass().getName());
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(this.getClass().getName());
	}

}
