# 🍃 LeafSkyblockCore

> **Plugin core cho server Skyblock với hệ thống tracking nông sản và bảo mật commands**

---

## 📋 Tổng Quan

LeafSkyblockCore là plugin core được thiết kế đặc biệt cho server Skyblock, cung cấp hệ thống theo dõi điểm nông sản tự động, chặn commands không mong muốn và nhiều tính năng mở rộng.

### ✨ Tính Năng Chính

- 🌾 **Crops Tracker** - Theo dõi điểm khi thu hoạch nông sản
- 🚫 **Command Blocker** - Chặn commands và tab-complete không mong muốn
- 🏝️ **Tích hợp SuperiorSkyblock2** - Chỉ tính điểm trong đảo của bạn
- 📊 **PlaceholderAPI Support** - Hiển thị điểm trên scoreboard, tab, chat
- 💾 **SQLite Database** - Lưu trữ dữ liệu an toàn với HikariCP
- ⚙️ **Cấu hình linh hoạt** - Tùy chỉnh messages, permissions, crops points
- 🎨 **MiniMessage Format** - Hỗ trợ màu sắc, gradient, hover, click events

---

## 📦 Yêu Cầu

| Plugin | Bắt buộc | Phiên bản |
|--------|----------|-----------|
| **Paper/Spigot** | ✅ | 1.20.x - 1.21.x |
| **Java** | ✅ | 21+ |
| **SuperiorSkyblock2** | ⚠️ | Latest (Softdepend) |
| **PlaceholderAPI** | ❌ | Latest (Khuyến nghị) |

---

## 🚀 Cài Đặt

1. **Download** plugin từ releases
2. **Đặt** file `.jar` vào folder `plugins/`
3. **Cài đặt** SuperiorSkyblock2 (nếu dùng Crops Tracker)
4. **Khởi động lại** server
5. **Cấu hình** files trong `plugins/LeafSkyblockCore/`

---

## 🎮 Commands

### 🌾 Crops Tracker

| Command | Mô tả | Permission |
|---------|-------|------------|
| `/lc cropstracker` | Xem điểm của bạn | `leafskyblockcore.cropstracker` |
| `/lc cropstracker check <player>` | Xem điểm người khác | `leafskyblockcore.cropstracker.check` |
| `/lc cropstracker set <player> <points>` | Set điểm cho người chơi | `leafskyblockcore.cropstracker.set` |
| `/lc cropstracker add <player> <points>` | Thêm điểm cho người chơi | `leafskyblockcore.cropstracker.add` |
| `/lc cropstracker reset <player>` | Reset điểm người chơi | `leafskyblockcore.cropstracker.reset` |
| `/lc cropstracker reload` | Reload config | `leafskyblockcore.cropstracker.reload` |

**Aliases:** `/lc`, `/leaf`, `/leafcore`, `/leafskyblockcore`

---

## 🔑 Permissions

### Crops Tracker Permissions

```yaml
leafskyblockcore.cropstracker          # Sử dụng command cơ bản
leafskyblockcore.cropstracker.check    # Xem điểm người khác
leafskyblockcore.cropstracker.set      # Set điểm (Admin)
leafskyblockcore.cropstracker.add      # Thêm điểm (Admin)
leafskyblockcore.cropstracker.reset    # Reset điểm (Admin)
leafskyblockcore.cropstracker.reload   # Reload config (Admin)
leafskyblockcore.cropstracker.admin    # Tất cả quyền admin
```

### Command Blocker Permissions

```yaml
leafskyblockcore.command.plugins       # Dùng /plugins, /pl
leafskyblockcore.command.version       # Dùng /version, /ver
leafskyblockcore.command.about         # Dùng /about
leafskyblockcore.command.help          # Dùng /help, /?
leafskyblockcore.command.stop          # Dùng /stop
leafskyblockcore.command.reload        # Dùng /reload, /rl
```

---

## 📊 PlaceholderAPI

### Placeholders có sẵn

