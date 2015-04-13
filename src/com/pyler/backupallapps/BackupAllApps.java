package com.pyler.backupallapps;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class BackupAllApps implements IXposedHookLoadPackage {
	@Override
	public void handleLoadPackage(LoadPackageParam lpparam) throws Throwable {
		if (!"android".equals(lpparam.packageName)) {
			return;
		}
		XC_MethodHook backupAllApps = new XC_MethodHook() {
			@Override
			protected void afterHookedMethod(MethodHookParam param)
					throws Throwable {
				PackageInfo packageInfo = (PackageInfo) param.getResult();
				if (packageInfo != null) {
					int flags = packageInfo.applicationInfo.flags;
					if ((flags & ApplicationInfo.FLAG_ALLOW_BACKUP) == 0) {
						flags |= ApplicationInfo.FLAG_ALLOW_BACKUP;
					}
					packageInfo.applicationInfo.flags = flags;
					param.setResult(packageInfo);
				}

			}
		};
		XposedBridge.hookAllMethods(XposedHelpers.findClass(
				"com.android.server.pm.PackageManagerService",
				lpparam.classLoader), "getPackageInfo", backupAllApps);
	}
}
