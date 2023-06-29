# 3ALevels

CIao
## Authors


| **Name and Surname** | **Linkedin** | **GitHub** |
|:--------------------:| :---: | :---: |
|  `Antonio Grieco `   | [![name](https://github.com/nardoz-dev/projectName/blob/main/docs/sharedpictures/LogoIn.png)](https://www.linkedin.com/in/AntonioGrieco96) | [![name](https://github.com/nardoz-dev/projectName/blob/main/docs/sharedpictures/GitHubLogo.png)](https://github.com/AGrieco96) |
| `Andrea Di Tommaso ` | [![name](https://github.com/nardoz-dev/projectName/blob/main/docs/sharedpictures/LogoIn.png)](https://www.linkedin.com/in/andrea-ditommaso) | [![name](https://github.com/nardoz-dev/projectName/blob/main/docs/sharedpictures/GitHubLogo.png)](https://github.com/Ditommy2) |
|  `Andrea Nardocci `  | [![name](https://github.com/nardoz-dev/projectName/blob/main/docs/sharedpictures/LogoIn.png)](https://www.linkedin.com/in/andrea-nardocci) | [![name](https://github.com/nardoz-dev/projectName/blob/main/docs/sharedpictures/GitHubLogo.png)](https://github.com/nardoz-dev) |


## How to use Firebase and how to Import.

### Access : Access a Cloud Firestore instance from your Activity
```
val db = Firebase.firestore
```
### Adding Data : 
```
// Create a new user with a first and last name
 val user = hashMapOf(
 "first" to "Ada",
 "last" to "Lovelace",
 "born" to 1815
 )
 // Add a new document with a generated ID
db.collection("users")
.add(user)
.addOnSuccessListener { 
    documentReference -> Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
}
.addOnFailureListener { 
    e -> Log.w(TAG, "Error adding document", e)
}
 ```
### Reading Data:
``` 
 db.collection("users")
.get()
.addOnSuccessListener { 
    result -> for (document in result) {
        Log.d(TAG, "${document.id} => ${document.data}")
    }
}
.addOnFailureListener { 
    exception -> Log.w(TAG, "Error getting documents.", exception)
}
```

### Protect your data : 
### AuthenticationMode :
```
// Allow read/write access on all documents to any user signed in to the application
service cloud.firestore {
    match /databases/{database}/documents {
        match /{document=**} {
            allow read, write: if request.auth != null;
        }
    }
}
```
### BlockedMode :
```
// Deny read/write access to all users under any conditions
service cloud.firestore {
    match /databases/{database}/documents {
        match /{document=**} {
            allow read, write: if false;
        }
    }
}
```
### TestMode : 
```
// Allow read/write access to all users under any conditions
// Warning: **NEVER** use this rule set in production; it allows
// anyone to overwrite your entire database.
service cloud.firestore {
    match /databases/{database}/documents {
        match /{document=**} {
            allow read, write: if true;
        }
    }
}
```
### Link Utili 
[VideoTutorial](https://www.youtube.com/watch?v=kDZYIhNkQoM)

[CodeLabTutorialFirebase](https://firebase.google.com/codelabs/firestore-android?hl=it#5)
