# Microbot Multi-Account Launcher

## Simple Batch Launcher (No EternalFarm Agent - No Crashes!)

This launcher uses our ExternalLogin system to launch multiple Microbot clients **without the EternalFarm Java agent**, eliminating all crashes!

---

## Setup (One Time)

### Step 1: Edit `accounts.txt`

Add your accounts (one per line) in format: `email:password`

```
gizezufa2016@outlook.com:junubiza46
sabekitimapana1983@hotmail.com:gujiki501
account3@gmail.com:password3
account4@gmail.com:password4
... add up to 50+ accounts!
```

### Step 2: Configure `launch-all-accounts.bat`

Open the batch file and adjust these settings at the top:

```batch
set "JAVA_PATH=C:\Program Files\Eclipse Adoptium\jdk-11.0.26.4-hotspot\bin\java.exe"
set "MICROBOT_JAR=C:\Users\MiniPC10\Desktop\Microbot\runelite-client\target\client-1.11.18-SNAPSHOT.jar"
set "MEMORY=2048"
set "DELAY_SECONDS=30"
```

**Settings:**
- `JAVA_PATH` - Your Java installation
- `MICROBOT_JAR` - Path to your Microbot JAR
- `MEMORY` - RAM per client (MB)
- `DELAY_SECONDS` - Delay between launching clients

---

## Usage

### Launch All Accounts:

**Just double-click:**
```
launch-all-accounts.bat
```

**What happens:**
1. Reads all accounts from `accounts.txt`
2. Launches first client with account 1
3. Waits 30 seconds
4. Launches second client with account 2
5. Waits 30 seconds
6. Repeats until all accounts launched!

---

## Features

✅ **No Java Agent** - No crashes!  
✅ **Simple Text File** - Easy to manage accounts  
✅ **Automatic Staggering** - Prevents server spam  
✅ **Memory Control** - Set RAM per client  
✅ **No Dependencies** - Just Windows batch script  
✅ **Infinite Accounts** - Add as many as you want  
✅ **Auto-Login** - Uses ExternalLogin system  

---

## Example Output:

```
================================================
   Microbot Multi-Account Launcher
================================================

Found 50 accounts in accounts.txt

Memory per client: 2048MB
Delay between launches: 30 seconds

Starting launches...

[1/50] Launching: gizezufa2016@outlook.com
   Waiting 30 seconds before next launch...

[2/50] Launching: sabekitimapana1983@hotmail.com
   Waiting 30 seconds before next launch...

...

================================================
   All 50 clients launched!
================================================
```

---

## Advanced: With Proxies

Want to add proxy support? Edit `accounts.txt` format to:

```
email:password:proxy
```

Example:
```
account1@gmail.com:pass1:192.168.1.100:8080
account2@gmail.com:pass2:192.168.1.101:8080
```

Then update the batch script to parse the proxy field!

---

## Benefits Over EternalFarm:

✅ **No crashes** - No Java agent interference  
✅ **Simpler** - Just a text file and batch script  
✅ **Free** - No EternalFarm license needed  
✅ **Faster** - Direct launches, no middleware  
✅ **Custom** - Easy to modify for your needs  

---

## Tips

### Tip 1: Close All Clients Quickly

Press **Alt+F4** on each window, or create a `kill-all-microbot.bat`:

```batch
@echo off
taskkill /f /im java.exe
echo All Microbot clients closed!
pause
```

### Tip 2: Auto-Enable Plugins

Enable plugins **once** in Microbot:
- nICK1 Private Hunter
- Player Hop Log
- AutoLogin

Settings are saved per account and will auto-enable on next launch!

### Tip 3: Monitor Crashes

Check Windows Event Viewer if clients crash to see which account had issues.

---

## Upgrading to GUI (Future)

Want a proper GUI later? I can build:
- Account list editor
- Start/Stop buttons per account
- Status monitoring
- Built-in account creator

But for now, this simple batch launcher works perfectly! 🚀



