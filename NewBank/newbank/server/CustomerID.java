package newbank.server;

public class CustomerID {
	private String key;
	
	public CustomerID(String key) {
		this.key = key;
	}
	
	public String getKey() {
		return key;
	}

	@Override
	public String toString() {
		return "CustomerID [key=" + key + ", getKey()=" + getKey() + ", getClass()=" + getClass() + ", hashCode()="
				+ hashCode() + ", toString()=" + super.toString() + "]";
	}
	
	
	
	
	
}
