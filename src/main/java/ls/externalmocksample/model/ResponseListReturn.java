package ls.externalmocksample.model;

import java.util.List;

public class ResponseListReturn <T>{
	private List<T> data;
	private String message;
	private String status;
	
	public ResponseListReturn() {
		
	}
	
	public ResponseListReturn(List<T> data, String message, String status) {
		this.setData(data);
		this.setMessage(message);
		this.setStatus(status);
	}

	public List<T> getData() {
		return data;
	}

	public void setData(List<T> data) {
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
