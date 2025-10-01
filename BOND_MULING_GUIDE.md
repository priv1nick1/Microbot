# 🎁 Bond Muling System - Complete Guide

## What This Does

Automatically gives bonds from your **main account** to **30+ ironman accounts** and applies 14-day membership to each one!

No more manual clicking for hours! ✨

---

## 🚀 Quick Start (5 Minutes)

### Step 1: Create Ironman Accounts File

Create `ironman-accounts.txt` in your Microbot folder:

```
ironman1@gmail.com:password123
ironman2@gmail.com:password456
ironman3@gmail.com:password789
```

**Format:** `email:password` (SAME as your main accounts.txt!)

**Easy Mode:**
- ✅ Just copy-paste from your existing accounts list!
- ✅ Character names detected automatically on login!
- ✅ This file is in `.gitignore` - will NOT upload to GitHub!

---

### Step 2: Setup Main Account (Bond Giver)

1. **Log in to your main account** (the one WITH bonds)
2. **Put bonds in your inventory** (as many as you need)
3. **Go to a safe location** (GE, bank, anywhere)
4. **Open Microbot plugin list**
5. **Enable "Bond Master"** plugin
6. **Check the overlay** - it shows:
   - Bonds Given
   - Total Accounts
   - Current Account
   - Status

---

### Step 3: Setup Ironman Accounts (Bond Receivers)

#### Option A: Manual Launch (Testing)

1. **Log in to ironman account**
2. **Go to same location as main**
3. **Enable "Bond Receiver"** plugin
4. **Watch it work!** 🎉

#### Option B: Batch Launch (Production - 30+ accounts)

1. **Edit `launch-all-accounts.bat`** to point to `ironman-accounts.txt`:
   ```bat
   set ACCOUNTS_FILE=ironman-accounts.txt
   ```

2. **Double-click `launch-all-accounts.bat`**
3. **All ironmen launch automatically!**

---

## 🔄 How It Works

### The Flow:

```
┌─────────────────┐
│  MASTER ACCOUNT │ (Your main with bonds)
│   (Location A)  │
└────────┬────────┘
         │
         │ 1. Waits for ironman
         │
         ▼
┌─────────────────┐
│ IRONMAN 1 LOGIN │
│   (Location A)  │
└────────┬────────┘
         │
         │ 2. Master uses bond on ironman
         │
         ▼
┌─────────────────┐
│ IRONMAN ACCEPTS │
│  Uses bond      │
│  Applies 14d    │
│  Logs out       │
└────────┬────────┘
         │
         │ 3. Next ironman logs in
         │
         ▼
┌─────────────────┐
│ IRONMAN 2 LOGIN │
└─────────────────┘
```

### Communication:

Both plugins communicate via shared files in:
```
C:\Users\YourName\.microbot\bonding\
```

Files created:
- `queue.txt` - List of accounts
- `status.txt` - Current state (WAITING, BOND_OFFERED, BOND_RECEIVED, COMPLETE)
- `current.txt` - Current account being processed

---

## 📊 What You'll See

### Master Overlay (Top Left):
```
┌─────────────────────────┐
│     Bond Master         │
├─────────────────────────┤
│ Bonds Given:        5   │
│ Total Accounts:    30   │
│ Current:      IronMan6  │
│ Status:   Using bond... │
└─────────────────────────┘
```

### Receiver Overlay (Top Left):
```
┌─────────────────────────┐
│    Bond Receiver        │
├─────────────────────────┤
│ Bond Received:     Yes  │
│ Membership:        Yes  │
│ Status:  Logging out... │
└─────────────────────────┘
```

---

## ⚙️ Advanced Setup

### Using with EternalFarm

If you want to use EternalFarm to launch ironmen:

1. **Load `ironman-accounts.txt` into EternalFarm**
2. **Set client to Microbot**
3. **In EternalFarm, enable "Bond Receiver" plugin for all accounts**
4. **Click "Start All"**

### Multi-Account Coordination

**Important:** Make sure all ironmen spawn at the **same location** as your main!

Recommended locations:
- ✅ Grand Exchange (easy)
- ✅ Varrock West Bank
- ✅ Lumbridge castle

---

## 🐛 Troubleshooting

### "No accounts found!"
- Check `ironman-accounts.txt` exists in Microbot folder
- Make sure format is: `email:password:character_name`
- No empty lines or comments inside account list

### "Bond not received!"
- Make sure **character name matches EXACTLY** in file
- Both accounts must be at **same location**
- Master must have **bonds in inventory**
- Try increasing timeout (contact support)

### "Trade didn't work!"
- Make sure ironman has **Accept Aid ON**
- Check ironman isn't in **PvP area**
- Make sure both accounts are **members** (oh wait, that's what we're fixing! 😄)

### Accounts getting out of sync
- **Stop both plugins**
- **Delete** `C:\Users\YourName\.microbot\bonding\` folder
- **Restart plugins**

---

## 🔒 Security Notes

**CRITICAL:** `ironman-accounts.txt` contains passwords!

✅ **Protected:** This file is in `.gitignore` and will NEVER upload to GitHub  
✅ **Local Only:** Keep this file on your server only  
✅ **Backup:** Keep a secure backup somewhere safe  

---

## 📈 Performance

**Expected Speed:**
- ~60 seconds per account (safe)
- 30 accounts = ~30 minutes total
- Fully automated! ☕ Go make coffee!

**Optimization:**
- Use a **dedicated world** (less lag)
- Use **quiet location** (fewer players)
- Keep main account **logged in** entire time

---

## 🎉 Success!

When complete, you'll see:
- ✅ All 30 ironmen with 14 days membership
- ✅ Main account with 30 fewer bonds
- ✅ Zero manual clicking
- ✅ Time saved: HOURS!

---

## 🆘 Need Help?

Check logs:
- **Master:** Look for "Bond Master" in console
- **Receiver:** Look for "Bond Receiver" in console

Common issues are usually:
1. Wrong character name in file
2. Accounts not at same location
3. No bonds in main inventory

---

**You're ready to automate 30+ bonds! Let's go!** 🚀🎁

