package qi.com.findyou.base;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import qi.com.findyou.util.Md5Util;
import qi.com.findyou.util.SpUtil;


/**
 * 账号管理器类
 * Created by k on 16/1/12.
 */
public class AccountManager {
    private static String KEY_USER = "find_you";
    private static String KEY_STATE = "false";
    private static String KEY_ACCOUNT = "account";

    private static SpUtil spUtil = SpUtil.getInstance();

//    public static void save(User user) throws IOException {
//        if (user == null) {
//            return;
//        }
//        String userStr = user.toString();
//        spUtil.putString(KEY_USER, userStr);
//        spUtil.putString(KEY_ACCOUNT, user.getAccount());
//    }
//
//    //更新用户信息
//    public static void update(User user) throws IOException {
//        if (user == null) {
//            return;
//        }
//
//        String userStr = user.toString();
//
//        spUtil.putString(KEY_USER, userStr);
//        spUtil.putString(KEY_ACCOUNT,user.getAccount());
//    }

    //是否已登录
    public static boolean isLogin() {
        String state = spUtil.getString(KEY_STATE);
        if (state==null || state.equals("false")) {
            return false;
        }
        return true;
    }

    //设置登录状态
    public static void setState(boolean isLogin) {
        spUtil.putString(KEY_STATE, String.valueOf(isLogin));
    }

    public static String generateSignature(String token, long timestamp) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        StringBuffer sb = new StringBuffer(token);
        String timeStr = Long.toString(timestamp, 16);

        sb.insert(5, timeStr.substring(0, 5));

        sb.insert(19, timeStr.substring(5, timeStr.length()));

        return Md5Util.encrypt(sb.toString());
    }
    public static void clearAccount() {
        spUtil.removeString(KEY_STATE);
        spUtil.removeString(KEY_USER);
    }

    public static String getAccount() {
        String account = spUtil.getString(KEY_ACCOUNT);
        return account;
    }

//    public static String getName() {
//        User user = getUser();
//        if (user == null) {
//            return null;
//        }
//        return user.getName();
//    }
//
//    public static User getUser() {
//        String userStr = spUtil.getString(KEY_USER);
//        User user = User.fromString(userStr);
//        return user;
//
//    }
//
//
//    public static String getToken() {
//
//        User user = getUser();
//        if (user == null) {
//            return null;
//        }
//        return user.getToken();
//    }

}
