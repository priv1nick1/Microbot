# EternalFarm Quick Start for Microbot

## ⚡ Quick Setup (5 Minutes)

### Step 1: Build Microbot

In IntelliJ, run Maven `install`:
- Open Maven panel (right side)
- Lifecycle → `install`
- Wait for BUILD SUCCESS
- Your JAR file will be in: `Microbot/runelite-client/target/`

---

### Step 2: Configure EternalFarm

In EternalFarm, add these **JVM Arguments** for each client:

```
-Dmicrobot.username=%USERNAME%
-Dmicrobot.password=%PASSWORD%
-Dmicrobot.world=%WORLD%
-Dmicrobot.members=true
```

If you use proxies, add:
```
-Dmicrobot.proxy=%PROXY%
```

---

### Step 3: Account List in EternalFarm

Format your accounts like this:

| Username | Password | Proxy (optional) | World | Members |
|----------|----------|------------------|-------|---------|
| bot001@gmail.com | pass123 | 1.2.3.4:8080 | 360 | true |
| bot002@gmail.com | pass456 | 1.2.3.5:8080 | 361 | true |
| bot003@gmail.com | pass789 | 1.2.3.6:8080 | 362 | true |

---

### Step 4: Launch & Test

1. **In EternalFarm**: Click "Start Client" for one account
2. **Watch Microbot console** for:
   ```
   External credentials detected:
     Username: bot001@gmail.com
     Password: ***SET***
   ```
3. **Enable AutoLogin** plugin in Microbot
4. **Test login** - Should automatically log in!

---

## ✅ Verification Checklist

- [ ] Microbot JAR built successfully
- [ ] JVM arguments added to EternalFarm
- [ ] Account credentials loaded in EternalFarm
- [ ] Test client launches and shows "External credentials detected"
- [ ] AutoLogin plugin enabled in Microbot
- [ ] Login works automatically

---

## 🎯 Full Workflow

Once set up, here's what happens:

1. **EternalFarm launches Microbot** with JVM arguments
2. **Microbot reads credentials** from arguments
3. **AutoLogin plugin logs in** using external credentials
4. **Your scripts run** (nICK1 Private Hunter, Player Hop Log, etc.)
5. **Next client launches** with different account
6. **Repeat for all 50+ accounts!**

---

## 🔥 Pro Tips

### Tip 1: World Distribution
Spread accounts across worlds to avoid bans:
```
Account 1: World 360
Account 2: World 361
Account 3: World 362
...
```

### Tip 2: Proxy Rotation
Use different proxies for each account:
```
Account 1: Proxy 1
Account 2: Proxy 2
Account 3: Proxy 3
...
```

### Tip 3: Auto-Enable Plugins
Want scripts to auto-start? Enable them ONCE in Microbot, then:
- Settings are saved per account
- Will auto-enable on next launch

---

## 🐛 Troubleshooting

### "No external credentials detected"
**Fix:** Check JVM arguments in EternalFarm - make sure they're in the right field

### Login still uses RuneLite profile
**Fix:** External credentials take priority - if you see this, arguments aren't being passed

### Can't find Microbot JAR
**Fix:** After Maven install, check `runelite-client/target/client-*.jar`

---

## 📝 EternalFarm Template

Copy this into EternalFarm's client configuration:

```json
{
  "name": "Microbot",
  "jarPath": "C:/Microbot/runelite-client/target/client-1.11.18-SNAPSHOT-shaded.jar",
  "jvmArgs": "-Dmicrobot.username=%USERNAME% -Dmicrobot.password=%PASSWORD% -Dmicrobot.proxy=%PROXY% -Dmicrobot.world=%WORLD% -Dmicrobot.members=true",
  "autoLogin": true,
  "accountVariables": true
}
```

---

**You're now ready to manage 50+ accounts with ease!** 🎉

Need help? Check the full guide: `ETERNALFARM_INTEGRATION.md`

