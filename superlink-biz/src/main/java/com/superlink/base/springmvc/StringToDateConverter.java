package com.superlink.base.springmvc;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.core.convert.converter.Converter;
import org.springframework.util.StringUtils;

import com.alibaba.common.lang.StringUtil;

/**
 * support yyyy-MM-dd，yyyy-MM-dd HH:mm:ss,yyyy-MM-dd HH:mm,yyyy-MM-dd HH
 * 
 */
public class StringToDateConverter implements Converter<String, Date> {

    public static final int SHORT_DATE  = 10;

    public static final int YYYYMM_DATE = 7;

    @Override
    public Date convert(String source) {
        if (StringUtil.isBlank(source)) {
            return null;
        }

        source = source.trim();
        if (!StringUtils.hasText(source)) {
            return null;
        }

        Date date = null;
        try {
            if (source.length() <= YYYYMM_DATE) {
                date = new java.sql.Date(new SimpleDateFormat("yyyy-MM").parse(source).getTime());
            } else if (source.length() <= SHORT_DATE) {
                date = new java.sql.Date(new SimpleDateFormat("yyyy-MM-dd").parse(source).getTime());
            } else if (source.length() == 16) {
                date = new java.sql.Timestamp(new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(source).getTime());
            } else if (source.length() == 13) {
                date = new java.sql.Timestamp(new SimpleDateFormat("yyyy-MM-dd HH").parse(source).getTime());
            } else {
                date = new java.sql.Timestamp(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(source).getTime());
            }
        } catch (ParseException ex) {
            IllegalArgumentException iae = new IllegalArgumentException("Could not parse date: " + ex.getMessage());
            iae.initCause(ex);
            throw iae;
        }

        return date;
    }

}
