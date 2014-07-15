package com.kohls.common.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import com.yantra.yfc.log.YFCLogCategory;

/**
 * 
 * @author Sterling
 * 
 */
public class KohlsDateUtil {

	public static YFCLogCategory kohlsLogger = YFCLogCategory.instance(KohlsDateUtil.class);
	/**
	 * The constant for the date format yyyyMMdd
	 */
	public static final String YYYYMMDD = "yyyyMMdd";

	/**
	 * Returns short default date string format i.e. <code>yyyyMMdd</code>
	 */
	protected static String getShortDefaultDateFormat() {
		// Yantra short default date string format
		return YYYYMMDD;
	}

	/**
	 * Converts date object to date-time string
	 * 
	 * @param inputDate
	 *            Date object to be converted
	 * @param outputFormat
	 *            Output format. Refer to
	 *            <code>java.text.SimpleDateFormat</code> for date format codes
	 * @return Formatted date-time string
	 * @throws IllegalArgumentException
	 *             for Invalid input
	 * @throws Exception
	 *             for all others
	 */
	public static String formatDate(java.util.Date inputDate,
			String outputFormat) throws IllegalArgumentException, Exception {
		// Validate input date value
		if (inputDate == null) {
			throw new IllegalArgumentException("Input date cannot "
					+ " be null in DateUtils.formatDate method");
		}

		// Validate output date format
		if (outputFormat == null) {
			throw new IllegalArgumentException("Output format cannot"
					+ " be null in DateUtils.formatDate method");
		}

		// Apply formatting
		SimpleDateFormat formatter = new SimpleDateFormat(outputFormat);
		return formatter.format(inputDate);
	}

	/**
	 * Returns default date-time string format i.e.
	 * <code>yyyyMMdd'T'HH:mm:ss</code>
	 * 
	 * @return Default date-time string i.e. yyyyMMdd'T'HH:mm:ss
	 */
	protected static String getDefaultDateFormat() {
		// Yantra default date-time string format
		return "yyyyMMdd'T'HH:mm:ss";
	}

	/**
	 * Returns default date-time string format i.e.
	 * <code>yyyyMMdd'T'HH:mm:ss</code>
	 * 
	 * @return Default date-time string i.e. yyyyMMdd'T'HH:mm:ss
	 */
	protected static String getDefaultDateFormatISO() {
		// Yantra default date-time string format
		return "yyyy-MM-dd'T'HH:mm:ss";
	}

	/**
	 * Converts date object to date-time string in default date format
	 * 
	 * @param inputDate
	 *            Date object to be converted
	 * @return Date-time string in default date format
	 * @throws IllegalArgumentException
	 *             for Invalid input
	 * @throws Exception
	 *             for all others
	 * @see getDefaultDateFormat
	 */
	public static String convertDate(java.util.Date inputDate)
			throws IllegalArgumentException, Exception {
		return formatDate(inputDate, getDefaultDateFormat());
	}

	/**
	 * Converts date-time string to Date object. Date-time string should be in
	 * default date format
	 * 
	 * @param inputDate
	 *            Date-time string to be converted
	 * @return Equivalent date object to input string
	 * @throws IllegalArgumentException
	 *             for Invalid input
	 * @throws Exception
	 *             for all others
	 */
	public static java.util.Date convertDate(String inputDate)
			throws IllegalArgumentException, Exception {

		// Validate input date value
		if (inputDate == null) {
			throw new IllegalArgumentException("Input date cannot "
					+ " be null in DateUtils.convertDate method");
		}
		if (inputDate.indexOf("T") != -1 && inputDate.indexOf("-") == -1) {
			return convertDate(inputDate, getDefaultDateFormat());
		} else if (inputDate.indexOf("T") != -1 && inputDate.indexOf("-") != -1) {

			return convertDate(inputDate, getDefaultDateFormatISO());
		} else {

			return convertDate(inputDate, getShortDefaultDateFormat());
		}
	}

	/**
	 * Converts date-time string to Date object
	 * 
	 * @param inputDate
	 *            Date-time string to be converted
	 * @param inputDateFormat
	 *            Format of date-time string. Refer to
	 *            <code>java.util.SimpleDateFormat</code> for date format codes
	 * @return Equivalent date object to input string
	 * @throws IllegalArgumentException
	 *             for Invalid input
	 * @throws Exception
	 *             for all others
	 */
	public static java.util.Date convertDate(String inputDate,
			String inputDateFormat) throws IllegalArgumentException, Exception {
		// Validate Input Date value
		if (inputDate == null) {
			throw new IllegalArgumentException("Input date cannot be null"
					+ " in DateUtils.convertDate method");
		}

		// Validate Input Date format
		if (inputDateFormat == null) {
			throw new IllegalArgumentException("Input date format cannot"
					+ " be null in DateUtils.convertDate method");
		}

		// Apply formatting
		SimpleDateFormat formatter = new SimpleDateFormat(inputDateFormat);

		ParsePosition position = new ParsePosition(0);
		return formatter.parse(inputDate, position);
	}

