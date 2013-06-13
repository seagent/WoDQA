package tr.edu.ege.seagent.wodqa.query.analyzer;

public class EndpointAndType {
	private String endpoint;
	private boolean type;

	public EndpointAndType(String endpoint, boolean type) {
		this.endpoint = endpoint;
		this.type = type;
	}

	public String getEndpoint() {
		return endpoint;
	}

	public boolean isType() {
		return type;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	public void setType(boolean type) {
		this.type = type;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EndpointAndType other = (EndpointAndType) obj;
		if (endpoint == null) {
			if (other.endpoint != null)
				return false;
		} else if (!endpoint.equals(other.endpoint))
			return false;
		if (type != other.type)
			return false;
		return true;
	}

}
