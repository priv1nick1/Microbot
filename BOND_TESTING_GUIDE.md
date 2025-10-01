# 🎁 Bond Muling - Local Testing Guide

## Quick Test Setup (5 Minutes)

### Step 1: Prepare Test Accounts

Create `ironman-accounts.txt` with 2-3 test accounts:

```
testironman1@gmail.com:password123
testironman2@gmail.com:password456
```

**Note:** Use real OSRS accounts you own for testing!

---

### Step 2: Launch Main Account (Bond Giver)

1. **Open Microbot** (double-click the launcher or run from IntelliJ)
2. **Log in** to your main account (the one WITH bonds)
3. **Go to Grand Exchange** or any safe location
4. **Put bonds in inventory** (2-3 for testing)
5. **Open plugin list** → Find **"nICK Bond Master"**
6. **Enable** the plugin
7. **Watch the overlay** appear (top-left)

**Overlay shows:**
- Bonds Given: 0
- Total Accounts: 2 (or however many in file)
- Current: (waiting)
- Status: Waiting for: testironman1@gmail.com

---

### Step 3: Launch Ironman #1

**Option A: Manual (easiest for testing)**
1. **Open another Microbot instance**
2. **Log in** to `testironman1@gmail.com`
3. **Go to same location** as your main
4. **Enable** "nICK Bond Receiver" plugin
5. **Watch the magic!** ✨

**Option B: Using Batch Launcher**
```bash
# Edit launch-all-accounts.bat
set ACCOUNTS_FILE=ironman-accounts.txt

# Run it
launch-all-accounts.bat
```

---

### Step 4: Watch The Process

**On Main Account (Master):**
```
Status: Waiting for: testironman1@gmail.com
        ↓
Status: Using bond on: TestIronman1
        ↓
Status: Waiting for acceptance: TestIronman1
        ↓
Status: Waiting for logout: TestIronman1
        ↓
Bonds Given: 1
Status: Waiting for: testironman2@gmail.com
```

**On Ironman (Receiver):**
```
Status: Waiting for turn...
        ↓
Status: Looking for trade request...
        ↓
Status: Accepting bond trade...
        ↓
Status: Using bond...
        ↓
Status: Applying membership...
        ↓
Status: Logging out...
        ↓
Status: Complete!
```

---

### Step 5: Verify Success

**Check on Ironman:**
- ✅ Bond in inventory OR membership applied
- ✅ 14 days membership showing
- ✅ Client logged out (if successful)

**Check on Main:**
- ✅ 1 less bond in inventory
- ✅ "Bonds Given" counter increased
- ✅ Waiting for next account

---

## 🐛 Troubleshooting

### "Character name not detected"
- **Fix:** Make sure ironman is fully logged in (not at login screen)
- **Fix:** Check that `Rs2Player.getLocalPlayer()` returns valid data

### "Bond not received"
- **Fix:** Both accounts must be at **same location**
- **Fix:** Make sure main can **see** the ironman on screen
- **Fix:** Ironman must have **Accept Aid ON**

### "Trade didn't work"
- **Fix:** Not in wilderness/dangerous area
- **Fix:** Both accounts on **same world**
- **Fix:** Try restarting both plugins

### "Stuck waiting"
- **Fix:** Check `C:\Users\YourName\.microbot\bonding\` files
- **Fix:** Delete the folder and restart both plugins
- **Fix:** Check console for error messages

---

## 📊 Testing Checklist

- [ ] Main account has bonds in inventory
- [ ] Main account at safe location (GE recommended)
- [ ] Bond Master plugin enabled on main
- [ ] Ironman accounts added to `ironman-accounts.txt`
- [ ] Both accounts on same world
- [ ] Both accounts at same location
- [ ] Bond Receiver plugin enabled on ironman
- [ ] Watch overlays for status updates
- [ ] Check console logs for errors

---

## 🎯 Success Criteria

**Test Passed If:**
1. ✅ Character name auto-detected
2. ✅ Master finds ironman
3. ✅ Bond trade initiated
4. ✅ Ironman accepts bond
5. ✅ Ironman applies 14-day membership
6. ✅ Ironman logs out
7. ✅ Master moves to next account
8. ✅ Process repeats for all accounts

---

## 💡 Pro Tips

**For Fastest Testing:**
- Use **Grand Exchange** (always busy, easy to find)
- Use **F2P world** initially (faster login)
- Test with **2 accounts** first
- Check **console logs** in IntelliJ for detailed info

**Common Test Scenario:**
1. Main at GE with 3 bonds
2. 3 test ironmen in file
3. Launch first ironman manually
4. Watch it complete
5. Launch second ironman
6. Verify automation works

---

## 📝 Test Notes

**What to Look For:**
- Character name detection speed
- Trade window interaction
- Bond redemption interface clicks
- Logout timing
- Queue file updates

**Expected Time Per Account:**
- ~30-60 seconds per bond
- Includes: detect → trade → accept → use → logout

---

**Ready to test! Good luck!** 🚀


