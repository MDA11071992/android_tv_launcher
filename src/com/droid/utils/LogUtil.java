package com.droid.utils;


import android.content.Context;
import android.os.Environment;
import android.util.Log;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;


public class LogUtil {
    public static int LEVEL = 3;
    public final static int V = 1;
    public final static int W = 2;
    public final static int I = 3;
    public final static int D = 4;
    public final static int E = 5;
    private final static int P = Integer.MAX_VALUE;
    private static final String _L = "[";
    private static final String _R = "]";
    private static boolean IS_SYNS = true;
    private static String LOG_FILE_DIR = SDcardUtil.getPath() + File.separator + "smileTvLog";
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
    private static boolean IF_START_NEWLOG = false;
    private static String CURRENT_LOG_NAME = "";
    private static int FILE_LOG_COUNT = 0;
    private static int LOG_MAX_SIZE = 6 * 1024 * 1024;
    private static Pattern pattern = Pattern.compile("(\\w+/)+");
    public static  void setSyns(boolean flag) {
        synchronized (LogUtil.class){
            IS_SYNS = flag;
        }
    }
    public static  void startNewLog() {
        IF_START_NEWLOG = true;
    }

    public static  void i(String message) {
        if (LEVEL <= I) {
            Log.i(getTag(message), message);
            if (IS_SYNS) {
                LogFile.writeLog(message);
            }
        }
    }

    public static  void i(Exception exp) {
        if (LEVEL <= I) {
            Log.i(getTag(exp),getMessage(exp));
            if (IS_SYNS) {
                LogFile.writeLog(exp);
            }
        }
    }

    public static  void e(String message) {
        if (LEVEL <= E) {
            Log.e(getTag(message), message);
            if (IS_SYNS) {
                LogFile.writeLog(message);
            }
        }
    }

    public static  void e(Exception exp) {
        if (LEVEL <= E) {
            Log.e(getTag(exp),getMessage(exp));
            if (IS_SYNS) {
                LogFile.writeLog(exp);
            }
        }
    }

    public static  void w(String message) {
        if (LEVEL <= W) {
            Log.w(getTag(message), message);
            if (IS_SYNS) {
                LogFile.writeLog(message);
            }
        }
    }

    public static  void w(Exception exp) {
        if (LEVEL <= W) {
            Log.w(getTag(exp),getMessage(exp));
            if (IS_SYNS) {
                LogFile.writeLog(exp);
            }
        }
    }

    public static  void v(String message) {
        if (LEVEL <= V) {
            Log.v(getTag(message), message);
            if (IS_SYNS) {
                LogFile.writeLog(message);
            }
        }
    }

    public static  void v(Exception exp) {
        if (LEVEL <= V) {
            Log.v(getTag(exp),getMessage(exp));
            if (IS_SYNS) {
                LogFile.writeLog(exp);
            }
        }
    }

    public static  void d(String message) {
        if (LEVEL <= D) {
            Log.d(getTag(message), message);
            if (IS_SYNS) {
                LogFile.writeLog(message);
            }
        }
    }

    public static  void d(Exception exp) {
        if (LEVEL <= D) {
            Log.d(getTag(exp),getMessage(exp));
            if (IS_SYNS) {
                LogFile.writeLog(exp);
            }
        }
    }

    public static  void print(String message) {
        if (LEVEL <= P) {
            Log.e(getTag(message), message);
            if (IS_SYNS)
                LogFile.writeLog(message);
        }
    }

    public static  void print(Exception exp) {
        if (LEVEL <= P) {
            Log.e(getTag(exp), getMessage(exp));
            if (IS_SYNS)
                LogFile.writeLog(exp);
        }
    }

    private static String getTag(String msg) {
        if (msg != null) {
            //since jdk 1.5
            if (Thread.currentThread().getStackTrace().length > 0) {
                String name = Thread.currentThread().getStackTrace()[0].getClassName();
                return _L + name.substring(name.lastIndexOf(".") + 1) + _R;
            }
        }
        return _L + "null" + _R;
    }

    private static String getTag(Exception exp) {
        if (exp != null) {
            if (exp.getStackTrace().length > 0) {
                String name = exp.getStackTrace()[0].getClassName();
                return _L + name.substring(name.lastIndexOf(".") + 1) + _R;
            }
            return _L + "exception" + _R;
        }
        return _L + "null" + _R;
    }

