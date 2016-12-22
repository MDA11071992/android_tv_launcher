package com.droid.activitys.eliminateprocess;

import java.text.DecimalFormat;
public class TextFormater {
	public static String longtoString(long size) {
		DecimalFormat format = new DecimalFormat("####.00");
		if (size < 1024) {
			return size + "byte";
		} else if (size < (1 << 20)) // Left 20, equivalent to 1024 * 1024
		{
			float kSize = size >> 10; // Shift 10 right, equal to divide by 1024
			return format.format(kSize) + "KB";
		} else if (size < (1 << 30)) // Left 30, equivalent to 1024 * 1024 * 1024
		{
			float mSize = size >> 20;// Right shift 20, which is divided by 1024 divided by 1024
		return format.format(mSize) + "MB";
		} else if (size < (1 << 40)) {
			float gSize = size >> 30;
			return format.format(gSize) + "GB";
		} else {
			return "size error";
		}
	}
	public static String floattoString(float size){
		if(size<0){
			return 0+"";
		}else {
			return size+"MB";
		}
	}
}
