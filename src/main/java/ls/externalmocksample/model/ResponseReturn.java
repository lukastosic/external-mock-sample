package ls.externalmocksample.model;

public class ResponseReturn <T> {
	private T data;
	private String message;
	private String status;
	
	public ResponseReturn() {
		
	}
	
	public ResponseReturn(T data, String message, String status) {
		this.setData(data);
		this.setMessage(message);
		this.setStatus(status);
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
