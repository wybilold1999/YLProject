package com.cyanbirds.yljy.net.request;


import com.cyanbirds.yljy.listener.NetFileDownloadListener;
import com.cyanbirds.yljy.net.base.ResultPostExecute;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloader;

import java.io.File;


/***
 * @author wangyb
 * @ClassName:DownloadFileRequest
 * @Description:下载文件请求
 * @Date:2015年6月9日下午9:00:21
 */
@SuppressWarnings("deprecation")
/*public class DownloadFileRequest extends ResultPostExecute<String> {

    *//**
     * 下载请求
     *
     * @param url          请求地址
     * @param savePath     保存地址
     * @param fileName     保存文件名
     *//*
    public void request(String url, final String savePath, String fileName) {
        File file = new File(savePath, fileName);
        CSRestClient.client.get(url, new FileAsyncHttpResponseHandler(file) {

            @Override
            public void onSuccess(int statusCode, Header[] headers, File file) {
                onPostExecute(file.getPath());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,
                                  Throwable throwable, File file) {
                onErrorExecute("下载失败");
            }
        });
    }
}*/

public class DownloadFileRequest extends ResultPostExecute<String> {

    /**
     * 下载请求
     *
     * @param url          请求地址
     * @param savePath     保存地址
     * @param fileName     保存文件名
     */
    public void request(String url, final String savePath, String fileName) {
        final File file = new File(savePath, fileName);
        FileDownloader.getImpl().create(url)
                .setPath(file.getAbsolutePath())
                .setListener(new NetFileDownloadListener(){
                    @Override
                    protected void completed(BaseDownloadTask task) {
                        onPostExecute(file.getPath());
                    }

                    @Override
                    protected void error(BaseDownloadTask task, Throwable e) {
                        onErrorExecute("下载失败");
                    }
                }).start();
    }
}
