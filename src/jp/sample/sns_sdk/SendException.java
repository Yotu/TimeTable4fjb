package jp.sample.sns_sdk;

public class SendException extends Exception {
	private static final long serialVersionUID = 1L;

	public SendException() {
		super("データの送信に失敗しました。");
	}

	public SendException(String description) {
		super("データの送信に失敗しました。:" + description);
	}

	public SendException(Exception e) {
		super("データの送信に失敗しました。");
		e.setStackTrace(e.getStackTrace());
	}

}