| Placeholder | Mô tả | Ví dụ |
|-------------|-------|-------|
| `%leafskyblockcore_crops_points%` | Điểm nông sản của người chơi | `12345` |
| `%leafskyblockcore_crops_rank%` | Hạng của người chơi | `5` |
| `%leafskyblockcore_crops_top_1_name%` | Tên người chơi top 1 | `Steve` |
| `%leafskyblockcore_crops_top_1_points%` | Điểm của người chơi top 1 | `99999` |
| `%leafskyblockcore_crops_top_2_name%` | Tên người chơi top 2 | `Alex` |
| `%leafskyblockcore_crops_top_2_points%` | Điểm của người chơi top 2 | `88888` |
| `%leafskyblockcore_crops_top_X_name%` | Tên người chơi top X (1-10) | `Player` |
| `%leafskyblockcore_crops_top_X_points%` | Điểm của người chơi top X (1-10) | `77777` |

### Cách sử dụng

**Trong scoreboard:**
```yaml
- "&aĐiểm nông sản: &e%leafskyblockcore_crops_points%"
```

**Trong tab:**
```yaml
header: "&6Điểm: &e%leafskyblockcore_crops_points%"
```

---

## ⚙️ Cấu Hình

### 📁 Cấu trúc files

```
plugins/LeafSkyblockCore/
├── data.db                   # SQLite database (tất cả data)
├── messages.yml              # Tất cả messages
├── permissions.yml           # Tất cả permissions
├── crops-tracker/
│   └── config.yml           # Config crops và điểm
└── command-blocker/
    └── config.yml           # Config chặn commands
```

---

## 🌾 Crops Tracker

### Config

**File:** `crops-tracker/config.yml`

```yaml
crops:
  WHEAT: 1          # Lúa mì = 1 điểm
  CARROTS: 1        # Cà rốt = 1 điểm
  POTATOES: 1       # Khoai tây = 1 điểm
  BEETROOTS: 1      # Củ cải đường = 1 điểm
  NETHER_WART: 2    # Nether Wart = 2 điểm
  SWEET_BERRY_BUSH: 1
  COCOA: 2
  MELON: 1
  PUMPKIN: 1
```

**Thêm crops mới:**
```yaml
crops:
  TÊN_MATERIAL: ĐIỂM
```

### Cách Hoạt Động

**Điều kiện tính điểm:**
- ✅ Nông sản đã **chín hoàn toàn**
- ✅ Đang ở trong **đảo của bạn** (SuperiorSkyblock2)
- ✅ **Không phải đảo** của người khác
- ❌ **Không tính** fortune drops (chỉ tính 1 lần)

**Ví dụ:**
1. Bạn trồng lúa mì trong đảo
2. Lúa mì chín hoàn toàn
3. Bạn thu hoạch → **+1 điểm**
4. Fortune III cho 3 lúa mì → **Vẫn chỉ +1 điểm**

---

## 🚫 Command Blocker

### Config

**File:** `command-blocker/config.yml`

```yaml
enabled: true
block-all-tab-complete: true  # Chặn tất cả tab-complete

commands:
  # Commands được phép (whitelist khi block-all: true)
  lc: "leafskyblockcore.use"
  leafskyblockcore: "leafskyblockcore.use"
  leaf: "leafskyblockcore.use"
  leafcore: "leafskyblockcore.use"
  
  # Vanilla commands cần permission
  plugins: "leafskyblockcore.command.plugins"
  pl: "leafskyblockcore.command.plugins"
  version: "leafskyblockcore.command.version"
  ver: "leafskyblockcore.command.version"
  about: "leafskyblockcore.command.about"
  help: "leafskyblockcore.command.help"
  "?": "leafskyblockcore.command.help"
  stop: "leafskyblockcore.command.stop"
  reload: "leafskyblockcore.command.reload"
  rl: "leafskyblockcore.command.reload"
```

### 2 Chế Độ Hoạt Động

#### 🔴 Mode 1: Block All (Khuyến nghị)
```yaml
block-all-tab-complete: true
```

