package tr.edu.ege.seagent.wodqa.query.analyzer;

import java.util.List;
import java.util.Vector;

public class VOIDPathSolution {

	private List<EndpointAndType> endpointTypeList;

	public VOIDPathSolution() {
		endpointTypeList = new Vector<EndpointAndType>();
	}

	public List<EndpointAndType> getEndpointTypeList() {
		return endpointTypeList;
	}

	public void addEndpoint(String endpoint) {
		endpointTypeList.add(new EndpointAndType(endpoint, false));
	}

	public void addEndpoint(String endpoint, boolean state) {
		endpointTypeList.add(new EndpointAndType(endpoint, state));
	}

	/**
	 * Chang
	 * 
	 * @param index
	 * @param externalState
	 */
	public void setExternalState(int index, boolean externalState) {
		endpointTypeList.get(index).setType(externalState);
	}

	/**
	 * It retrieves the external state of the given service endpoint.
	 * 
	 * @param service
	 * @throws Exception
	 */
	public boolean getExternalStateOfEndpoint(String service) throws Exception {
		for (EndpointAndType enpt : endpointTypeList) {
			if (enpt.getEndpoint().equals(service))
				return enpt.isType();
		}
		throw new Exception("There is no service in service list!");
	}

	/**
	 * It checks the all elements of given list with the endpoints of this
	 * object.
	 * 
	 * @param currentEndpoints
	 * @return
	 */
	public boolean checkEndpointsEquality(List<String> currentEndpoints) {
		getAllEndpoints(true);
		if (getAllEndpoints().size() != currentEndpoints.size())
			return false;
		for (String string : currentEndpoints) {
			if (!getAllEndpoints().contains(string))
				return false;
		}
		return true;
	}

	private List<String> allEndpoints;
	private List<Boolean> allExternalStates;

	public List<String> getAllEndpoints() {
		if (allEndpoints == null) {
			allEndpoints = new Vector<String>();
			for (EndpointAndType ent : endpointTypeList) {
				if (ent.getEndpoint() != null && ent.getEndpoint() != "") {
					allEndpoints.add(ent.getEndpoint());
				}
			}
		}
		return allEndpoints;
	}

	public List<String> getAllEndpoints(boolean clear) {
		if (clear)
			allEndpoints = null;
		return getAllEndpoints();
	}

	public List<Boolean> getAllStates() {
		if (allExternalStates == null) {
			allExternalStates = new Vector<Boolean>();
			for (EndpointAndType ent : endpointTypeList) {
				allExternalStates.add(ent.isType());
			}
		}
		return allExternalStates;
	}

	public boolean isAllInternal() {
		for (Boolean isExternal : getAllStates()) {
			if (isExternal)
				return false;
		}
		return true;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		VOIDPathSolution other = (VOIDPathSolution) obj;
		if (endpointTypeList == null) {
			if (other.endpointTypeList != null)
				return false;
			else
				return true;
		}
		if (endpointTypeList.size() != other.getEndpointTypeList().size())
			return false;
		else {
			for (EndpointAndType endpointType : other.getEndpointTypeList()) {
				if (!endpointTypeList.contains(endpointType)) {
					return false;
				}
			}
		}
		return true;
	}

	public boolean isEmpty() {
		return endpointTypeList.isEmpty();
	}

}
