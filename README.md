# Bungee Ban System
A BungeeCord plugin that has several features to help manage players across the server network.

## Features

### 1. Kick

- [ ] Done

**Description**: Kick a player from the server network

**Commands**:
- `/kick <player> [reason]`
- `/kickall [reason]`
- `/kickserver <server> [reason]`
- `/kickip <ip|cidr> [reason]`

**Arguments**:
- `player`: The player to kick
- `server`: The server to kick all players from (`this` for the current server)
- `ip`: The IP to kick
- `cidr`: The CIDR notation to kick

**Permissions**:
- `bansystem.kick`: Allow kicking of other players
- `bansystem.kick.all`: Allow kicking of all players
- `bansystem.kick.server`: Allow kicking of all players on a specific server
- `bansystem.kick.ip`: Allow kicking of players with a specific IP
- `bansystem.kick.staff`: Allow kicking of other staff members

### 2. Ban

- [ ] Done

**Description**: Ban a player from the server network

**Commands**: 
- `/ban <player> [-t <time>] [-ip|-xip] <reason>`
- `/banip <player|ip|cidr> [-t <time>] <reason>`
- `/unban <player>`
- `/unbanip <player|ip|cidr>`

**Arguments**:
- `player`: The player to ban
- `ip`: The IP to ban/unban
- `cidr`: The CIDR notation to ban/unban
- `-t <time>`: The time the ban should last (e.g. `1s`, `1mi`, `1h`, `1d`, `1w`, `1mo`, `1y`)
- `-ip`: Additionally ban the IP of the player
- `-xip`: Additionally ban the IP of the player and all players that connect with the same IP
- `reason`: The reason for the ban

**Permissions**:
- `bansystem.ban`: Allow banning of players
- `bansystem.ban.ip`: Allow banning of IPs
- `bansystem.ban.xip`: Allow banning of IPs and all players that connect with the same IP
- `bansystem.ban.staff`: Allow banning of other staff members
- `bansystem.ban.unban`: Allow unbanning of players

### 3. Mute

- [ ] Done

**Description**: Mute a player from the server network

**Commands**:
- `/mute <player> [-t <time>] [-ip] <reason>`
- `/muteip <player|ip|cidr> [-t <time>] <reason>`
- `/unmute <player>`
- `/unmuteip <player|ip|cidr>`

**Arguments**:
- `player`: The player to mute
- `ip`: The IP to mute/unmute
- `cidr`: The CIDR notation to mute/unmute
- `-t <time>`: The time the mute should last (e.g. `1s`, `1mi`, `1h`, `1d`, `1w`, `1mo`, `1y`)
- `-ip`: Additionally mute the IP of the player
- `reason`: The reason for the mute

**Permissions**:
- `bansystem.mute`: Allow muting of players
- `bansystem.mute.ip`: Allow muting of IPs
- `bansystem.mute.staff`: Allow muting of other staff members
- `bansystem.mute.unmute`: Allow unmuting of players

### 4. Reports (Non Staff)

- [ ] Done

**Description**: Report a player to the staff

**Commands**:
- `/report <player> <reason>`

**Arguments**:
- `player`: The player to report
- `reason`: The reason for the report

**Permissions**:
- `bansystem.report`: Allow reporting of players

### 5. Reports (Staff)

- [ ] Done

**Description**: Manage reports

**Commands**:
- `/reports [page]`: List all open reports
- `/reportsof <player> [page]` List all reports about a specific player
- `/report <reportId>`: Show a specific report
- `/report <reportId> <action> [reason]`: Take action on a report
- `/notes <player> [page]`: List all notes about a specific player
- `/note add <player> <note>`: Add a note to a player
- `/note remove <player> <noteId>`: Remove a note from a player
- `/note <noteId>`: Show a specific note in full detail

**Arguments**:
- `player`: A specific player (either username or uuid)
- `page`: The page to show (default: 1)
- `reportId`: The id of the report
- `action`: The action to take on a report (`accept`, `deny`, `ignore`)
- `reason`: The reason for the action
- `note`: The note to add to a player
- `noteId`: The id of the note

**Permissions**:
- `bansystem.reports`: Allow viewing of reports
- `bansystem.reports.of`: Allow viewing of reports about a specific player
- `bansystem.reports.action`: Allow taking action on reports
- `bansystem.reports.notes`: Allow viewing of notes about a specific player
- `bansystem.reports.notes.add`: Allow adding notes to a player
- `bansystem.reports.notes.remove`: Allow removing notes from a player

### 6. Staff Chat

- [ ] Done

**Description**: Chat with other staff members

**Commands**:
- `/staff <message>`: Send a message to all staff members

**Arguments**:
- `message`: The message to send

**Permissions**:
- `bansystem.staffchat.write`: Allow sending of messages to all staff members
- `bansystem.staffchat.read`: Allow reading of messages from all staff members

## Explanation of some things

### BanId

The ban id is a unique identifier for a ban. It is used to identify a ban in the database. It is also used to reference a ban in the XIP ban system.

### XIP Ban

The XIP ban system is a system that automatically bans all players that connect with the same IP as a banned player.
It is used to prevent players from bypassing bans by connecting with a different account.

### CIDR Notation

The CIDR notation is a way to specify a range of IP addresses. It is used to ban a range of IPs.
Example: `159.89.173.104/28` would ban all IPs from `159.89.173.96` to `159.89.173.111` (inclusive) -> 16 IPs.