package broot.ingress.mod;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.PowerManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import broot.ingress.mod.BuildConfig.UiVariant;
import broot.ingress.mod.util.Config;
import broot.ingress.mod.util.Config.Pref;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.nianticproject.ingress.NemesisActivity;
import com.nianticproject.ingress.common.app.NemesisMemoryCache;
import com.nianticproject.ingress.common.app.NemesisWorld;
import com.nianticproject.ingress.common.assets.AssetFinder;
import com.nianticproject.ingress.common.inventory.MenuControllerImpl;
import com.nianticproject.ingress.common.scanner.ScannerStateManager;
import com.nianticproject.ingress.common.scanner.visuals.PortalParticleRender;
import com.nianticproject.ingress.common.ui.elements.PortalInfoDialog;
import com.nianticproject.ingress.common.ui.elements.AvatarPlayerStatusBar;

public class Mod {

	public static Application           app;
	public static NemesisActivity       nemesisActivity;
	public static NemesisWorld          world;
	public static NemesisMemoryCache    cache;
	public static MenuControllerImpl    menuController;
	public static AssetFinder           assetFinder;
	public static Skin                  skin;
	public static ScannerStateManager   scannerStateManager;

	public static PortalInfoDialog      portalInfoDialog;
    public static AvatarPlayerStatusBar avatarPlayerStatusBar;
    public static long lastTap;
    public static boolean statusBarIsVisible;

	public static DisplayMetrics        displayMetrics;
	public static UiVariant             currUiVariant;

	public static PowerManager.WakeLock ksoWakeLock;

	public static String getFullVersion() {
		try {
			return "v" + app.getPackageManager().getPackageInfo(app.getPackageName(), 0).versionName + "-broot-"
			        + BuildConfig.MOD_VERSION;
		} catch (final PackageManager.NameNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	public static void init() {
        statusBarIsVisible = true;
        lastTap = System.currentTimeMillis();
		// Debug.waitForDebugger();
	}

	public static void onConfigLoaded() {
		PortalParticleRender.enabled = Config.getBoolean(Pref.PortalParticlesEnabled);
		// EnergyGlobVisuals.initEnabled = Config.xmGlobsEnabled;
	}

	public static void restartApp() {
		final Context ctx = Mod.app;
		final Intent i = ctx.getPackageManager().getLaunchIntentForPackage(ctx.getPackageName());
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		final AlarmManager mgr = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
		mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, PendingIntent.getActivity(ctx, 0, i, 0));

		HacksTimer.save();

		android.os.Process.killProcess(android.os.Process.myPid());
	}

	public static float getUiScale() {
		final int w = Mod.displayMetrics.widthPixels;
		if(w < 320) {
			Log.v("broot", "return 0.4");
			return 0.4f;
		}

		if(w < 480) {
			return 0.6f;
		}

		return 1f;
	}

	public static String HackController_portalGuid;

	public static class HacksTimer {
		private final static HashMap<String,Long> hackTimes = new HashMap<String,Long>(); // portalGuid->timestamp
		public static void registerHackAtTime(final String portalGuid, final long timestamp) {
			hackTimes.put(portalGuid, timestamp);
		}
		public static long getLastHackTime(final String portalGuid) {
			final long result;
			if (hackTimes.containsKey(portalGuid)) {
				result = hackTimes.get(portalGuid);
			} else {
				result = PORTAL_NOT_HACKED_YET;
			}
			return result;
		}
		public static final long PORTAL_NOT_HACKED_YET = 0;
		private static final String INTERNAL_STORAGE_FILENAME = "hacktimer";
		public static void save() {
			ObjectOutputStream oos = null;
			try {
				oos = new ObjectOutputStream(app.openFileOutput(INTERNAL_STORAGE_FILENAME, Context.MODE_PRIVATE)); // recreate
				oos.writeObject(hackTimes);
			} catch (final Exception e) {
				// ignore
			} finally {
				if (oos != null) {
					try {
						oos.close();
					} catch (final Exception e) {
						// ignore
					}
				}
			}
		}
		public static void load() {
			ObjectInputStream ois = null;
			try {
				ois = new ObjectInputStream(app.openFileInput(INTERNAL_STORAGE_FILENAME));
				try {
					final HashMap<String,Long> map = (HashMap<String,Long>)ois.readObject();
					hackTimes.clear();
					hackTimes.putAll(map);
				} catch (final ClassNotFoundException e) { // from ObjectInputStream.readObject, file corrupted, delete
					app.deleteFile(INTERNAL_STORAGE_FILENAME);
				} catch (final OptionalDataException e) { // from ObjectInputStream.readObject, file corrupted, delete
					app.deleteFile(INTERNAL_STORAGE_FILENAME);
				} catch (final IOException e) { // from ObjectInputStream.readObject, i/o error, maybe transient? leave data
					// ignore
				}
			} catch (final StreamCorruptedException e) { // from ObjectInputStream.<init>, file corrupted, delete
				app.deleteFile(INTERNAL_STORAGE_FILENAME);
			} catch (final Exception e) {
				// ignore
			} finally {
				if (ois != null) {
					try {
						ois.close();
					} catch (final Exception e) {
						// ignore
					}
				}
			}
		}
	}

	public static void updateCurrUiVariant() {
		currUiVariant = Config.getEnumValue(Pref.UiVariant);
		if (currUiVariant != UiVariant.auto) {
			return;
		}

		final List<String> names = new ArrayList<String>();
		switch (assetFinder.screenDensity) {
		case XXHIGH:
			names.add("xxhdpi");
		case XHIGH:
			names.add("xhdpi");
			break;
		case HIGH:
			break;
		case MEDIUM:
		case LOW:
			final int w = Mod.displayMetrics.widthPixels;
			if (w < 320) {
				names.add("qvga");
				names.add("ingressopt_qvga");
			} else if (w < 480) {
				names.add("hvga");
				names.add("ingressopt_hvga");
			}
			break;
		}
		names.add("normal");
		for (final String name : names) {
			for (final UiVariant variant : UiVariant.values()) {
				if (variant.name().equals(name)) {
					currUiVariant = variant;
					return;
				}
			}
		}
		currUiVariant = UiVariant.values()[1];
	}

	public static void updateFullscreenMode() {
		nemesisActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				final WindowManager.LayoutParams attrs = nemesisActivity.getWindow().getAttributes();
				if (Config.getBoolean(Pref.Fullscreen)) {
					attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
				} else {
					attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
				}
				nemesisActivity.getWindow().setAttributes(attrs);
			}
		});
	}

	public static void updateKeepScreenOn() {
		nemesisActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (Config.getBoolean(Pref.KeepScreenOn)) {
					if (!ksoWakeLock.isHeld()) {
						ksoWakeLock.acquire();
					}
				} else {
					if (ksoWakeLock.isHeld()) {
						ksoWakeLock.release();
					}
				}
			}
		});
	}
}