**Cách hoạt động:**
- ✅ **CHỈ** hiện commands trong config
- ✅ **CHỈ** cho phép dùng commands trong config
- ✅ Phải có permission tương ứng
- ❌ Tất cả commands khác bị ẩn và chặn

**Ví dụ:**
```yaml
commands:
  lc: "leafskyblockcore.use"
  spawn: "essentials.spawn"
```
→ Người chơi chỉ thấy `/lc` và `/spawn` khi tab, không thấy gì khác!

#### 🟡 Mode 2: Blacklist
```yaml
block-all-tab-complete: false
```

**Cách hoạt động:**
- ✅ Hiện tất cả commands
- ❌ Chặn commands trong config nếu không có permission
- ✅ Commands không trong config vẫn hiện bình thường

### Thêm Commands Mới

**Cho phép command của plugin khác:**
```yaml
commands:
  spawn: "essentials.spawn"
  home: "essentials.home"
  tpa: "essentials.tpa"
  shop: "yourplugin.shop"
```

**Chặn command vanilla:**
```yaml
commands:
  gamemode: "minecraft.command.gamemode"
  give: "minecraft.command.give"
  op: "minecraft.command.op"
```

---

## 💬 Messages Config

**File:** `messages.yml`

Hỗ trợ **MiniMessage format** với màu sắc, gradient, hover, click!

```yaml
general:
  no-permission: "<red>You don't have permission to use this command!"
  player-not-found: "<red>Player not found!"

crops-tracker:
  your-points: "<green>Your crops points: <yellow>{points}"
  player-points: "<green>{player}'s crops points: <yellow>{points}"

command-blocker:
  blocked: "<red>You don't have permission to use /{command}!"
```

**Ví dụ nâng cao:**
```yaml
your-points: "<gradient:#00ff00:#ffff00>Điểm: {points}</gradient>"
hover-message: "<hover:show_text:'<gold>Click để xem top!'>Xem điểm</hover>"
click-message: "<click:run_command:/lc cropstracker top>Xem bảng xếp hạng</click>"
```

