# Android App

## Run the app

### Run the relevant beckend servers

1. Make sure your Node js server is up and running (see [here](https://github.com/maayanzahavi/WebPart2.git) branch `mainPart4`).
2. Make sure your C++ server is up and running (see [here](https://github.com/maayanzahavi/YouTubeCppServer.git) branch `main`).

### Run the Android Application

1. Clone the relevant repository to Android Studio
2. Ensure your Android device is connected, or start an emulator.
3. Configure the Base URL (detailed below).
4. Run the project by clicking the run button in Android Studio.

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
## Run on a physical device

If you want to run the application on a physical device, change the IP address to your computer's IP address:

1. Connect your phone to the same WiFi network as your computer.
2. Update the BaseUrl in `res/values/strings.xml`:
   ```xml
   <string name="BaseUrl">http://{your_computer_ip}:{port_number}</string>
For example:

  ```bash
<string name="BaseUrl">http://192.168.1.10:8200</string>
  ```
