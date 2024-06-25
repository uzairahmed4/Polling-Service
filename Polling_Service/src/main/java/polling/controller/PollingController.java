package polling.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import polling.model.Model;
import polling.service.PollingService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
public class PollingController {

    private final PollingService ps;

    public PollingController(PollingService ps) {
        this.ps = ps;
    }

    @GetMapping("/models")
    public ResponseEntity<List<Model>> getAllModels() {
        List<Model> models = ps.getAllModels();
        if (models.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(models);
    }

    @GetMapping("/retrieve/{voterID}")
    public ResponseEntity<String> retrieve(@PathVariable String voterID) {
        String voteMessage = ps.retrieveVote(voterID);
        if (voteMessage != null) {
            return ResponseEntity.ok(voterID + " voted for " + voteMessage);
        }
        return ResponseEntity.badRequest().body("No vote placed for this ID.");
    }

    @PostMapping("/vote/{voterID}/{modelName}")
    public ResponseEntity<?> createVote(@PathVariable String voterID, @PathVariable String modelName) {
        if (ps.retrieveVote(voterID) == null) {
            String voteMessage = ps.vote(voterID, modelName);
            if (voteMessage != null) {
                return ResponseEntity.status(HttpStatus.CREATED).body(voteMessage + " for " + modelName);
            } else {
                return ResponseEntity.badRequest()
                        .body("Failed to create vote. Please try again when the poll is open.");
            }
        } else {
            ps.deleteVote(voterID);
            String voteMessage = ps.vote(voterID, modelName);
            if (voteMessage != null) {
                return ResponseEntity.status(HttpStatus.CREATED).body("Vote has been updated to " + modelName);
            } else {
                return ResponseEntity.badRequest()
                        .body("Failed to create vote. Please try again when the poll is open.");
            }
        }
    }

    @DeleteMapping("/delete/{voterID}")
    public ResponseEntity<String> deleteVote(@PathVariable String voterID) {
        if (ps.retrieveVote(voterID) != null) {
            String deleteMessage = ps.deleteVote(voterID);
            return ResponseEntity.ok(deleteMessage);
        }
        return ResponseEntity.badRequest().body("No vote placed for this ID.");
    }

    @PostMapping("/addModel/{modelName}/{purpose}")
    public ResponseEntity<String> addModel(@PathVariable String modelName, @PathVariable String purpose) {
        // Find the largest key value
        int maxKey = 0;

        // Iterate over the values of modelDB to find the largest modelID
        for (Model model : ps.getAllModels()) {
            if (model.getModelID() > maxKey) {
                maxKey = model.getModelID();
            }
        }

        // Increment the largest modelID by 1 for the new model
        int newKey = maxKey + 1;

        // Create and add the new model
        ps.addModel(new Model(newKey, modelName, purpose));

        return ResponseEntity.ok().body("Model added: " + modelName);
    }

    @DeleteMapping("/deleteModel/{modelName}")
    public ResponseEntity<String> deleteModel(@PathVariable String modelName) {
        if (ps.getModel(modelName) != null) { // Check if the model exists
            ps.deleteModel(modelName); // Remove the model
            return ResponseEntity.ok("Model deleted: " + modelName);
        } else {
            return ResponseEntity.badRequest().body("Model not found: " + modelName);
        }
    }

    @GetMapping("/verifyVotes")
    public ResponseEntity<String> verifyVotes() {
        ps.verifyVotes();
        return ResponseEntity.ok("Votes verified.");
    }

    @GetMapping("/tallyVotes")
    public ResponseEntity<Map<String, Integer>> getVoteTally() {
        Map<String, Integer> voteCounts = ps.tallyVotes();
        if (voteCounts.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(voteCounts);
    }

    @GetMapping("/tallyInterests")
    public ResponseEntity<Map<String, String>> getInterestTally() {
        Map<String, String> interestCount = ps.findMostFrequentTopic();
        if (interestCount.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(interestCount);
    }

    @PostMapping("/startPoll")
    public ResponseEntity<String> startPoll() {
        ps.startPoll();
        return ResponseEntity.ok("Poll started.");
    }

    @PostMapping("/stopPoll")
    public ResponseEntity<String> stopPoll() {
        ps.stopPoll();
        return ResponseEntity.ok("Poll stopped.");
    }
}