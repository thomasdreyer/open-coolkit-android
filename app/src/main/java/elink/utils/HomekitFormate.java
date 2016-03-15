package elink.utils;

import com.coolkit.common.HLog;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class HomekitFormate {
	public static int toUTCHour(int hour) {
		int UTCHour = hour + (new Date()).getTimezoneOffset() / 60;
		if (UTCHour < 0)
			return UTCHour + 24;
		if (UTCHour > 23)
			return UTCHour - 24;
		return UTCHour;
	};

	public static int toLocalHour(int hour) {
		int localHour = hour - (new Date()).getTimezoneOffset() / 60;
		if (localHour < 0)
			return localHour + 24;
		if (localHour > 23)
			return localHour - 24;
		return localHour;
	};

	// 2015-07-14T03:04:00.000Z
	private static Calendar getStringToCal(String date) {
		HLog.i("data", "at:" + date);
		final String year = date.substring(0, 4);
		final String month = date.substring(5, 7);
		final String day = date.substring(8, 10);
		final String hour = date.substring(11, 13);
		final String minute = date.substring(14, 16);
		final String second = date.substring(17, 19);
		final int millisecond = Integer.valueOf(date.substring(20, 23));
		Calendar result = new GregorianCalendar(Integer.valueOf(year),
				Integer.valueOf(month) - 1, Integer.valueOf(day),
				toLocalHour(Integer.valueOf(hour)), Integer.valueOf(minute),
				Integer.valueOf(second));
		result.set(Calendar.MILLISECOND, millisecond);
		return result;
	}

	public static Date getLocaleDateFromUtcStr(String utr) {
		Date date = new Date(getStringToCal(utr).getTimeInMillis());
		return date;
	}
	
	public static Date getLocal(String date) {
		final String year = date.substring(0, 4);
		final String month = date.substring(5, 7);
		final String day = date.substring(8, 10);
		final String hour = date.substring(11, 13);
		final String minute = date.substring(14, 16);
		final String second = date.substring(17, 19);

		// final int millisecond = Integer.valueOf(date.substring(20, 23));
		// Calendar result = new GregorianCalendar(Integer.valueOf(year),
		// Integer.valueOf(month) - 1, Integer.valueOf(day),
		// toLocalHour(Integer.valueOf(hour)), Integer.valueOf(minute),
		// Integer.valueOf(second));
		// result.set(Calendar.MILLISECOND, millisecond);

		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, Integer.parseInt(year));
		cal.set(Calendar.MONTH, Integer.parseInt(month) - 1);
		cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(day));
		cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hour));
		cal.set(Calendar.MINUTE, Integer.parseInt(minute));
		cal.set(Calendar.MILLISECOND,0);

		int zoneOffset = cal.get(java.util.Calendar.ZONE_OFFSET);

		// 3、取得夏令时差：
		int dstOffset = cal.get(java.util.Calendar.DST_OFFSET);

		// 4、从本地时间里扣除这些差量，即可以取得UTC时间：
		cal.add(java.util.Calendar.MILLISECOND, +(zoneOffset + dstOffset));

		return new Date(cal.getTimeInMillis());
	}
}
