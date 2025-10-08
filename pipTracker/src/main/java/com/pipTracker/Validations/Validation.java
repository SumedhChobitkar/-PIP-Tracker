package com.pipTracker.Validations;

import java.util.regex.Pattern;

public class Validation{


    //Numeric IDs(only positive numbers allowed)
    public static final Pattern REVIEWER_ID_PATTERN =
            Pattern.compile("^[1-9][0-9]*$");


    //status (Enum values:ACTIVE,COMPLETED,FAILED)
    public static final Pattern STATUS_PATTERN =
            Pattern.compile("^(ACTIVE|COMPLETED|FAILED)$");


    //Goals:allow letters,numbers,spaces,and punctuation up to 500 chars
    public static final Pattern GOALS_PATTERN =
            Pattern.compile("^[A-Za-z0-9 .,;:'\"!?()\\-\\n\\r]{1,500}$");


    //progress: same rules,max 500 chars
    public static final Pattern PROGRESS_PATTERN =
            Pattern.compile("[A-Za-z0-9.,;:'\"!?()\\n\\r]{0,500}$");


    //outcome:same rules,max 500 chars
    public static final Pattern OUTCOME_PATTERN =
            Pattern.compile("^[A-Za-z0-9.,;:'\"!?()\\-\\n\\r]{0,500}$");


    //comments: same rules,max 1000 chars
    public static final Pattern COMMENTS_PATTERN =
            Pattern.compile("^[A-Za-z0-9.,;:'\"!?()\\-\\n\\r]{0,1000}?$");


    //Dates in YYYY-MM-dd format (for validation at input level if needed)
    public static final Pattern DATE_PATTERN =
            Pattern.compile("^\\d{4}-\\d{2}-\\d{2}$");




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
    public static final Pattern title_PATTERN=Pattern.compile("^[a-zA-Z0-9 ,.'@\\-()?!]*$");
    public static final Pattern message_PATTERN=Pattern.compile("^[a-zA-Z0-9 ,.:;'@\\-()?!]*$");
    public static final Pattern type_PATTERN=Pattern.compile("^(REMINDER|ALERT|INFO)$");

}



