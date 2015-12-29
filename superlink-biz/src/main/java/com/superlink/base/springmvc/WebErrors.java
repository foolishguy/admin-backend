package com.superlink.base.springmvc;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.MessageSource;
import org.springframework.ui.ModelMap;
import org.springframework.util.Assert;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.support.RequestContextUtils;

import com.alibaba.common.lang.StringUtil;

/**
 * WEB error message
 * 
 */
public abstract class WebErrors {

    /**
     * email reg
     */
    public static final Pattern EMAIL_PATTERN    = Pattern.compile("^\\w+(\\.\\w+)*@\\w+(\\.\\w+)+$");
    /**
     * username reg
     */
    public static final Pattern USERNAME_PATTERN = Pattern.compile("^[0-9a-zA-Z\\u4e00-\\u9fa5\\.\\-@_]+$");

    /**
     */
    public static final Pattern MOBILE_PATTERN   = Pattern.compile("^[1][0-9][0-9]{9}$");

    /**
     * use HttpServletRequest to create WebErrors
     * 
     * @param request 
     */
    public WebErrors(HttpServletRequest request){
        WebApplicationContext webApplicationContext = RequestContextUtils.getWebApplicationContext(request);
        if (webApplicationContext != null) {
            LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(request);
            Locale locale;
            if (localeResolver != null) {
                locale = localeResolver.resolveLocale(request);
                this.messageSource = webApplicationContext;
                this.locale = locale;
            }
        }
    }

    public WebErrors(){
    }

    /**
     * constructor
     * 
     * @param messageSource
     * @param locale
     */
    public WebErrors(MessageSource messageSource, Locale locale){
        this.messageSource = messageSource;
        this.locale = locale;
    }

    public String getMessage(String code, Object... args) {
        if (messageSource == null) {
            throw new IllegalStateException("MessageSource cannot be null.");
        }
        return messageSource.getMessage(code, args, locale);
    }

    /**
     * add error code
     * 
     * @param code error code
     * @param args 
     * @see org.springframework.context.MessageSource#getMessage
     */
    public void addErrorCode(String code, Object... args) {
        getErrors().add(getMessage(code, args));
    }

    /**
     * add error code
     * 
     * @param code
     * @see org.springframework.context.MessageSource#getMessage
     */
    public void addErrorCode(String code) {
        getErrors().add(getMessage(code));
    }

    /**
     * add error code
     * 
     * @param error
     */
    public void addErrorString(String error) {
        getErrors().add(error);
    }

    /**
     * 
     * @param error
     */
    public void addError(String error) {
        // if messageSource exist
        if (messageSource != null) {
            error = messageSource.getMessage(error, null, error, locale);
        }
        getErrors().add(error);
    }

    /**
     * has error or not
     * 
     * @return
     */
    public boolean hasErrors() {
        return errors != null && errors.size() > 0;
    }

    /**
     * error count
     * 
     * @return
     */
    public int getCount() {
        return errors == null ? 0 : errors.size();
    }

    /**
     * error list
     * 
     * @return
     */
    public List<String> getErrors() {
        if (errors == null) {
            errors = new ArrayList<String>();
        }
        return errors;
    }

    /**
     * 
     * @param model
     * @return error page
     * @see org.springframework.ui.ModelMap
     */
    public String showErrorPage(ModelMap model) {
        toModel(model);
        return getErrorPage();
    }

    /**
     * save error to ModelMap
     * 
     * @param model
     */
    public void toModel(Map<String, Object> model) {
        Assert.notNull(model);
        if (!hasErrors()) {
            throw new IllegalStateException("no errors found!");
        }
        model.put(getErrorAttrName(), getErrors());
    }

    public boolean ifNull(Object o, String field) {
        if (o == null) {
            addErrorCode("error.required", field);
            return true;
        } else {
            return false;
        }
    }

    public boolean ifBlank(String o, String error) {
        if (StringUtil.isBlank(o)) {
            this.addError(error);
            return true;
        } else {
            return false;
        }
    }

    public boolean ifEmpty(Object[] o, String field) {
        if (o == null || o.length <= 0) {
            addErrorCode("error.required", field);
            return true;
        } else {
            return false;
        }
    }

    public boolean ifBlank(String s, String field, int maxLength) {
        if (StringUtils.isBlank(s)) {
            this.addErrorString(field + "不能为空!");
            return true;
        }
        if (ifMaxLength(s, field, maxLength)) {
            return true;
        }
        return false;
    }

    public boolean ifMaxLength(String s, String field, int maxLength) {
        if (s != null && s.length() > maxLength) {
            this.addErrorString(field + "超过" + maxLength + "字");
            return true;
        }
        return false;
    }

    public boolean ifOutOfLength(String s, String field, int minLength, int maxLength) {
        if (s == null) {
            this.addErrorString(field + "不能为空!");
            return true;
        }
        int len = s.length();
        if (len < minLength || len > maxLength) {
            this.addErrorString(field + "由" + minLength + "-" + maxLength + "个字组成!");
            return true;
        }
        return false;
    }

    public boolean ifNotEmail(String email, String field, int maxLength) {
        if (ifBlank(email, field, maxLength)) {
            return true;
        }
        Matcher m = EMAIL_PATTERN.matcher(email);
        if (!m.matches()) {
            addErrorCode("error.email", field);
            return true;
        }
        return false;
    }

    public boolean ifNotMobile(String mobile, String field, int maxLength) {
        if (ifBlank(mobile, field, maxLength)) {
            return true;
        }
        Matcher m = MOBILE_PATTERN.matcher(mobile);
        if (!m.matches()) {
            this.addErrorString("手机号码格式不正确!");
            return true;
        }
        return false;
    }

    public boolean ifNotUsername(String username, String field, int minLength, int maxLength) {
        if (ifOutOfLength(username, field, minLength, maxLength)) {
            return true;
        }
        Matcher m = USERNAME_PATTERN.matcher(username);
        if (!m.matches()) {
            addErrorCode("error.username", field);
            return true;
        }
        return false;
    }

    public boolean ifNotExist(Object o, Class<?> clazz, Serializable id) {
        if (o == null) {
            addErrorCode("error.notExist", clazz.getSimpleName(), id);
            return true;
        } else {
            return false;
        }
    }

    public void noPermission(Class<?> clazz, Serializable id) {
        addErrorCode("error.noPermission", clazz.getSimpleName(), id);
    }

    private MessageSource messageSource;
    private Locale        locale;
    private List<String>  errors;

    public MessageSource getMessageSource() {
        return messageSource;
    }

    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    /**
     * get locale info
     * 
     * @return
     */
    public Locale getLocale() {
        return locale;
    }

    /**
     * set locale info
     * 
     * @param locale
     */
    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    /**
     * 
     * @return
     */
    protected abstract String getErrorPage();

    /**
     * 
     * @return
     */
    protected abstract String getErrorAttrName();
}