[📖 MiniMessage Format Guide](https://docs.advntr.dev/minimessage/format.html)

---

## 🎨 Ví Dụ Sử Dụng

### Cho Players

```bash
# Xem điểm của bạn
/lc cropstracker

# Xem điểm người khác
/lc cropstracker check Steve
```

### Cho Admins

```bash
# Set điểm cho người chơi
/lc cropstracker set Steve 10000

# Thêm điểm thưởng
/lc cropstracker add Steve 500

# Reset điểm
/lc cropstracker reset Steve

# Reload config
/lc cropstracker reload
```

### Setup Command Blocker

**Bước 1:** Bật block-all mode
```yaml
block-all-tab-complete: true
```

**Bước 2:** Thêm commands được phép
```yaml
commands:
  lc: "leafskyblockcore.use"
  spawn: "essentials.spawn"
  home: "essentials.home"
  sethome: "essentials.sethome"
  tpa: "essentials.tpa"
  shop: "shop.use"
```

**Bước 3:** Set permissions trong LuckPerms
```bash
/lp group default permission set leafskyblockcore.use true
/lp group default permission set essentials.spawn true
/lp group default permission set essentials.home true
```

**Kết quả:** Người chơi chỉ thấy và dùng được 5 commands trên!

---

## 🔧 Troubleshooting

### Plugin không load?
- ✅ Kiểm tra Java version (cần Java 21+)
- ✅ Kiểm tra Paper/Spigot version (1.20.x - 1.21.x)
- ✅ Xem console log để biết lỗi

### Crops Tracker không tính điểm?
- ✅ Kiểm tra nông sản đã chín chưa
- ✅ Kiểm tra bạn có trong đảo của mình không
- ✅ Kiểm tra crops có trong config không
- ✅ Cài đặt SuperiorSkyblock2

### Command Blocker không hoạt động?
- ✅ Kiểm tra `enabled: true` trong config
- ✅ Kiểm tra permissions đã set đúng chưa
- ✅ Reload plugin: `/lc cropstracker reload`
- ✅ Restart server để áp dụng PlayerCommandSendEvent

### Tab-complete vẫn hiện nhiều commands?
- ✅ Set `block-all-tab-complete: true`
- ✅ Chỉ thêm commands muốn hiện vào config
- ✅ **Restart server** (không phải reload!)
- ✅ Kiểm tra permissions của người chơi

### Placeholder không hoạt động?
- ✅ Cài đặt PlaceholderAPI
- ✅ Reload PlaceholderAPI: `/papi reload`
- ✅ Test placeholder: `/papi parse me %leafskyblockcore_crops_points%`

### Database bị lỗi?
- ✅ Kiểm tra file `data.db` có tồn tại không
- ✅ Kiểm tra quyền write vào folder plugin
- ✅ Backup và xóa `data.db` để tạo mới

---

## 💡 Tips & Tricks

### Bảo mật Server tốt nhất

1. **Bật block-all mode:**
```yaml
block-all-tab-complete: true
```

2. **Chỉ cho phép commands cần thiết:**
```yaml
commands:
  lc: "leafskyblockcore.use"
  spawn: "essentials.spawn"
  home: "essentials.home"
  # Không thêm /plugins, /version, /help...
```

3. **Set permissions chặt chẽ:**
```bash
# Chỉ admin mới thấy /plugins
/lp group admin permission set leafskyblockcore.command.plugins true
```

### Tạo Leaderboard Crops

Dùng PlaceholderAPI với plugin scoreboard:
```yaml
lines:
  - "&6&lTOP FARMERS"
  - "&e1. %leafskyblockcore_crops_top_1_name%: &a%leafskyblockcore_crops_top_1_points%"
  - "&e2. %leafskyblockcore_crops_top_2_name%: &a%leafskyblockcore_crops_top_2_points%"
  - "&e3. %leafskyblockcore_crops_top_3_name%: &a%leafskyblockcore_crops_top_3_points%"
  - "&e4. %leafskyblockcore_crops_top_4_name%: &a%leafskyblockcore_crops_top_4_points%"
  - "&e5. %leafskyblockcore_crops_top_5_name%: &a%leafskyblockcore_crops_top_5_points%"
  - ""
  - "&7Your rank: &e#%leafskyblockcore_crops_rank%"
  - "&7Your points: &e%leafskyblockcore_crops_points%"
```

### Backup Dữ Liệu

```bash
# Backup database
cp plugins/LeafSkyblockCore/data.db backups/data-$(date +%Y%m%d).db

# Backup configs
tar -czf backups/configs-$(date +%Y%m%d).tar.gz plugins/LeafSkyblockCore/*.yml plugins/LeafSkyblockCore/*/*.yml
```

---

## 📞 Hỗ Trợ

- 🐛 **Bug Report:** [GitHub Issues](https://github.com/yourusername/LeafSkyblockCore/issues)
- 💡 **Feature Request:** [GitHub Issues](https://github.com/yourusername/LeafSkyblockCore/issues)
- 💬 **Discord:** [Join Server](https://discord.gg/yourserver)
- 📧 **Email:** support@yourdomain.com

---

## 📝 Changelog

### Version 1.0
- ✨ Thêm Crops Tracker system
- ✨ Thêm Command Blocker với 2 modes
- ✨ Tích hợp SuperiorSkyblock2
- ✨ PlaceholderAPI support
- ✨ SQLite database với HikariCP
- ✨ MiniMessage format support
- ✨ Config system (messages, permissions, crops, commands)

---

## 📜 License

Copyright © 2024 ipapervn. All rights reserved.

---

## 🙏 Credits

- **Paper Team** - Paper API
- **PlaceholderAPI** - Placeholder support
- **SuperiorSkyblock2** - Island management
- **HikariCP** - Database connection pool
- **Kyori Adventure** - MiniMessage format

---

<div align="center">

**Made with ❤️ for Skyblock Community**

⭐ **Nếu thích plugin, hãy cho 1 star!** ⭐

[⬆ Back to top](#-leafskyblockcore)

</div>
