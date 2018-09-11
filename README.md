### Folk From https://github.com/chiclaim/AndroidUpdater 
```text
在作者之上经过改进，兼容7.x 8.x
```

### simple usage

```java
      final UpdateDownloadDialog.Builder alert = new UpdateDownloadDialog.Builder(activity)
                .setImageRecourse(R.drawable.ic_update)
                .setTextTitle("版本升级")
                //.setTextSubTitle("")
                .setBody(updateInfo)
                .setNegativeButtonText("取消")
                .setOnNegativeClicked((view, dialog) -> dialog.dismiss())
                .setPositiveButtonText("下载")
                .setOnPositiveClicked((view, dialog) -> {
                    //权限检测
                    new RxPermissions(activity).request(
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            .subscribe(granted -> {
                                if (granted) {
                                    //Always true pre-M  I can control the xxx
                                    UpdaterConfig config = new UpdaterConfig.Builder(context)
                                            .setTitle("版本更新日志")
                                            //.setDescription(context.getString(R.string.system_download_description))
                                            .setFileUrl(updateurl)
                                            .setCanMediaScanner(true)
                                            .build();
                                    Updater.get().showLog(true).download(config);
                                    dialog.dismiss();
                                } else {
                                    // Oups permission denied
                                }
                            });

                })
                .setTouchDismiss(false)
                .setAutoHide(false);
                 alert.setNegativeButtonText("取消")
                      .setOnNegativeClicked((view, dialog) -> dialog.dismiss());
                 alert.build();
                 alert.show();

```