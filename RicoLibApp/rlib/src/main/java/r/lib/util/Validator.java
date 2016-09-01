package r.lib.util;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Rico on 2015/8/18.
 */
public class Validator {

    private Validator() {
    }


    public static boolean validate(@NonNull ValidateConfiguration configuration) {
        boolean result = true;
        switch (configuration.validateType) {
            case EMAIL:
                result = matchPattern(configuration.validateStr, ValidateType.EMAIL.pattern);
                break;
            case MOBILE:
                result = matchPattern(configuration.validateStr, ValidateType.MOBILE.pattern);
                break;
            case LENGTH:
                result = configuration.validateStr != null && configuration.validateStr.length() >= configuration.validateType.minLength;
                break;
            case EQUAL:
                result = configuration.validateStr != null && configuration.validateStr.toString().equals(configuration.validateType.pattern.toString());
                break;
            case IDENTITY_CARD:
                LogUtil.info("身份证－" + configuration.validateStr);
                result = matchPattern(configuration.validateStr, ValidateType.IDENTITY_CARD.pattern);
                break;
            case PLATE_NUMBER:
                result = matchPattern(configuration.validateStr, ValidateType.PLATE_NUMBER.pattern);
                break;
            case NUMBER_LARGER_THAN:
                result = !TextUtils.isEmpty(configuration.validateStr);
                if (!result)
                    try {
                        double temp = Double.valueOf(configuration.validateStr.toString());
                        result = temp >= configuration.validateType.patternNumber;
                    } catch (NumberFormatException e) {
                        result = false;
                    }
                break;
            case NUMBER_SMALLER_THAN:
                result = !TextUtils.isEmpty(configuration.validateStr);
                if (!result)
                    try {
                        double temp = Double.valueOf(configuration.validateStr.toString());
                        result = temp <= configuration.validateType.patternNumber;
                    } catch (NumberFormatException e) {
                        result = false;
                    }
                break;
        }
        LogUtil.err("验证－" + result);
        if (!result) {
            if (!TextUtils.isEmpty(configuration.errStr)) {
                if (configuration.toast && configuration.context != null)
                    Toast.makeText(configuration.context, configuration.errStr, Toast.LENGTH_SHORT).show();
                else if (configuration.validateFailedListener != null)
                    configuration.validateFailedListener.validateFailed(configuration.errStr);
            }
            if (configuration.animationView != null)
                configuration.animationView.startAnimation(configuration.animation);
        }
        configuration.release();
        return result;
    }


    private static boolean matchPattern(CharSequence str, CharSequence pattern) {
        Pattern regex = Pattern.compile(pattern.toString());
        Matcher matcher = regex.matcher(str);
        return matcher.matches();
    }

    public static class ValidateConfiguration {
        Context context;
        ValidateType validateType;
        CharSequence errStr;
        View animationView;
        Animation animation;
        CharSequence validateStr;
        OnValidateFailedListener validateFailedListener;
        boolean toast = true;

        public ValidateConfiguration(Context context) {
            this.context = context;
        }

        public ValidateConfiguration setToast(boolean toast) {
            this.toast = toast;
            return this;
        }

        public ValidateConfiguration setValidateType(ValidateType validateType) {
            this.validateType = validateType;
            return this;
        }

        public ValidateConfiguration setOnValidateFailedListener(OnValidateFailedListener validateFailedListener) {
            this.validateFailedListener = validateFailedListener;
            return this;
        }

        public ValidateConfiguration setErrStr(CharSequence errStr) {
            this.errStr = errStr;
            return this;
        }

        public ValidateConfiguration setErrStr(@StringRes int errStrRes) {
            if (context != null)
                this.errStr = context.getResources().getString(errStrRes);
            return this;
        }


        public ValidateConfiguration setAnimation(View animationView, Animation animation) {
            if (animationView != null) {
                this.animationView = animationView;
                if (animation == null) {
                    this.animation = new TranslateAnimation(0.0f, 15.0f, 0.0f, 0.0f);
                    this.animation.setFillAfter(false);
                    this.animation.setDuration(200);
                    this.animation.setInterpolator(new CycleInterpolator(5));
                } else this.animation = animation;

            }
            return this;
        }

        public ValidateConfiguration setValidateStr(CharSequence validateStr) {
            this.validateStr = validateStr;
            return this;
        }

        void release() {
            context = null;
            validateType = null;
            errStr = null;
            animationView = null;
            validateStr = null;
        }
    }

    public interface OnValidateFailedListener {
        public void validateFailed(CharSequence errMsg);
    }

    public enum ValidateType {

        EMAIL(10, "^([network-z0-9A-Z]+[-|_|\\\\.]?)+[network-z0-9A-Z]@([network-z0-9A-Z]+(-[network-z0-9A-Z]+)?\\\\.)+[network-zA-Z]{2,}$"),
        MOBILE(11, "^1[0-9][0-9]\\d{8}$"),
        LENGTH(12, null), EQUAL(13, null),
        IDENTITY_CARD(14, "^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}([0-9]|X|x)$"),
        PLATE_NUMBER(15, "^[\\u4e00-\\u9fa5]{1}[network-zA-Z]{1}[network-zA-Z_0-9]{5}$"),//车牌号
        NUMBER_LARGER_THAN(16, null),
        NUMBER_SMALLER_THAN(17, null);

        private int minLength;
        private CharSequence pattern;
        private int type;
        private double patternNumber;

        private ValidateType(int type, String pattern) {
            this.type = type;
            this.pattern = pattern;
        }

        public ValidateType setMinLength(int minLength) {
            this.minLength = minLength;
            return this;
        }

        public ValidateType setPattern(CharSequence pattern) {
            this.pattern = pattern;
            return this;
        }

        public ValidateType setPatternNumber(double patternNumber) {
            this.patternNumber = patternNumber;
            return this;
        }

        public int getMinLength() {
            return minLength;
        }

        public CharSequence getPattern() {
            return pattern;
        }
    }
}
