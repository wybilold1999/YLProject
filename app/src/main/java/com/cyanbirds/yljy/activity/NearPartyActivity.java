package com.cyanbirds.yljy.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.cyanbirds.yljy.R;
import com.cyanbirds.yljy.activity.base.BaseActivity;
import com.cyanbirds.yljy.adapter.NearPartyAdapter;
import com.cyanbirds.yljy.entity.LoveParty;
import com.cyanbirds.yljy.net.request.GetLovePartListRequest;
import com.cyanbirds.yljy.ui.widget.NoScrollGridView;
import com.cyanbirds.yljy.utils.StringUtil;
import com.cyanbirds.yljy.utils.ToastUtil;
import com.umeng.analytics.MobclickAgent;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 作者：wangyb
 * 时间：2016/11/2 15:56
 * 描述：相亲活动
 */
public class NearPartyActivity extends BaseActivity {

	@BindView(R.id.title_1)
	TextView mTitle1;
	@BindView(R.id.party_1)
	NoScrollGridView mParty1;
	@BindView(R.id.title_2)
	TextView mTitle2;
	@BindView(R.id.party_2)
	NoScrollGridView mParty2;
	@BindView(R.id.title_3)
	TextView mTitle3;
	@BindView(R.id.party_3)
	NoScrollGridView mParty3;
	@BindView(R.id.title_4)
	TextView mTitle4;
	@BindView(R.id.party_4)
	NoScrollGridView mParty4;

	private NearPartyAdapter mAdapter1;
	private NearPartyAdapter mAdapter2;
	private NearPartyAdapter mAdapter3;
	private NearPartyAdapter mAdapter4;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_near_party);
		ButterKnife.bind(this);
		Toolbar toolbar = getActionBarToolbar();
		if (toolbar != null) {
			toolbar.setNavigationIcon(R.mipmap.ic_up);
		}
		setupData();
	}

	private void setupData() {
		new GetLovePartyListTask().request();
	}

	class GetLovePartyListTask extends GetLovePartListRequest {
		@Override
		public void onPostExecute(List<LoveParty> loveParties) {
			if (loveParties != null && loveParties.size() > 0) {
				mTitle1.setText(loveParties.get(0).title);
				mTitle2.setText(loveParties.get(1).title);
				mTitle3.setText(loveParties.get(2).title);
				mTitle4.setText(loveParties.get(3).title);

				mAdapter1 = new NearPartyAdapter(NearPartyActivity.this,
						loveParties.get(0),
						StringUtil.stringToIntList(loveParties.get(0).ImgUrl), mParty1);
				mAdapter2 = new NearPartyAdapter(NearPartyActivity.this,
						loveParties.get(1),
						StringUtil.stringToIntList(loveParties.get(1).ImgUrl), mParty2);
				mAdapter3 = new NearPartyAdapter(NearPartyActivity.this,
						loveParties.get(2),
						StringUtil.stringToIntList(loveParties.get(2).ImgUrl), mParty3);
				mAdapter4 = new NearPartyAdapter(NearPartyActivity.this,
						loveParties.get(3),
						StringUtil.stringToIntList(loveParties.get(3).ImgUrl), mParty4);
				mParty1.setAdapter(mAdapter1);
				mParty2.setAdapter(mAdapter2);
				mParty3.setAdapter(mAdapter3);
				mParty4.setAdapter(mAdapter4);
			}
		}

		@Override
		public void onErrorExecute(String error) {
			ToastUtil.showMessage(error);
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
