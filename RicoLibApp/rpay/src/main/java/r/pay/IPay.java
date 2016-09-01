package r.pay;

import android.content.Context;

public abstract class IPay {

	protected OnPayListener onPayListener;

	public void setOnPayListener(OnPayListener onPayListener) {
		this.onPayListener = onPayListener;
	}

	public abstract void pay(Context context, IPayEntity entity);


	public interface OnPayListener{
		public void onPay(RPayResult result);
	}

	public static class RPayResult {
		public boolean success;
		public String message;

		public RPayResult(boolean success, String message) {
			this.success = success;
			this.message = message;
		}

		public RPayResult() {
		}
	}
}
