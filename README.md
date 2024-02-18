# GHRepo



## Description

Recruitment task.
REST API application that retrieves a user's repositories and their branches, returning only those that are not forks.


## Build

Needs Java 21 and Gradle 8.5
To build clone and run
```bash
./gradlew build
```
 ## Usage
 
 Program needs to get request with appropriate header `application/json` to enpoint `/{username}`
 For example with curl
 ```bash
 curl -H "Accept: application/json" http://127.0.0.1:8080/materusPL
 ```

 Program will return list of repositories and their branches in format:
 ```json
 [{ 
    "name":"", //Repository name
    "owner":"", //Owner name
    "branches": [{"name":"", "sha":"" }] //List of branches with their name and last commit sha
 }]
 ```
 On error it will return json in format:
 ```json
 {
    "message":"",
    "status":""
 }
 ```
