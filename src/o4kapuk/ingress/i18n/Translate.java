package o4kapuk.ingress.i18n;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher; 
import java.util.regex.Pattern; 

public class Translate {
	private static Map<String, String> dict = new HashMap<String, String>();
	private static Map<Pattern, String> dictRe = new HashMap<Pattern, String>();

	static
	{
		final FileHandle file = Gdx.files.internal("i18n/strings.json");
		if(file != null) {
			String jsonString = file.readString();

			Json json = new Json();
			dict = json.fromJson(dict.getClass(), jsonString);

			final Pattern pattern = Pattern.compile(".*%[a-z]+.*");
			for(Map.Entry<String, String> entry : dict.entrySet()) {
				String org = entry.getKey();
				String t = entry.getValue();

				final Matcher matcher = pattern.matcher(org);
				if(matcher.matches()) {
					final String orgRe = org.replace("(", "\\(")
								.replace(")", "\\)")
								.replaceAll("%[a-z0-9.]+", "(.*)");
					final Pattern p = Pattern.compile(orgRe);

					dictRe.put(p, t);
//					Log.v("translate", "Regex found: " + orgRe);
				}
			}
		}
	}

	public static CharSequence t(CharSequence org)
	{
		if(dict.containsKey(org)) {
//		Log.v("translate", org.toString() + " -> " + dict.get(org));
//			Thread.dumpStack();
			return dict.get(org);
		}

		for(Map.Entry<Pattern, String> entry : dictRe.entrySet()) {
			Matcher matcher = entry.getKey().matcher(org);
			if(matcher.matches()) {
//				Log.v("translate", "Regex match: " + org);
				final List<String> matches = new ArrayList<String>();

				for(int i = 1; i <= matcher.groupCount(); i++) {
					matches.add(matcher.group(i));
				}

				StringBuffer bufStr = new StringBuffer();
				final Matcher m = Pattern.compile("%[a-z0-9.]+").matcher(entry.getValue());
				int i = 0;
				while(m.find()) {
					m.appendReplacement(bufStr, matches.get(i++));
				}
				m.appendTail(bufStr);
				return bufStr.toString();
			}
		}

//		Log.v("translate", "Not found: " + org.toString());
		return org;
	}
}