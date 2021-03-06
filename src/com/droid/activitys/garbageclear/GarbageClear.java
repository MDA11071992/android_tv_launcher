package com.droid.activitys.garbageclear;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.*;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.*;
import com.droid.R;
import com.droid.activitys.garbageclear.util.ClearUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@SuppressLint("InflateParams")
public class GarbageClear extends Activity {
    private Button StartFound, StartClear;
    protected final int FOUND_FINISH = 0;
    protected final int CLEAR_FINISH = 1;
    private List<File> list;
    private ProgressBar progressdisplay;
    private String[] ClearType = {".apk", ".log"};
    private boolean IsInstall;
    private RelativeLayout found_layout;
    private FrameLayout clear_layout;
    private ImageView RoundImg;
    private Animation animation;
    private TextView file_path;
    private ImageView dialog_img;
    private long File_Grbagesize = 0;
    private TextView grbage_size;
    private boolean Found = false;
    private int TaskNum = 0;
    private int progressbar_num = 0;
    private List<FoundTask> tasklist;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case FOUND_FINISH:
                    //Finished
                    Found = true;
                    StartFound.setText("Очистить");
                    file_path.setText("Сканирование завершено");
                    StartFound.setClickable(true);
                    progressdisplay.setProgress(100);
                    progressbar_num = 0;
                    dialog_img.setImageResource(R.drawable.dialog_center_img);
                    break;
                case CLEAR_FINISH:
                    //Clean up is complete
                    Found = false;
                    StartClear.setClickable(true);
                    StartFound.setText("Запуск сканирования");
                    StartClear.setText("Очищено");
                    grbage_size.setText("0");
                    StartFound.setClickable(true);
                    animation = null;
                    File_Grbagesize = 0;
                    progressdisplay.setProgress(progressbar_num);
                    RoundImg.setAnimation(animation);
                    RoundImg.setVisibility(View.GONE);
                    dialog_img.setImageResource(R.drawable.finish_clear);
                    break;
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.garbageactivity_main);
        Init();
        Linstener();
    }

    public void Init() {
        list = new ArrayList<File>();
        tasklist = new ArrayList<GarbageClear.FoundTask>();
        animation = AnimationUtils.loadAnimation(GarbageClear.this, R.anim.dialog_anmiation);
        progressdisplay = (ProgressBar) findViewById(R.id.progressBar1);
        StartFound = (Button) findViewById(R.id.start_found);
        file_path = (TextView) findViewById(R.id.file_path);
        StartClear = (Button) findViewById(R.id.start_clear);
        RoundImg = (ImageView) findViewById(R.id.round_img);
        grbage_size = (TextView) findViewById(R.id.garbage_size);
        dialog_img = (ImageView) findViewById(R.id.dialog_img);
        found_layout = (RelativeLayout) findViewById(R.id.found_layout);
        clear_layout = (FrameLayout) findViewById(R.id.clear_layout);
//		UseMemory=StorageUtil.getUseMemorySize();
    }

    @SuppressLint("SdCardPath")
    public void Linstener() {
        StartClear.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                clear_layout.setVisibility(View.GONE);
                found_layout.setVisibility(View.VISIBLE);
            }
        });
        StartFound.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                StartFound.setClickable(false);
                if (!Found) {
                    StartFound.setText("Сканирование...");
                    StartClear.setClickable(false);
                    new FoundTask(Environment.getExternalStorageDirectory()
                            + "/", ClearType).execute();
                } else {
                    if (File_Grbagesize != 0) {
                        StartClear.setClickable(false);
                        clear_layout.setVisibility(View.VISIBLE);
                        found_layout.setVisibility(View.GONE);
                        RoundImg.setAnimation(animation);
                        new Thread(new Runnable() {

                            @Override
                            public void run() {
                                // TODO Auto-generated method stub
                                DeleteFile();
                                Message message = new Message();
                                message.what = CLEAR_FINISH;
                                handler.sendMessage(message);
                            }
                        }).start();
                    } else {
                        StartFound.setClickable(true);
                        Toast.makeText(GarbageClear.this, "Очистка не нужна", Toast.LENGTH_SHORT)
                                .show();
                    }
                }

            }
        });
    }

    public void DeleteFile() {
        for (File file : list) {
            file.delete();
        }
        list.clear();
    }

    /*
     *Scans a file for asynchronous tasks
     */
    class FoundTask extends AsyncTask<Void, String, List<FoundTask>> {
        String path;
        String[] Extension;

        public FoundTask(String path, String[] Extension) {
            this.path = path;
            this.Extension = Extension;
        }

        @Override
        protected List<FoundTask> doInBackground(Void... arg0) {
            final File[] files = new File(path).listFiles();
            String File_path = null;
            for (File file : files) {
                if (file.isFile()) {
                    publishProgress(file.getPath());
                    for (int i = 0; i < ClearType.length; i++) {
                        if (file.getPath()
                                .substring(
                                        file.getPath().length()
                                                - Extension[i].length())
                                .equals(Extension[i])) {
                            File_path = file.getAbsolutePath();
                            IsInstall = ClearUtil.TakeIsInstallApk(File_path,
                                    GarbageClear.this);
                            if (!IsInstall) {
                                long size = ClearUtil.getFileSize(file);
                                File_Grbagesize = File_Grbagesize + size;
                                list.add(file);
                            }
                        }
                    }
                } else if (file.isDirectory() && file.getPath().indexOf("/.") == -1) {
                    tasklist.add(new FoundTask(file.getPath(), Extension));
                    TaskNum++;
                }
            }
            return tasklist;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            //Change the displayed file path
            String value = values[0];
            file_path.setText(value);
        }

        @Override
        protected void onPostExecute(List<FoundTask> result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            //Execution is completed and the next task is started
            if (result != null && TaskNum != 0) {
                tasklist.get(0).execute();
                grbage_size.setText((int) ((float) File_Grbagesize / 1024 / 1024 / 2) + "");
                TaskNum--;
                tasklist.remove(0);
                if (progressbar_num < 100) {
                    progressbar_num++;
                    progressdisplay.setProgress(progressbar_num);
                } else if (progressbar_num == 100) {
                    progressbar_num = 0;
                }
            }
            //Task execution is complete
            else if (TaskNum == 0) {
                Message message = new Message();
                message.what = FOUND_FINISH;
                handler.sendMessage(message);
            }

        }

    }
}

