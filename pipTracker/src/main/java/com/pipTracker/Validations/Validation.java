package com.pipTracker.Validations;

import java.util.regex.Pattern;

public class Validation {

    //FeedBack
    public static final Pattern toUserId_PATTERN=Pattern.compile("^[0-9]+$");
    public static final Pattern feedBackType_PATTERN=Pattern.compile("^(SELF|MANAGER)$");
    public static final Pattern comments_PATTERN=Pattern.compile("^[a-zA-Z0-9 ,.'@\\-()?!]*$");
    public static final Pattern rating_PATTERN=Pattern.compile("^[1-5]$");
    public static final Pattern isAnonoymous_PATTERN=Pattern.compile("^(true|false)$");

    //SkillGap
    public static final Pattern skill_PATTERN = Pattern.compile("^[a-zA-Z0-9 ,.'+\\- ]{2,50}$");
    public static final Pattern currentLevel_PATTERN=Pattern.compile("^[1-10]$");
    public static final Pattern training_PATTERN = Pattern.compile("^[a-zA-Z0-9 ,.'@\\-()]{2,100}$");

    //AuditLog/AuditLogArchieve
    public static final Pattern  entityname_PATTERN=Pattern.compile("^(FEEDBACK|PIP|REVIEW|SKILLGAP|REPORT)$");
    public static final Pattern entityId_PATTERN=Pattern.compile("^[0-9]+$");
    public static final Pattern action_PATTERN=Pattern.compile("^(CREATE|UPDATE|DELETE)$");
    public static final Pattern remarks_PATTERN=Pattern.compile("^[a-zA-Z0-9 ,.'@\\-()?!]*$");
    public static final Pattern archieveStatus_PATTERN=Pattern.compile("^(DELETED|RESTORED)$");

    //Notification
    public static final Pattern useridN_PATTERN=Pattern.compile("^[0-9]+$]");
    public static final Pattern title_PATTERN=Pattern.compile("^[a-zA-Z0-9 ,.'@\\-()?!]*$");
    public static final Pattern message_PATTERN=Pattern.compile("^[a-zA-Z0-9 ,.'@\\-()?!]*$");
    public static final Pattern type_PATTERN=Pattern.compile("^(REMINDER|ALERT|INFO)$");

}
