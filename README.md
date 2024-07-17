# YouTube Server

## Run the server
1. Clone the repository to your computer.
2. Open terminal in the cloned folder.
3. Run __npm install__ to install dependencies.

## Create a config folder
1. Create a new folder in the repository named __config__.
2. Inside the __config__ folder create the file .env.
3. Add the following content to the .env file:
  ```bash
  CONNECTION_STRING="{your_mongoDB_connection_string}/YouTube_101"
  PORT={port_number}
  ```
For example:
  ```bash
  CONNECTION_STRING="mongodb://localhost:27017/YouTube_101"
  PORT=8200
  ```

* Open MongoDB on your computer.
* __Note__ that a JavaScript script will automatically run and initialize a __YouTube_101__ database when you run the server. Please make sure you don't have a DB with that called __YouTube_101__.
* Run the server using __npm start__ (if you're using unix, run __npm run startUnix__ instead).

## Run the Android Application

1. Open the Android project in Android Studio.
2. Ensure your Android device is connected, or start an emulator.
3. Run the project by clicking the Run button in Android Studio.

## Configuring the Base URL

The project is currently configured to run on port 8200. If you choose a different port, you need to update the BaseUrl in the Android project:

1. Go to `res/values/strings.xml`.
2. Update the BaseUrl string to the new port:
   ```xml
   <string name="BaseUrl">http://10.0.2.2:{new_port_number}</string>
For example:
  ```bash
<string name="BaseUrl">http://10.0.2.2:8200</string>
  ```
### Running on a Physical Device

If you want to run the application on a physical device, change the IP address to your computer's IP address:

1. Connect your phone to the same WiFi network as your computer.
2. Update the BaseUrl in `res/values/strings.xml`:
   ```xml
   <string name="BaseUrl">http://{your_computer_ip}:{port_number}</string>
   For example:
  ```bash
<string name="BaseUrl">http://192.168.1.10:8200</string>
  ```

## Android Integration

### Initial Android Setup
At the start, we focused on connecting the Android application with ViewModel, Repository, API, Web Service, and Room. This setup ensured a robust architecture for data handling and persistence.

### Work Distribution
Subsequently, we divided the tasks. Each team member worked on modifying the functionality in the Android application to fetch the required data from the server using API commands.

### Specific Tasks
We divided the tasks into separate components, focusing on:

* __User-related screens__: Editing, registration, and login.
* __Video-related functionalities__: Editing, viewing, adding likes and views.
* __Comments functionalities__: Adding, editing, and viewing comments on videos.
* __Integrating these components__: Ensuring seamless functionality across the application by integrating the user, video, and comment components.
* __Additional components__: Created screens for the user page and trending videos to enhance the user interface and user experience.

### Multer Integration
We also integrated Multer for handling file uploads in both the server and Android application, enabling smooth video and image uploads.


## Enjoy!



