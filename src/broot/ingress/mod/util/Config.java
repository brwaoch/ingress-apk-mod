package broot.ingress.mod.util;

import java.text.SimpleDateFormat;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.SparseIntArray;
import broot.ingress.mod.BuildConfig.UiVariant;
import broot.ingress.mod.Mod;

public class Config {

	public static enum ChatTimeFormat {
		tf12("h:mma", "12:00 AM"), tf24("HH:mm:ss", "00:00:00"), tf24ns("HH:mm", "00:00");

		private final String           desc;
		private final SimpleDateFormat format;

		private ChatTimeFormat(final String dateTimeFormat, final String desc) {
			this.desc = desc;
			format = new SimpleDateFormat(dateTimeFormat);
		}

		public SimpleDateFormat getFormat() {
			return format;
		}

		@Override
		public String toString() {
			return desc;
		}
	}

	public static enum GpsLockTime {
		None(0, "Disabled"), HalfMinute(30000, "30sec"), Minute(60000, "1min"), TwoMinutes(120000, "2min"), FiveMinutes(
		        300000, "5min"), TenMinutes(600000, "10min"), FifteenMinutes(900000, "15min");

		private final String desc;
		private final int    lockTime;

		private GpsLockTime(final int time, final String desc) {
			this.desc = desc;
			lockTime = time;
		}

		public int getLockTime() {
			return lockTime;
		}

		@Override
		public String toString() {
			return desc;
		}
	}

	public static enum ItemsTab {
		HIDDEN("Hide"), AT_END("Last"), AT_START("First");

		private final String desc;

		private ItemsTab(final String desc) {
			this.desc = desc;
		}

		@Override
		public String toString() {
			return desc;
		}
	}

	public static enum AllowModRecycle {
		ALL("All mods"), NOT_VR("All but VR"), COMMON("Common only");

		private final String desc;

		private AllowModRecycle(final String desc) {
			this.desc = desc;
		}

		@Override
		public String toString() {
			return desc;
		}
	}

	public static enum Pref {
		SwapTouchMenuButtons(0, "Swap NAV/FIRE"),
		ItemsTab(0, "[ITEMS]", ItemsTab.class),
		ShowOrigItemsTab(1, "INVENTORY"),
		ShowAgentTab(1, "AGENT"),
		ShowIntelTab(1, "INTEL"),
		ShowMissionTab(1, "MISSIONS"),
		ShowRecruitTab(1, "RECRUIT"),
		ShowPasscodeTab(1, "PASSCODE"),
		ShowDeviceTab(1, "DEVICE"),
		ScannerZoomInAnimEnabled(1, "Scanner zoom in"),
		HackAnimEnabled(1, "Hacking"),
		RotateInventoryItemsEnabled(1, "Item rotation"),
		RecycleAnimationsEnabled(1, "Recycle animation"),
		XmFlowEnabled(1, "XM flow"),
		ShieldAnimEnabled(1, "Shield Animation"),
		Fullscreen(0, "Fullscreen"),
		ShowPortalVectors(1, "Portal vectors"),
		PortalParticlesEnabled(1, "Portal particles"),
		XmGlobsEnabled(1, ""),
		ScannerObjectsEnabled(1, "Scanner objects"),
		SimplifyInventoryItems(0, "Simplify Items"),
		ChatTimeFormat(0, "Chat time format", ChatTimeFormat.class),
		GpsLockTime(3, "Keep GPS on", GpsLockTime.class),
		Vibration(1, "Vibrate"),
		KeepScreenOn(0, "Keep screen on"),
		ChangePortalInfoDialog(0, "Modify portal info"),
		EnablePowerCubesRecycle(1, "Allow Cubes recycling"),
		AllowModRecycle(0, "Allow Mods recycling", AllowModRecycle.class),
		IsPrivacyOn(0, "Privacy"),
		NeedInviteNagBlock(0, "Block invite nag"),
		UiVariant(0, "", UiVariant.class);

		private final int      defaultValue;
		private final String   description;
		private final Class<?> classLink;

		private Pref(final int defValue, final String desc) {
			this(defValue, desc, Boolean.class);
		}

		private Pref(final int defValue, final String desc, final Class<?> clazz) {
			defaultValue = defValue;
			description = desc;
			classLink = clazz;
		}

		public final Class<?> getClassLink() {
			return classLink;
		}

		public final int getDefaultValue() {
			return defaultValue;
		}

		public final String getDescription() {
			return description;
		}
	}

	private static SparseIntArray configArray = new SparseIntArray();

	public static boolean getBoolean(final Pref pref) {
		return getRawValue(pref) != 0;
	}
	
	public static String getButtonText(Pref pref) {
        if (pref.getClassLink().equals(Boolean.class)){
        	return (getBoolean(pref)) ? "ON" : "OFF";
        }
        return getEnumValue(pref).toString();
    }

	public static <T extends Enum<T>> T getEnumValue(final Pref pref) {
		final T[] values = getEnumValues(pref);
		return values[getRawValue(pref)];
	}

	@SuppressWarnings("unchecked")
	private static <T extends Enum<T>> T[] getEnumValues(final Pref pref) {
		return ((Class<T>) pref.getClassLink()).getEnumConstants();
	}

	private static int getKey(final Pref pref) {
		return pref.ordinal();
	}

	private static int getRawValue(final Pref pref) {
		return configArray.get(getKey(pref));
	}

	public static void invertBooleanPreference(final Pref pref) {
		configArray.put(getKey(pref), (getRawValue(pref) + 1) % 2);
		save();
	}

	public static void load() {
		final SharedPreferences prefs = Mod.app.getSharedPreferences("mod", Context.MODE_PRIVATE);
		for (final Pref pref : Pref.values()) {
			configArray.put(getKey(pref), prefs.getInt(pref.name(), pref.getDefaultValue()));
		}

		Mod.onConfigLoaded();
	}

	public static void save() {
		final SharedPreferences.Editor e = Mod.app.getSharedPreferences("mod", Context.MODE_PRIVATE).edit();
		for (final Pref pref : Pref.values()) {
			e.putInt(pref.name(), configArray.get(getKey(pref)));
		}

		e.commit();
	}

	public static <T extends Enum<T>> void setNextValue(final Pref pref) {
		final T[] values = getEnumValues(pref);
		configArray.put(getKey(pref), (getRawValue(pref) + 1) % values.length);
		save();
	}
}
