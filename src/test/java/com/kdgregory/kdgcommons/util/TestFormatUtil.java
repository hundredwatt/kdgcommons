// Copyright (c) Keith D Gregory, all rights reserved
package com.kdgregory.kdgcommons.util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.junit.Test;
import static org.junit.Assert.*;


public class TestFormatUtil
{
    // we'll just verify that we can do the format; no easy way to verify
    // thread-safety
    @Test
    public void testLocalDateFormatting() throws Exception
    {
        Calendar cal = GregorianCalendar.getInstance();
        cal.clear();
        cal.set(2011, Calendar.JUNE, 30, 13, 14, 15);

        assertEquals("2011-06-30 13:14:15", FormatUtil.formatDate(cal, "yyyy-MM-dd HH:mm:ss"));

        Date date = new Date(cal.getTimeInMillis());
        assertEquals("2011-06-30 13:14:15", FormatUtil.formatDate(date, "yyyy-MM-dd HH:mm:ss"));

        assertEquals("2011-06-30 13:14:15", FormatUtil.formatDate(date.getTime(), "yyyy-MM-dd HH:mm:ss"));
    }


    @Test
    public void testTzDateFormatting() throws Exception
    {
        Calendar cal = GregorianCalendar.getInstance();
        cal.clear();
        cal.setTimeZone(TimeZone.getTimeZone("GMT"));
        cal.set(2011, Calendar.JUNE, 30, 13, 14, 15);

        assertEquals("2011-06-30 13:14:15", FormatUtil.formatDate(cal, "yyyy-MM-dd HH:mm:ss", "GMT"));
        assertEquals("2011-06-30 08:14:15", FormatUtil.formatDate(cal, "yyyy-MM-dd HH:mm:ss", "GMT-0500"));

        Date date = new Date(cal.getTimeInMillis());
        assertEquals("2011-06-30 13:14:15", FormatUtil.formatDate(date, "yyyy-MM-dd HH:mm:ss", "GMT"));
        assertEquals("2011-06-30 08:14:15", FormatUtil.formatDate(date, "yyyy-MM-dd HH:mm:ss", "GMT-0500"));

        assertEquals("2011-06-30 13:14:15", FormatUtil.formatDate(date.getTime(), "yyyy-MM-dd HH:mm:ss", "GMT"));
        assertEquals("2011-06-30 08:14:15", FormatUtil.formatDate(date.getTime(), "yyyy-MM-dd HH:mm:ss", "GMT-0500"));
    }


    @Test
    public void testNumberFormatting() throws Exception
    {
        assertEquals("1,234.56", FormatUtil.formatNumber(1234.5602, "#,##0.00"));
        assertEquals("1,234.00", FormatUtil.formatNumber(1234, "#,##0.00"));
        assertEquals("1,234.00", FormatUtil.formatNumber(1234L, "#,##0.00"));
    }


}
