# EternalFarm Integration Guide for Microbot

## Overview

This guide explains how to configure EternalFarm to work with Microbot for managing 50+ accounts efficiently.

## How It Works

Microbot now supports external credential injection via:
1. **System Properties** (JVM arguments)
2. **Environment Variables**
3. **Programmatic API**

EternalFarm can pass account credentials when launching each Microbot client instance.

---

## EternalFarm Configuration

### Method 1: JVM Arguments (Recommended)

Add these JVM arguments in EternalFarm's client configuration:

```
-Dmicrobot.username=your_email@example.com
-Dmicrobot.password=your_password
-Dmicrobot.proxy=ip:port:user:pass
-Dmicrobot.world=360
-Dmicrobot.members=true
```

### Full Example JVM Arguments:
```
-Dmicrobot.username=bot001@gmail.com
-Dmicrobot.password=SecurePass123
-Dmicrobot.proxy=192.168.1.100:8080:proxyuser:proxypass
-Dmicrobot.world=360
-Dmicrobot.members=true
```

---

## EternalFarm Settings

### Client Settings in EternalFarm:

1. **Client Type**: Select "Microbot" or "Custom Client"
2. **Client Path**: Point to your Microbot JAR file
3. **JVM Arguments**: Add the `-Dmicrobot.*` arguments above
4. **Profile Variables**: Use EternalFarm's built-in variables:

```
-Dmicrobot.username=%USERNAME%
-Dmicrobot.password=%PASSWORD%
-Dmicrobot.proxy=%PROXY%
-Dmicrobot.world=%WORLD%
-Dmicrobot.members=true
```

EternalFarm will automatically replace these variables for each account!

---

## Parameter Reference

| Parameter | Required | Description | Example |
|-----------|----------|-------------|---------|
| `microbot.username` | ✅ Yes | OSRS email/username | `bot001@gmail.com` |
| `microbot.password` | ✅ Yes | OSRS password | `SecurePass123` |
| `microbot.proxy` | ❌ No | Proxy (ip:port:user:pass) | `1.2.3.4:8080:user:pass` |
| `microbot.world` | ❌ No | World number | `360` |
| `microbot.members` | ❌ No | Members-only worlds | `true` or `false` |

---

## Environment Variables (Alternative)

If EternalFarm doesn't support JVM arguments well, use environment variables:

```bash
export MICROBOT_USERNAME=bot001@gmail.com
export MICROBOT_PASSWORD=SecurePass123
export MICROBOT_PROXY=192.168.1.100:8080:proxyuser:proxypass
export MICROBOT_WORLD=360
export MICROBOT_MEMBERS=true
```

---

## Proxy Format

The proxy parameter supports two formats:

**With Authentication:**
```
ip:port:username:password
Example: 192.168.1.100:8080:proxyuser:proxypass
```

**Without Authentication:**
```
ip:port
Example: 192.168.1.100:8080
```

---

## EternalFarm Account CSV Format

If EternalFarm uses CSV files for account management, format it like this:

```csv
username,password,proxy,world,members
bot001@gmail.com,SecurePass123,192.168.1.100:8080:user:pass,360,true
bot002@gmail.com,SecurePass456,192.168.1.101:8080:user:pass,361,true
bot003@gmail.com,SecurePass789,192.168.1.102:8080:user:pass,362,true
```

---

## Testing

### Test Single Account:

1. **Launch Microbot manually** with test credentials:
```bash
java -jar microbot.jar -Dmicrobot.username=test@gmail.com -Dmicrobot.password=testpass
```

2. **Check console output** for:
```
External credentials detected:
  Username: test@gmail.com
  Password: ***SET***
  Proxy: NOT SET
  World: DEFAULT
  Members: AUTO-DETECT
```

3. **Verify auto-login** works when you reach login screen

---

## Troubleshooting

### Issue: Credentials Not Detected

**Solution:** Check console for this message:
```
No external credentials detected, using RuneLite profile system
```

If you see this, your JVM arguments aren't being passed correctly.

**Fix:**
- Verify JVM arguments are in the correct field in EternalFarm
- Try environment variables instead
- Check for typos in parameter names

### Issue: Login Fails

**Solution:** Enable the AutoLogin plugin in Microbot:
1. Open plugin panel (wrench icon)
2. Search for "AutoLogin"
3. Enable it
4. Check the logs for authentication errors

### Issue: Proxy Not Working

**Solution:** 
- Verify proxy format: `ip:port:user:pass`
- Test proxy separately
- Check if proxy requires special authentication

---

## Advanced: Programmatic API

For custom launchers, you can set credentials programmatically:

```java
import net.runelite.client.plugins.microbot.util.security.ExternalLogin;

// Set credentials before login
ExternalLogin.setCredentials(
    "bot001@gmail.com",  // username
    "SecurePass123",     // password
    "192.168.1.100:8080:user:pass",  // proxy
    360,                 // world
    true                 // members
);

// Apply to Microbot
ExternalLogin.applyToActiveProfile();
```

---

## EternalFarm Configuration Template

Save this as `microbot-template.json` in EternalFarm:

```json
{
    "clientType": "Microbot",
    "clientPath": "C:/Microbot/microbot.jar",
    "jvmArguments": [
        "-Dmicrobot.username=%USERNAME%",
        "-Dmicrobot.password=%PASSWORD%",
        "-Dmicrobot.proxy=%PROXY%",
        "-Dmicrobot.world=%WORLD%",
        "-Dmicrobot.members=true"
    ],
    "autoLogin": true,
    "loadPlugins": [
        "nICK1 Private Hunter",
        "Player Hop Log"
    ]
}
```

---

## Benefits of This Setup

✅ **No RuneLite Profiles Needed** - Each client gets credentials from EternalFarm  
✅ **Proxy Support** - Each account can have its own proxy  
✅ **Easy Management** - Update all accounts in EternalFarm CSV  
✅ **Mass Deployment** - Launch 50+ clients with different accounts  
✅ **World Selection** - Assign specific worlds to accounts  

---

## Security Notes

⚠️ **Important:**
- Passwords are encrypted before storage
- Never commit credential files to git
- Use environment variables in production
- Proxies protect your IP across accounts

---

## Support

If EternalFarm still doesn't work after following this guide:

1. Check Microbot console logs
2. Verify EternalFarm supports JVM argument injection
3. Contact EternalFarm support with this integration guide
4. Consider using environment variables if JVM args don't work

---

**Ready to manage 50+ accounts effortlessly!** 🚀