    private static String getMessage(Exception exp) {
        StringBuilder sb = new StringBuilder();
        StackTraceElement[] element =  exp.getStackTrace();
        int n = 0;
        sb.append("\n");
        sb.append(exp.toString());
        sb.append("\n");
        for (StackTraceElement e : element) {
            sb.append(e.getClassName());
            sb.append(".");
            sb.append(e.getMethodName());
            sb.append("[");
            sb.append(e.getLineNumber());
            sb.append("]");
            sb.append("\n");
            n++;
            if (n >= 2) break;
        }
        if (exp.getCause() != null) {
            sb.append("Caused by: ");
            sb.append(exp.getMessage());
        }
        return sb.toString();
    }


    public  static void setLogFilePath(String path) {
        String url = SDcardUtil.getPath() + File.separator + path;
        boolean flag = pattern.matcher(url).matches();
        if (flag) {
            LOG_FILE_DIR = url;
        } else {
            LogFile.print("the url is not match file`s dir");
        }
    }

    public  static void setDefaultFilePath(Context context) {
        String pkName = context.getPackageName().replaceAll("\\.", "\\/");
        setLogFilePath(pkName);
    }
    private static String getCurrTimeDir() {
        return sdf.format(new Date());
    }


    private static class LogFile {
        private static void print(String msg) {
            if (LEVEL <= P) {
                Log.e(getTag(msg), msg);
            }
        }

        public static synchronized void writeLog(String message) {
            File f = getFile();
            if (f != null) {
                try {
                    FileWriter fw = new FileWriter(f , true);
                    BufferedWriter bw = new BufferedWriter(fw);
                    bw.append("\n");
                    bw.append(message);
                    bw.append("\n");
                    bw.flush();
                    bw.close();
                    fw.close();
                } catch (IOException e) {
                    print("writeLog error, " + e.getMessage());
                }
            } else {
                print("writeLog error, due to the file dir is error");
            }
        }

        public static synchronized void writeLog(Exception exp) {
            File f = getFile();
            if (f != null) {
                try {
                    FileWriter fw = new FileWriter(f , true);
                    PrintWriter pw = new PrintWriter(fw);
                    pw.append("\n");
                    exp.printStackTrace(pw);
                    pw.flush();
                    pw.close();
                    fw.close();
                } catch (IOException e) {
                    print("writeLog error, " + e.getMessage());
                }
            } else {
                print("writeLog error, due to the file dir is error");
            }
        }

        private static  File getFile() {
            if ("".equals(LOG_FILE_DIR)) {
                return null;
            }
            synchronized (LogUtil.class) {
                if (!IF_START_NEWLOG) {
                    File currFile = new File(CURRENT_LOG_NAME);
                    if (currFile.length() >= LOG_MAX_SIZE) {
                        IF_START_NEWLOG = true;
                        return getFile();
                    }
                    return currFile;
                }
                File f = new File(LOG_FILE_DIR);
                if (!f.exists()) {
                    f.mkdirs();
                }
                File file = new File(f.getAbsolutePath() + File.separator + getCurrTimeDir() + ".log");
                if (!file.exists()) {
                    try {
                        file.createNewFile();
                        FILE_LOG_COUNT = 0;
                        IF_START_NEWLOG = false;
                        CURRENT_LOG_NAME = file.getAbsolutePath();
                    } catch (IOException e) {
                        print("createFile error , " + e.getMessage());
                    }
                } else {
                    if (IF_START_NEWLOG) {
                        FILE_LOG_COUNT ++;
                        return new File(f.getAbsolutePath() + File.separator + getCurrTimeDir() + "_" + FILE_LOG_COUNT+ ".log");
                    }
                }
                return file;
            }
        }
    }


    private static class SDcardUtil {

      public static String getAbsPath() {
          if (isMounted()) {
              return Environment.getExternalStorageDirectory().getAbsolutePath();
          }
          return "";
      }
        public static String getPath() {
            if (isMounted()) {
                return Environment.getExternalStorageDirectory().getPath();
            }
            LogFile.print("please check if sd card is not mounted");
            return "";
        }
        public static boolean isMounted() {
            return Environment.isExternalStorageEmulated();
        }
    }
}
