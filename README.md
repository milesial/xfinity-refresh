# xfinity-refresh

:warning: This program is for demonstration and educational purposes only. I do not advocate the abuse of the complimentary pass from xfinity. Use this tool at your own risk. :warning:

xfinity hotspots offer a complimentary 1-hour complimentary pass, restricted to one per device and per month.
This tool automates the process of getting a pass and activating it every hour (headless browser):
- changing the MAC address
- going to the captive portal page
- creating an account
- activating the pass

## Installation
First, download and install Chrome and [chromedriver](https://github.com/SeleniumHQ/selenium/wiki/ChromeDriver). For debian: `sudo apt install chromium-chromedriver`

Then:
```
git clone https://github.com/milesial/xfinity-refresh
cd xfinity-refresh
pip install .
```

## Usage: standalone (short ~1min interruptions every hour)

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


To run only once and not every hour, pass `--once`.

## Usage: client-server (no interruption)

For this mode you need to have two devices that each have a wifi interface and a way to communicate to each other (a raspberry pi and your laptop for example).

The server (raspberry pi) runs a process that periodically fetches and activates a pass linked to a MAC address. Once activated, the MAC address is sent to the client (laptop) via a TCP connection.
When the client's current pass will soon end and it receives an activated MAC address, the client will change the MAC address of its interface to the one it received, thus gaining internet access.

### Server
Here, `wlan0` is the interface on the server.
```
xfinity-refresh-server wlan0
```

### Client
Here, `wlan0` is the interface on the client.

```
xfinity-refresh-client wlan0 --host <ip_of_server>
```

You can specify a specific port with `--port`

### Android client

<center><img src="https://i.imgur.com/XfyqCgA.jpg" height="700px" /></center>

You can find the APK in the [release section](https://github.com/milesial/xfinity-refresh/releases).


## Will I ever run out of hardware addresses?

no. You have a 0.47% chance of having a collision during a given month if you let this tool run 24/7.
