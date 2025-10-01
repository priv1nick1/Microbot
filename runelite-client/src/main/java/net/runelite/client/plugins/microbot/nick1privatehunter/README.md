# nICK1 Private Hunter

## Overview
Private butterfly hunting bot for nICK1. Automatically catches butterflies based on your Hunter level.

## Features

### Smart Level Detection
- **< Level 65**: Catches Ruby Harvest in West Aldarin
- **≥ Level 65**: Catches Sunlight Moth north of Hunter Guild

### Automatic Setup
✅ Checks for required equipment  
✅ Banks if missing items  
✅ Withdraws and equips full graceful + butterfly net  
✅ Walks to correct location based on level  
✅ Catches butterflies continuously  

### Required Equipment
- Full Graceful outfit (hood, top, legs, gloves, boots, cape)
- Butterfly net
- Coins (for traveling)

### Smart Features
- **Smart Restart**: If already at location with net, skips banking and starts catching immediately
- **Dialogue Handler**: Automatically handles sailing confirmations
- **Bank Support**: Works with both bank booths and bank chests
- **Auto-Walking**: Uses Rs2Walker to navigate to butterfly locations

## Stats Overlay

Located in **top-left corner**:

```
╔════════════════════════════╗
║  nICK1 Private Hunter      ║  (Orange)
╠════════════════════════════╣
║ Exp/H:          15,243     ║  (Green)
║ Current level:  58         ║  (Yellow) <-- NEW!
║ Time to level 65:  2h 34m  ║  (Cyan)
║ Status:  Catching Ruby...  ║  (White)
╚════════════════════════════╝
```

## How to Use

1. **Have required items in bank** (graceful + butterfly net + coins)
2. **Enable the plugin** - Search for "nICK1 Private Hunter"
3. **Script will automatically**:
   - Bank and gear up
   - Travel to correct location
   - Catch butterflies forever!

## Locations

### Ruby Harvest (< 65)
- **Area**: West Aldarin
- **Coordinates**: WorldArea(1337, 2930, 9, 10, 0)

### Sunlight Moth (≥ 65)
- **Area**: North of Hunter Guild
- **Coordinates**: WorldArea(1590, 3215, 10, 10, 0) *(adjust when you get there!)*

## Tips

- Works great with **Player Hop Log** plugin for safety!
- If you level up to 65, script automatically switches to Sunlight Moths
- Can safely restart - won't re-bank if already at location
- Handles sailing costs dialogue automatically

---

**Happy hunting!** 🦋