	/**
	 * Returns current date-time string in desired format
	 * 
	 * @param outputFormat
	 *            Desired output date-time format
	 * @return Current date-time string in desired format
	 * @throws IllegalArgumentException
	 *             for Invalid input
	 * @throws Exception
	 *             for all others
	 */
	public static String getCurrentTime(String outputFormat)
			throws IllegalArgumentException, Exception {
		// Create current date object
		Date currentDateTime = new Date();

		// Apply formatting
		return formatDate(currentDateTime, outputFormat);
	}

	/**
	 * Adds leading zeros to given value until max length is reached
	 */
	private static String addLeadingZeros(long value, int maxLength) {
		String result = Long.toString(value);

		int remaining = maxLength - result.length();
		for (int index = 0; index < remaining; index++) {
			result = "0" + result;
		}
		return result;
	}

	/**
	 * Returns time difference between two date objects
	 * 
	 * @param startTime
	 *            Start time
	 * @param endTime
	 *            End time. End time should be greater than Start time.
	 * @return Time difference in HH:mm:ss.SSS format
	 * @throws IllegalArgumentException
	 *             for Invalid input
	 * @throws Exception
	 *             for all others
	 */
	public static String getTimeDifference(Date startTime, Date endTime)
			throws IllegalArgumentException, Exception {
		// Validate Start time
		if (startTime == null) {
			throw new IllegalArgumentException("Start time cannot be"
					+ " null in DateUtils.getTimeDifference method");
		}

		// Validate End time
		if (endTime == null) {
			throw new IllegalArgumentException("End time cannot be "
					+ "null in DateUtils.getTimeDifference method");
		}

		// Check whether start time is less than end time
		if (startTime.after(endTime)) {
			throw new IllegalArgumentException(
					"End time should be greater than Start time in"
							+ "  DateUtils.getTimeDifference method");
		}

		long longStartTime = startTime.getTime();
		long longEndTime = endTime.getTime();

		// Get total difference in milli seconds
		long difference = longEndTime - longStartTime;

		long temp = difference;

		// Get milli seconds
		long milliseconds = temp % 1000;
		temp = temp / 1000;

		// Get seconds
		long seconds = temp % 60;
		temp = temp / 60;

		// Get Minutes
		long minutes = temp % 60;
		temp = temp / 60;

		// Get Hours
		long hours = temp;

		// Calculate result
		String result = addLeadingZeros(hours, 2) + ":"
				+ addLeadingZeros(minutes, 2) + ":"
				+ addLeadingZeros(seconds, 2) + "."
				+ addLeadingZeros(milliseconds, 3);

		// Format result and return
		return result;
	}

	/**
	 * Returns time difference between two date objects in hours
	 * 
	 * @param startTime
	 *            Start time
	 * @param endTime
	 *            End time. End time should be greater than Start time.
	 * @return Time difference in hours
	 * @throws IllegalArgumentException
	 *             for Invalid input
	 * @throws Exception
	 *             for all others
	 */
	public static long getTimeDifferenceInHours(Date startTime, Date endTime)
			throws IllegalArgumentException, Exception {
		// Validate Start time
		if (startTime == null) {
			throw new IllegalArgumentException("Start time cannot be null in "
					+ " DateUtils.getTimeDifferenceInHours method");
		}

		// Validate End time
		if (endTime == null) {
			throw new IllegalArgumentException("End time cannot be null in "
					+ "DateUtils.getTimeDifferenceInHours method");
		}

		// Check whether start time is less than end time
		if (startTime.after(endTime)) {
			throw new IllegalArgumentException(
					"End time should be greater than Start time in"
							+ "  DateUtils.getTimeDifferenceInHours method");
		}

		long differenceInHours = (endTime.getTime() - startTime.getTime())
				/ (1000 * 3600);

		// Return number of hours
		return differenceInHours;
	}

