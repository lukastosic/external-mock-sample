package ls.externalmocksample.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Common {

	public static String EXTERNAL_API_ADDRESS = "http://api.externalservice.net";
	
	public static String getCurrentDateTime() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		return dateFormat.format(date);
	}
	
	public static String getCurrentDate() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		return dateFormat.format(date);
	}
	
	public static String addDays(Date myDate, int days){
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal = Calendar.getInstance();
        cal.setTime(myDate);
        cal.add(Calendar.DATE, days); //minus number would decrement the days
        String formattedTime = format.format(cal.getTime());
        return formattedTime;
	}
	
	
	
}
