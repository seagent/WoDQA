package tr.edu.ege.seagent.wodqa.voiddocument;

import java.util.List;

import tr.edu.ege.seagent.triplestoremanager.DBConnectionDescription;
import tr.edu.ege.seagent.triplestoremanager.SDBHandler;
import tr.edu.ege.seagent.wodqa.exception.VOIDStoreCreationException;

import com.hp.hpl.jena.ontology.OntModel;

public class VOIDRegisterer {

	private List<OntModel> allVOIDs;
	private VOIDStoreFactory voidStore;

	/**
	 * Creates a registerer and create just one handler with the given database
	 * name. If database is exist it returns only created handler instance.
	 * 
	 * @param voidStoreDbName
	 */
	public VOIDRegisterer(String voidStoreDbName) {
		this(voidStoreDbName, null);
	}

	/**
	 * Creates a registerer and create just one handler with the given database
	 * name. It uses the database which configuration has been given.
	 * 
	 * @param voidStoreDbName
	 */
	public VOIDRegisterer(String voidStoreDbName, DBConnectionDescription desc) {
		try {
			this.voidStore = new VOIDStoreFactory(voidStoreDbName, desc);
		} catch (VOIDStoreCreationException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * It returns created sdb handler instance if it exists otherwise it throws
	 * an exception.
	 * 
	 * @return
	 * @throws VOIDStoreCreationException
	 */
	public SDBHandler getHandler() throws VOIDStoreCreationException {
		return voidStore.getVOIDStoreHandler();
	}

	/**
	 * Returns the created void store db name.
	 * 
	 * @return
	 * @throws VOIDStoreCreationException
	 */
	public String getDbName() throws VOIDStoreCreationException {
		return voidStore.getDbName();
	}

	public void dispose() {
		voidStore.disposeVOIDStoreHandler();
	}

	/**
	 * Register the given void model to the store.
	 * 
	 * @param voidModel
	 * @param ontURI
	 * @throws VOIDStoreCreationException
	 */
	public void registerVOID(OntModel voidModel, String ontURI)
			throws VOIDStoreCreationException {
		VOIDEntityOperationUtils.getInstance(getHandler())
				.saveVOIDModelWithHandler(voidModel, this, ontURI);
	}

	/**
	 * Retrieves the all void models from the void store.
	 * 
	 * @return
	 * @throws VOIDStoreCreationException
	 */
	public List<OntModel> getAllVOIDs() throws VOIDStoreCreationException {
		return this.allVOIDs;
	}

	public void retrieveAllVOIDs() throws VOIDStoreCreationException {
		setAllVOIDs(VOIDEntityOperationUtils.getInstance(getHandler())
				.getVOIDModels(this));
	}

	private void setAllVOIDs(List<OntModel> voidModels) {
		this.allVOIDs = voidModels;
	}
}
