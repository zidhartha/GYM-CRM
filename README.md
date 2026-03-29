Security
Based on the codebase created during the previous module, implement follow functionality:
Add Spring Security module to your project and configurate it for Authentication access for all endpoints (except Create Trainer/Trainee profile).
Use Username/Password combination.
Use salt and hashing to store user passwords in DB.
Configure Spring Security to use Login functionality.
Add Brute Force protector. Block user for 5 minutes on 3 unsuccessful logins
Implement Logout functionality and configure it in Spring Security.
Implement Authorization − Bearer token for Create Profile and Login functionality Use JWT token implementation.
Configure CORS policy in Spring Security.
Notes:
During Create Trainer/Trainee profile username and password should be generated as described in previous module.
All functions except Create Trainer/Trainee profile. Should be executed only after Trainee/Trainer authentication.