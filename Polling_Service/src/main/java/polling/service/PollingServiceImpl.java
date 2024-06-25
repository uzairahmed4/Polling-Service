// Implementation of the business logic, living in the service sub-package.
// Discoverable for auto-configuration, thanks to the @Component annotation.

package polling.service;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.io.BufferedReader;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import polling.model.*;

@Component
public class PollingServiceImpl implements PollingService {

	// Very simple in-memory database; key is the lang field of class Welcome.
	// We have to be careful with this 'database'. In order to avoid objects
	// in the database being mutated accidentally, we must always copy objects
	// before insertion and retrieval.

	// stores model name and the model object
	private Map<String, Model> modelDB;

	private HashMap<String, ArrayList<String>> interestsDB;

	// stores voterID with the model the voter voted for

	private Map<String, String> pollDB;

	private Boolean startPoll = true;

	public PollingServiceImpl() {
		modelDB = new HashMap<>();
		pollDB = new HashMap<>();
		interestsDB = new HashMap<>();
	}

	public void addModel(Model model) {
		if (startPoll == true) {
			if (model != null && model.getModelName().length() != 0) {
				// copying welcome to isolate objects in the database from changes
				model = new Model(model);
				modelDB.put(model.getModelName(), model);
				System.out.println("Model has been added " + model);

			}
		} else {
			System.out.println("Failed to delete vote. Please try again when the poll is open.");
		}
	}

	public void deleteModel(String model) {
		if (startPoll == true) {
			if (modelDB.containsKey(model)) {
				modelDB.remove(model);
				System.out.println("Model has been deleted");
			} else {
				System.out.println("Failed to delete model. Please try again when the poll is open.");
			}
		}
	}

	public Model getModel(String modelName) {
		Model model = modelDB.get(modelName);
		if (model == null) {
			return null;
		}
		return model;
	}

	public List<Model> getAllModels() {
		return new ArrayList<>(modelDB.values());
	}

	public boolean verifyVoter(String voterID) {
		String url = "https://pmaier.eu.pythonanywhere.com/vps/voter/" + voterID;

		try {
			// Create URL object
			URL obj = new URL(url);

			// Open connection
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();

			// Set request method
			con.setRequestMethod("GET");

			// Get response code
			int responseCode = con.getResponseCode();

			// Print response code
			System.out.println("Response Code: " + responseCode);

			// Check if response code is 200 (OK)
			if (responseCode == HttpURLConnection.HTTP_OK) {
				System.out.println("Connection is successful");
				return true;
			} else {
				System.out.println("Connection failed: " + responseCode);
				return false;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public void verifyVotes() {
		for (Map.Entry<String, String> entry : pollDB.entrySet()) {
			String key = entry.getKey();
			if (verifyVoter(key) == false) {
				pollDB.remove(key);
			}
			System.out.println("Votes Verified");
		}
	}

	// this method adds a new vote, as well as updates any previous votes casted by
	// the same user
	public String vote(String voterID, String modelName) {
		if (startPoll == true) {
			if (verifyVoter(voterID)) {
				pollDB.put(voterID, modelName);
				for (Map.Entry<String, String> entry : pollDB.entrySet()) {
					System.out.println(entry.getKey() + " : " + entry.getValue());
				}
				readVoterInterests(voterID);
				return "Vote has been cast";
			}
		}
		return null;
	}

	public String deleteVote(String voterID) {
		if (startPoll == true) {
			if (pollDB.containsKey(voterID)) {
				pollDB.remove(voterID);
				interestsDB.remove(voterID);
				return "Vote has been deleted";
			}
		}
		return "Failed to delete vote. Please try again when the poll is open.";
	}

	public String retrieveVote(String voterID) {
		String modelName = pollDB.get(voterID);
		if (pollDB.containsKey(voterID)) {
			if (modelName != null) {
				return modelName;
			}
		}
		return null;
	}

	public Map<String, Integer> tallyVotes() {
		verifyVotes();
		Map<String, Integer> voteCounts = new HashMap<>();
		// Initialize the vote counts for each model to 0
		for (String modelName : modelDB.keySet()) {
			voteCounts.put(modelName, 0);
		}

		// Iterate over the entries in the pollDB map
		for (String modelName : pollDB.values()) {
			// Increment the count for the corresponding model
			voteCounts.put(modelName, voteCounts.getOrDefault(modelName, 0) + 1);
		}
		System.out.println("Votes tallied");

		findMostFrequentTopic();
		return voteCounts;
	}

	public HashMap<String, ArrayList<String>> readVoterInterests(String voterID) {
		String url = "https://pmaier.eu.pythonanywhere.com/vps/voter/" + voterID;

		try {
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("GET");
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			// Parsing JSON response
			String json = response.toString();
			// Assuming the JSON structure is as described
			JSONObject jsonObject = new JSONObject(json);

			// Check if the JSON contains "voter" object
			if (jsonObject.has("voter")) {
				JSONObject voterObject = jsonObject.getJSONObject("voter");
				// Check if the "voter" object contains "interests" array
				if (voterObject.has("interests")) {
					JSONArray interestsArray = voterObject.getJSONArray("interests");
					ArrayList<String> interests = new ArrayList<>();
					for (int i = 0; i < interestsArray.length(); i++) {
						interests.add(interestsArray.getString(i));
					}
					interestsDB.put(voterID, interests);
					System.out.println("interestsDB: " + interestsDB);
				} else {
					System.out.println("No interests found for voter: " + voterID);
				}
			} else {
				System.out.println("No voter information found for voter: " + voterID);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return interestsDB;
	}

	public HashMap<String, String> findMostFrequentTopic() {
		HashMap<String, String> modelTopicMap = new HashMap<>();

		// Iterate through each model in modelDB
		for (String model : modelDB.keySet()) {
			HashMap<String, Integer> topicCount = new HashMap<>();

			// Iterate through pollDB to find voters who voted for the current model
			for (Map.Entry<String, String> entry : pollDB.entrySet()) {
				String voterID = entry.getKey();
				String votedModel = entry.getValue();

				if (votedModel.equals(model)) {
					// Find interests of the voter in interestsDB
					ArrayList<String> interests = interestsDB.get(voterID);
					if (interests != null) {
						// Update the count for each interest
						for (String interest : interests) {
							topicCount.put(interest, topicCount.getOrDefault(interest, 0) + 1);
						}
					}
				}
			}

			// Find the most frequent interest for the current model
			String mostFrequentInterest = "";
			int maxCount = 0;
			for (Map.Entry<String, Integer> entry : topicCount.entrySet()) {
				if (entry.getValue() > maxCount) {
					maxCount = entry.getValue();
					mostFrequentInterest = entry.getKey();
				}
			}

			modelTopicMap.put(model, mostFrequentInterest);
		}

		// Print the contents of modelTopicMap
		for (Map.Entry<String, String> entry : modelTopicMap.entrySet()) {
			System.out.println("Model: " + entry.getKey() + ", Most Frequent Interest: " + entry.getValue());
		}

		return modelTopicMap;
	}

	public void startPoll() {
		System.out.println("Poll opened");
		startPoll = true;
	}

	public void stopPoll() {
		startPoll = false;
		System.out.println("Poll closed");
		verifyVotes();
	}

}