package com.droid.activitys.eliminateprocess;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import java.util.ArrayList;
import java.util.List;


public class TaskInfoProvider {
	private PackageManager pmManager;
	private ActivityManager aManager;

	public TaskInfoProvider(Context context) {
		pmManager = context.getPackageManager();
		aManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
	}

	// Traverse the incoming list, passing all the application information to taskinfo
	public List<TaskInfo> GetAllTask(List<RunningAppProcessInfo> list) {
		List<TaskInfo> taskInfos = new ArrayList<TaskInfo>();
		for (RunningAppProcessInfo appProcessInfo : list) {
			TaskInfo info = new TaskInfo();
			int id = appProcessInfo.pid;
			info.setId(id);
			String packageName = appProcessInfo.processName;
			info.setPackageName(packageName);
			try {
				// ApplicationInfo is the encapsulation of the entire Application node inside the AndroidManifest file
				ApplicationInfo applicationInfo = pmManager.getPackageInfo(
						packageName, 0).applicationInfo;
				Drawable icon = applicationInfo.loadIcon(pmManager);
				info.setIcon(icon);
				String name = applicationInfo.loadLabel(pmManager).toString();
				info.setName(name);
				info.setIsSystemProcess(!IsSystemApp(applicationInfo));
				android.os.Debug.MemoryInfo[] memoryInfo = aManager
						.getProcessMemoryInfo(new int[] { id });
				int memory = memoryInfo[0].getTotalPrivateDirty();
				info.setMemory(memory);
				taskInfos.add(info);
				info = null;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				info.setName(packageName);
				info.setIsSystemProcess(true);
			}
		}
		return taskInfos;
	}

	public Boolean IsSystemApp(ApplicationInfo info) {
		// Some system applications can be updated,
		// if users download a system application to update the original,
		// it is not the system application, and this is to determine the situation
		if ((info.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
			return true;
		}
		else if ((info.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
			return true;
		}
		return false;
	}
}
