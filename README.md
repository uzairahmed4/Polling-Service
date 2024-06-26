# Polling-Service-Application

This project implements a web service for polling the popularity of generative AI tools using RESTful web services with Java Spring Boot. It includes clients that interact with the web service to facilitate voting and administration functionalities.

## Overview
The web service allows voters to cast their votes for different AI models anonymously. Administrators can manage candidate models, validate voters, and tally votes securely. The application is designed following REST principles to ensure scalability, reliability, and security.

## Functionality
### Voter Functionality:
- View available AI models.
- Vote for a preferred model.
- Retract a vote.
- Retrieve the model voted for.
- Admin Functionality:

### Tally votes for each model.
- Analyze voting behavior by identifying popular topics among voters.
- Validate voters against a voter profile service.
- Additional functionalities like adding/removing models and managing poll status.
