package com.superlink.base.springmvc;

import java.beans.PropertyEditorSupport;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.util.StringUtils;

/**
 * support yyyy-MM-dd，yyyy-MM-dd HH:mm:ss,yyyy-MM-dd HH:mm,yyyy-MM-dd HH
 * 
 */
public class DateTypeEditor extends PropertyEditorSupport {

    public static final int SHORT_DATE  = 10;

    public static final int YYYYMM_DATE = 7;

    public void setAsText(String text) throws IllegalArgumentException {
        text = text.trim();
        if (!StringUtils.hasText(text)) {
            setValue(null);
            return;
        }
        try {
            if (text.length() <= YYYYMM_DATE) {
                setValue(new java.sql.Date(new SimpleDateFormat("yyyy-MM").parse(text).getTime()));
            } else if (text.length() <= SHORT_DATE) {
                setValue(new java.sql.Date(new SimpleDateFormat("yyyy-MM-dd").parse(text).getTime()));
            } else if (text.length() == 16) {
                setValue(new java.sql.Timestamp(new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(text).getTime()));
            } else if (text.length() == 13) {
                setValue(new java.sql.Timestamp(new SimpleDateFormat("yyyy-MM-dd HH").parse(text).getTime()));
            } else {
                setValue(new java.sql.Timestamp(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(text).getTime()));
            }
        } catch (ParseException ex) {
            IllegalArgumentException iae = new IllegalArgumentException("Could not parse date: " + ex.getMessage());
            iae.initCause(ex);
            throw iae;
        }
    }

    /**
     * Format the Date as String, using the specified DateFormat.
     */
    public String getAsText() {
        Date value = (Date) getValue();
        return (value != null ? new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(value) : "");
    }
}
