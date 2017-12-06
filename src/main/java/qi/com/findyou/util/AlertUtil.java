package qi.com.findyou.util;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


/**
 * Created by k on 16/7/5.
 */
public class AlertUtil {
	public static void showToastCenter(Context ctx, String str) {
		Toast toast=Toast.makeText(ctx, str, Toast.LENGTH_LONG);
		toast.setGravity(0,Gravity.CENTER,Gravity.CENTER);
		toast.show();
	}
	public static void showToastLong(Context ctx, String str) {
		Toast.makeText(ctx, str, Toast.LENGTH_LONG).show();
	}

	public static void showToastShort(Context ctx, String str) {
		Toast.makeText(ctx, str, Toast.LENGTH_SHORT).show();
	}

	private static AlertDialog.Builder builder;
	private static BuilderCancelLister builderCancelLister;
	public static void createAlertDialog(Context context,String title, String msg, DialogInterface.OnClickListener okListener, DialogInterface.OnClickListener noListener) {
		TextView tvTitle = new TextView(context);
		tvTitle.setText(title);
		tvTitle.setTextColor(Color.WHITE);
		tvTitle.setPadding(18, 10, 18, 0);
		tvTitle.setGravity(Gravity.LEFT);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
			tvTitle.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);
		}
		tvTitle.setTextSize(20);

		TextView tvMsg = new TextView(context);
		tvMsg.setText(Html.fromHtml(msg));
		tvMsg.setTextColor(Color.WHITE);
		tvMsg.setPadding(24, 8, 24, 0);
		tvMsg.setGravity(Gravity.LEFT);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
			tvMsg.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);
		}
		tvMsg.setTextSize(16);

		builder = new AlertDialog.Builder(context);
		builder.setCustomTitle(tvTitle);
		builder.setView(tvMsg);
		builder.setCancelable(true);
		builder.setPositiveButton(android.R.string.yes, okListener);
		builder.setNegativeButton(android.R.string.no, noListener);
		AlertDialog dialog = builder.create();
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
	}
	public interface BuilderCancelLister {

		//处理出现dialog时点击返回键的处理
		void onCancel();
	}
}