// An interface to the business logic, living in the service sub-package.

package polling.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;

import polling.model.*;

public interface PollingService {

    void addModel(Model model);

    Model getModel(String modelName);

    List<Model> getAllModels();

    void deleteModel(String modelName);

    boolean verifyVoter(String voterID);

    void verifyVotes();

    String vote(String voterID, String modelName);

    String deleteVote(String voterID);

    String retrieveVote(String voterID);

    Map<String, Integer> tallyVotes();

    HashMap<String, ArrayList<String>> readVoterInterests(String voterID);

    HashMap<String, String> findMostFrequentTopic();

    void startPoll();

    void stopPoll();
}