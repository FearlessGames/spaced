package se.spaced.server.services.webservices.external;

public class BroadcastResultDTO {
	private boolean success;
	private String message;

	public BroadcastResultDTO() {
	}

	public static BroadcastResultDTO fail(String message) {
		BroadcastResultDTO fail = new BroadcastResultDTO();
		fail.setSuccess(false);
		fail.setMessage(message);
		return fail;
	}

	public static BroadcastResultDTO success() {
		BroadcastResultDTO fail = new BroadcastResultDTO();
		fail.setSuccess(true);
		fail.setMessage("");
		return fail;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
