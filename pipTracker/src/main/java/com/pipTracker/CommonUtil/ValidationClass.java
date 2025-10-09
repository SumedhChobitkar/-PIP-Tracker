package com.pipTracker.CommonUtil;

import java.util.regex.Pattern;

public class ValidationClass {

    // Employee Related
    public static final Pattern NAME_PATTERN = Pattern.compile("^[A-Z][a-zA-Z ]*$");
    public static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w-]+(\\.[\\w-]+)*@([\\w-]+\\.)+[a-zA-Z]{2,7}$");
    public static final Pattern DEPARTMENT_PATTERN = Pattern.compile("^[a-zA-Z0-9_.@\\- '()]*$");
    public static final Pattern DESIGNATION_PATTERN = Pattern.compile("^[a-zA-Z0-9_.@\\- '()]*$");
    public static final Pattern SKILLS_PATTERN = Pattern.compile("^[a-zA-Z, ]+$");
    public static final Pattern KRA_PATTERN = Pattern.compile("^[a-zA-Z0-9 ,.'-]*$");
    public static final Pattern KPI_PATTERN = Pattern.compile("^[a-zA-Z0-9 ,.'-]*$");
    //public static final Pattern STATUS_PATTERN = Pattern.compile("^(Active|Inactive|OnHold)$");
    public static final Pattern PHOTO_URL_PATTERN = Pattern.compile("([^\\s]+(\\.(?i)(jpg|jpeg|png|gif|bmp|webp))$)");
    public static final Pattern ID_PATTERN = Pattern.compile("^[0-9]+$");

    // User Related
    public static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@#$%^&+=!])([a-zA-Z\\d@#$%^&+=!]{6,20})$");
    public static final Pattern ISREGISTERED_PATTERN = Pattern.compile("^(Yes|No)$");
    public static final Pattern FILE_TYPE_PATTERN = Pattern.compile("^(image/jpeg|image/png|image/gif|image/bmp|image/webp)$");
    public static final long MAX_FILE_SIZE = 100 * 1024 * 1024;
    public static final Pattern OTP_PATTERN = Pattern.compile("^[0-9]{6}$"); // 6 digit OTP

}




