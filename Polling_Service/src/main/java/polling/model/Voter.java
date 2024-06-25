// A resource, living in the model sub-package.

package polling.model;

import java.util.ArrayList;

public class Voter {

    private String voterID;
    private ArrayList <String> voterInterest;
    private String voterName;


    // Standard constructor
    public Voter(String voterID, ArrayList <String> voterInterest, String voterName) {
        this.voterID = voterID;
        this.voterInterest = voterInterest;
        this.voterName = voterName;
    }

    // Copy constructor
    public Voter(Voter that) {
    	this.voterID = that.voterID;
        this.voterInterest = that.voterInterest;
        this.voterName = that.voterName;
    }

	public String getVoterID() {
		return voterID;
	}

	public void setVoterID(String voterID) {
		this.voterID = voterID;
	}

	public ArrayList<String> getVoterInterest() {
		return voterInterest;
	}

	public void setVoterInterest(ArrayList<String> voterInterest) {
		this.voterInterest = voterInterest;
	}

	public String getVoterName() {
		return voterName;
	}

	public void setVoterName(String voterName) {
		this.voterName = voterName;
	}

	@Override
	public String toString() {
		return "Voter [voterID=" + voterID + ", voterInterest=" + voterInterest + ", voterName=" + voterName + "]";
	}

}
