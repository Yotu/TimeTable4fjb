package jp.sample.sns_sdk;

public class ReceiveException extends Exception {

	private static final long serialVersionUID = 1L;

	public ReceiveException() {
		super("データの受信に失敗しました。");
	}

	public ReceiveException(Exception e) {
		super("データの受信に失敗しました。");
		e.setStackTrace(e.getStackTrace());
	}

}