	/**
	 * Returns difference between two dates in days. This method does not
	 * consider timings for calculation. Result can be a positive or negative or
	 * zero value. This method results the difference as an integer.
	 * 
	 * @param startDate
	 *            Start date
	 * @param endDate
	 *            End date
	 * @return Difference in dates
	 * @throws IllegalArgumentException
	 *             for Invalid input
	 * @throws Exception
	 *             for all others
	 */
	public static int getDifferenceInDays(Date startDate, Date endDate)
			throws IllegalArgumentException, Exception {
		// Difference in days
		int differenceInDays = 0;

		// Validate Start date
		if (startDate == null) {
			throw new IllegalArgumentException("Start date cannot be"
					+ " null in DateUtils.getDifferenceInDays method");
		}

		// Validate End date
		if (endDate == null) {
			throw new IllegalArgumentException("End date cannot be "
					+ "null in DateUtils.getDifferenceInDays method");
		}

		// Calculate difference in days
		long difference = (endDate.getTime() - startDate.getTime())
				/ (1000 * 86400);

		// Set sign
		differenceInDays = (int) difference;

		return differenceInDays;
	}

	
	/**
	 * This method gives the time difference in seconds of the input date to the
	 * system date. If input date is before the system date the hours returned
	 * is a postive value other wise negative value.
	 * 
	 * @param inputDateString
	 * @return
	 */
	public static long diffInSecondsFromSystemDate(String inputDateString) {
		long diff = 0;
		try {
			
			
			Date dateSystemDate = new Date();
			
			String pattern = "yyyy-MM-dd'T'HH:mm:ssZ";
			
			java.text.DateFormat df = new java.text.SimpleDateFormat(pattern);
			java.util.Date inputDate = df.parse(inputDateString);
			
			long systemDateTime = dateSystemDate.getTime();			
			long inputDateTime = inputDate.getTime();
			
			diff = inputDateTime - systemDateTime ;
		 
		} catch (Exception e) {
			e.printStackTrace();
		}
	 
		return diff;

	}
	
