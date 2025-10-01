# Player Hop Log Plugin

## Overview
Automatically hops to a random P2P world when ANY other player is detected nearby - even at 50+ tile distance!

Perfect for:
- 🛡️ Avoiding PKers
- 🤫 Privacy while training
- 🏃 Escaping crashers
- 🎯 Solo activities

## How It Works

The plugin continuously monitors for nearby players every 600ms (very fast detection):

1. ✅ Detects ANY player on screen (excluding yourself)
2. ⚡ Instantly hops to a random P2P world
3. 🔄 Waits for hop to complete
4. 👀 Resumes monitoring

## Features

- **Ultra-sensitive detection** - Catches players the moment they appear
- **P2P worlds only** - Always hops to members worlds
- **Fast response** - 600ms check interval
- **Stats overlay** - Shows nearby players, total hops, and status
- **Zero config needed** - Just enable and go!

## Overlay Display

Located in **top-right corner**:

```
╔═══════════════════════╗
║  Player Hop Log       ║
╠═══════════════════════╣
║ Nearby Players:  0    ║ (Green=safe, Red=detected)
║ Total Hops:      5    ║ (Cyan)
║ Status: Area clear    ║ (White)
╚═══════════════════════╝
```

## Usage

1. **Enable the plugin** in the plugin panel
2. **That's it!** The plugin will automatically hop when it detects anyone

## Status Messages

- `Area clear - Monitoring` - No players nearby, all good
- `Player detected! Hopping...` - Found someone, hopping now!
- `Hopped! Monitoring...` - Successfully hopped, back to monitoring
- `Hop failed, retrying...` - Couldn't hop, will try again
- `Not logged in` - You need to be in-game

## Tips

- Works great with other Microbot scripts
- Can run alongside your hunter bot or any other activity
- Very lightweight - minimal performance impact
- Detection range covers entire screen + render distance

## Compatible With

✅ Example Plugin (Butterfly Hunter)  
✅ Any other Microbot plugin  
✅ All P2P activities  
✅ Any location in-game  

---

**Stay safe out there!** 🛡️

