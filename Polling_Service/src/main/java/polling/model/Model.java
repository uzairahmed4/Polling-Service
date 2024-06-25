package polling.model;

public class Model {

	private Integer modelID;
    private String modelName;
    private String modelPurpose;


    // Standard constructor
	public Model() {
    }

    public Model(Integer modelID, String modelName, String modelPurpose) {
        this.modelID = modelID;
        this.modelName = modelName;
        this.modelPurpose = modelPurpose;
    }
    
    // Copy constructor
    public Model(Model that) {
        this.modelID = that.modelID;
        this.modelName = that.modelName;
        this.modelPurpose = that.modelPurpose;
    }

	public Integer getModelID() {
		return modelID;
	}

	public void setModelID(Integer modelID) {
		this.modelID = modelID;
	}
	public String getModelName() {
		return modelName;
	}

	public void setModelName(String modelName) {
		this.modelName = modelName;
	}

	public String getModelPurpose() {
		return modelPurpose;
	}

	public void setModelPurpose(String modelPurpose) {
		this.modelPurpose = modelPurpose;
	}

	@Override
	public String toString() {
		return "Model [modelID=" + modelID + ", modelName=" + modelName + ", modelPurpose=" + modelPurpose + "]";
	}
 
}