	/**
	 * This method will take the Date input in Sterling Format (2012-06-01T12:52:12-04:00)
	 * and return the date as '2012-06-01T00:00:00-0400' ( ':' removed from timezone)
	 * 
	 * @param sInputSterlingDate
	 * @return formatedSystemDate (by removing the ':' from timezone)
	 */
	public static Date convertSterlingDateToSystemDate(String sInputSterlingDate,String sInFormat)
	{
		Date formatedSystemDate =null;
		
		try {
			
			
			SimpleDateFormat formatter = new SimpleDateFormat(sInFormat);
			
			if ((sInputSterlingDate.length() > 20) && (sInputSterlingDate.substring(22, 23).equals(":"))) {
				String sDate1 = sInputSterlingDate.substring(0, 22);
				String sDate2 = sDate1.concat(sInputSterlingDate.substring(23, 25));
				formatedSystemDate =  (Date) formatter.parse(convertToSystemTimeZone(sDate2));
				
			} else {
				
				formatedSystemDate = (Date)  formatter.parse(convertToSystemTimeZone(sInputSterlingDate));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return formatedSystemDate;
	}
	
	public static String convertToSystemTimeZone(String sDateInput)
	{
		String sConvertedTOTimeZone = null;
		
		try {
				 
			   DateFormat dateformat = new SimpleDateFormat(KohlsConstants.STERLING_TS_FORMAT);
			   Calendar cc  = Calendar.getInstance();
			 
			   dateformat.setTimeZone(cc.getTimeZone());
			   Date sdConvertedTOTimeZone = dateformat.parse(sDateInput);
			   sConvertedTOTimeZone = dateformat.format(sdConvertedTOTimeZone);
			   
			  
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sConvertedTOTimeZone;
	}
	
	public static String ConvertFormattedDateToGMT(String sDate,
			String sInFormat, String sOutFormat) {
		String sGMTTime = "";
		try {
			DateFormat formatter = new SimpleDateFormat(sInFormat);
			SimpleDateFormat formatter1 = new SimpleDateFormat(sOutFormat);
			Date date;
			if ((sDate.length() > 20) && (sDate.substring(22, 23).equals(":"))) {
				String sDate1 = sDate.substring(0, 22);
				String sDate2 = sDate1.concat(sDate.substring(23, 25));
				date = (Date) formatter.parse(sDate2);
			} else {
				date = (Date) formatter.parse(sDate);
			}
			Calendar here = Calendar.getInstance();
			int gmtoffset = here.get(Calendar.DST_OFFSET)
					+ here.get(Calendar.ZONE_OFFSET);
			Date GMTDate = new Date(date.getTime() - gmtoffset);

			sGMTTime = formatter1.format(GMTDate);

		} catch (ParseException e) {
		}
		return sGMTTime;
	}
	
	
	/**
	 * Adds specified interval to input date. Valid values for Interval are
	 * Calendar.YEAR, Calendar.MONTH, Calendar.DATE etc. See Calendar API for
	 * more information
	 * 
	 * @param inputDate
	 *            Input Date
	 * @param interval
	 *            Interval
	 * @param amount
	 *            Amount to add(use negative numbers to subtract
	 * @return Date after addition
	 * @throws IllegalArgumentException
	 *             for Invalid input
	 * @throws Exception
	 *             for all others
	 */
	public static Date addToDate(Date inputDate, int interval, int amount)
			throws IllegalArgumentException, Exception {
		// Validate Input date
		if (inputDate == null) {
			throw new IllegalArgumentException("Input date cannot be"
					+ " null in DateUtils.addToDate method");
		}

		// Get instance of calendar
		Calendar calendar = Calendar.getInstance();

		// Set input date to calendar
		calendar.setTime(inputDate);

		// Add amount to interval
		calendar.add(interval, amount);

		// Return result date;
		return calendar.getTime();
	}

	/**
	 * Returns current date-time string in Sterling Datetime format
	 * 
	 * @param outputFormat
	 *            Sterling date-time format
	 * @return Current date-time string in desired format
	 * @throws IllegalArgumentException
	 *             for Invalid input
	 * @throws Exception
	 *             for all others
	 */
	public Object getSterlingCurrentTimes() throws IllegalArgumentException,
			Exception {
		// Create current date object
		Date currentDateTime = new Date();

		// Apply the Sterling Date & Time Format
		String strDateFormat = formatDate(currentDateTime,
				KohlsConstants.STERLING_TS_FORMAT);

		String strNewDateFormat = strDateFormat.substring(0, 22) + ":"
				+ strDateFormat.substring(22, 24);

		// Apply formatting
		return strNewDateFormat;
	}

	/**
	 * Returns short date string format i.e. <code>yyMM</code>
	 */
	public static String getVeryShortDefaultDateFormat()
			throws IllegalArgumentException, Exception {
		Date currentDateTime = new Date();
		String strDateFormat = formatDate(currentDateTime,
				KohlsConstants.PO_SERVICE_ORDER_TS_FORMAT);
		return strDateFormat;
	}

	public static String getCurrentMonth() throws IllegalArgumentException,
			Exception {
		Date currentDateTime = new Date();
		String strDateFormat = formatDate(currentDateTime, "MM");
		return strDateFormat;
	}

	public static String getCurrentYear() throws IllegalArgumentException,
			Exception {
		Date currentDateTime = new Date();
		String strDateFormat = formatDate(currentDateTime, "yyyy");
		return strDateFormat;
	}

	/**
	 * Create a unique key based on the current time and with some randomness
	 * introduced in it
	 * 
	 * @return Unique Key based on the time
	 */
	public static String createUniqueKey() {
		String currentTime = getCurrentDateTime("yyyyMMddHHmmssSSS");
		Double randNumber = new Double(java.lang.Math.random() * 1000);
	
		currentTime += randNumber.intValue();
	
		return currentTime;
	}

	/**
	 * Gets the current time in the default format of "YYYY-MM-DD HH:MM:SS"
	 */
	public static String getCurrentDateTime(String inputFormat) {
		return formatDate(inputFormat, new java.util.Date());
	}
	/**
	 * Formats the input date(java.util.Date) in the given output format
	 * 
	 * @param outFormat
	 *            Desired output format of the Date
	 * @param dte
	 *            Date to be formatted to output format
	 * @return Date in the output format specified
	 */
	public static String formatDate(String outFormat, java.util.Date dte) {
		SimpleDateFormat formatter = new SimpleDateFormat(outFormat);
		return formatter.format(dte);
	}
		
	/**
	 * @param sInFormat is InputFormat of the dats to be compared
	 * @param sDate1 is the first date.
	 * @param sDate2 is the second date
	 * @return returns value greater than 0 if sDate1>sDate2 value less than 0
	 *         if sDate1<sDate2 value 0 if sDate1 = sDate2

	 */

	public static int compareDateStrings(String sInFormat, String sDate1, String sDate2) {
		int result = 0;
		try {
			DateFormat formatter = new SimpleDateFormat(sInFormat);
			Date date1 = (Date) formatter.parse(sDate1);
			Date date2 = (Date) formatter.parse(sDate2);

			result = date1.compareTo(date2);
		} catch (Exception e) {

			// System.out.println("error");

		}

		return result;

	}
}
