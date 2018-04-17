package com.cyanbirds.yljy.listener;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;

/**
 * 作者：wangyb
 * 时间：2016/10/12 17:34
 * 描述：
 */
public class NetFileDownloadListener extends FileDownloadListener {
	@Override
	protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {

	}

	@Override
	protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {

	}

	@Override
	protected void completed(BaseDownloadTask task) {
	}

	@Override
	protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {

	}

	@Override
	protected void error(BaseDownloadTask task, Throwable e) {

	}

	@Override
	protected void warn(BaseDownloadTask task) {

	}
}
