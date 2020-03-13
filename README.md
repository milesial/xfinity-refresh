 # xfinity-refresh
 xfinity hotspots offer a complimentary 1-hour complimentary pass, restricted to one per device and per month.
 This tool automates the process of getting a pass and activating it every hour:
 - changing the MAC address
 - going to the captive portal page
 - creating an account
 - activating the pass
 
 ## Installation
 
 ```
 git clone https://github.com/milesial/xfinity-refresh
 cd xfinity-refresh
 pip install .
 ```
 
 ## Usage
 
 ```
xfinity-refresh <wifi_interface>
 ```
 Example:
 ```
xfinity-refresh wlan0
 ```

> ✔ Device wlan0 disconnected  
> ✔ Changed MAC address to 02:0C:29:10:F8:4E  
> ✔ Device wlan0 connected  
> ✔ Pass activated on Fri Mar 13 14:30:29 2020
