# 🍃 LeafSkyblockCore

> **Plugin core cho server Skyblock với hệ thống tracking nông sản và generator**

---

## 📋 Tổng Quan

LeafSkyblockCore là plugin core được thiết kế đặc biệt cho server Skyblock, cung cấp hệ thống theo dõi điểm nông sản tự động, generator block tự động sinh vật phẩm với hologram đếm ngược và nhiều tính năng mở rộng.

### ✨ Tính Năng Chính

- 🌾 **Crops Tracker** - Theo dõi điểm khi thu hoạch nông sản
- ⚙️ **Generator** - Block generator tự động sinh vật phẩm, hologram đếm ngược (DecentHolograms)
- 🖥️ **Generator GUI** - Chuột phải vào generator để xem thông tin và nhận vật phẩm
- 🏝️ **Tích hợp SuperiorSkyblock2** - Chỉ tính điểm và đặt generator trong đảo của bạn
- 📊 **PlaceholderAPI Support** - Hiển thị điểm trên scoreboard, tab, chat
- 💾 **SQLite Database** - Lưu trữ dữ liệu an toàn với HikariCP
- ⚙️ **Cấu hình linh hoạt** - Tùy chỉnh messages, permissions, crops points, GUI layout
- 🎨 **MiniMessage Format** - Hỗ trợ màu sắc, gradient, hover, click events

---

## 📦 Yêu Cầu

| Plugin | Bắt buộc | Phiên bản |
|--------|----------|-----------|
| **Paper/Spigot** | ✅ | 1.20.x - 1.21.x |
| **Java** | ✅ | 21+ |
| **SuperiorSkyblock2** | ⚠️ | Latest (Softdepend) |
| **DecentHolograms** | ⚠️ | Latest (Softdepend) |
| **PlaceholderAPI** | ❌ | Latest (Khuyến nghị) |

---

## 🚀 Cài Đặt

1. **Download** plugin từ releases
2. **Đặt** file `.jar` vào folder `plugins/`
3. **Cài đặt** SuperiorSkyblock2 và DecentHolograms
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

### ⚙️ Generator

| Command | Mô tả | Permission |
|---------|-------|------------|
| `/lc generator give <player> [amount]` | Cho player item generator | `leafskyblockcore.generator.give` |

### 🔄 Reload

| Command | Mô tả | Permission |
|---------|-------|------------|
| `/lc reload` | Reload tất cả configs | `leafskyblockcore.reload` |
| `/lc reload all` | Reload tất cả configs | `leafskyblockcore.reload` |
| `/lc reload generator` | Reload generator config | `leafskyblockcore.reload` |

**Aliases:** `/lc`, `/leaf`, `/leafcore`, `/leafskyblockcore`

---

## 🔑 Permissions

### Crops Tracker

```yaml
leafskyblockcore.cropstracker          # Xem điểm của bạn
leafskyblockcore.cropstracker.check    # Xem điểm người khác
leafskyblockcore.cropstracker.set      # Set điểm (Admin)
leafskyblockcore.cropstracker.add      # Thêm điểm (Admin)
leafskyblockcore.cropstracker.reset    # Reset điểm (Admin)
leafskyblockcore.cropstracker.admin    # Tất cả quyền admin crops
```

### Generator

```yaml
leafskyblockcore.generator.use         # Đặt generator block
leafskyblockcore.generator.break       # Phá generator block
leafskyblockcore.generator.admin       # Bypass mọi check generator
leafskyblockcore.generator.give        # Cho item generator (Admin)
```

### Reload

```yaml
leafskyblockcore.reload                # Reload configs
```

---

## 📊 PlaceholderAPI

| Placeholder | Mô tả | Ví dụ |
|-------------|-------|-------|
| `%leafskyblockcore_crops_points%` | Điểm nông sản của người chơi | `12345` |
| `%leafskyblockcore_crops_rank%` | Hạng của người chơi | `5` |
| `%leafskyblockcore_crops_top_1_name%` | Tên người chơi top 1 | `Steve` |
| `%leafskyblockcore_crops_top_1_points%` | Điểm của người chơi top 1 | `99999` |
| `%leafskyblockcore_crops_top_X_name%` | Tên người chơi top X (1-10) | `Player` |
| `%leafskyblockcore_crops_top_X_points%` | Điểm của người chơi top X (1-10) | `77777` |

---

## ⚙️ Cấu Hình

### 📁 Cấu trúc files

```
plugins/LeafSkyblockCore/
├── data.db                   # SQLite database
├── messages.yml              # Tất cả messages
├── permissions.yml           # Tất cả permissions
├── crops-tracker/
│   └── config.yml           # Config crops và điểm
└── generator/
    └── config.yml           # Config generator, output, GUI
```

---

## 🌾 Crops Tracker

### Config

**File:** `crops-tracker/config.yml`

```yaml
crops:
  WHEAT: 1
  CARROTS: 1
  POTATOES: 1
  BEETROOTS: 1
  NETHER_WART: 2
  SWEET_BERRY_BUSH: 1
  COCOA: 2
  MELON: 1
  PUMPKIN: 1
```

### Cách Hoạt Động

- ✅ Nông sản đã **chín hoàn toàn**
- ✅ Đang ở trong **đảo của bạn** (SuperiorSkyblock2)
- ❌ **Không tính** fortune drops (chỉ tính 1 lần mỗi block)

---

## ⚙️ Generator

### Config

**File:** `generator/config.yml`

```yaml
countdown: 15          # Giây mỗi chu kỳ
max-per-island: 1      # Số generator tối đa mỗi đảo

hologram:
  lines:
    counting: "&e⏳ &6{seconds}s"

item:                  # Item để đặt generator
  material: END_PORTAL_FRAME
  name: "&6⚙ Generator"
  lore:
    - "&7Đặt xuống để kích hoạt"
    - "&7Đếm ngược: &e{countdown}s"
  custom-model-data: 0

output:                # Vật phẩm sinh ra sau mỗi countdown
  material: PAPER
  amount: 1
  name: ""
  lore: []

gui:
  title: "&8⚙ Generator"
  size: 27
  status-counting: "&e⏳ Counting down"
  background:
    material: GRAY_STAINED_GLASS_PANE
    name: " "
  slots:
    info:
      slot: 11
      material: END_PORTAL_FRAME
      name: "&6⚙ Generator"
      lore:
        - "&7Status: {status}"
        - "&7Time left: &e{seconds}s"
        - "&7Placed by: &e{player}"
    collect:
      slot: 15
      material: CHEST
      name: "&aCollect Items"
      lore:
        - "&7Stored: &e{stored} items"
        - "&7Click to collect!"
      action: collect
```

### Placeholders trong GUI

| Placeholder | Mô tả |
|-------------|-------|
| `{seconds}` | Thời gian đếm ngược còn lại |
| `{status}` | Trạng thái hiện tại |
| `{stored}` | Tổng số vật phẩm đang chờ nhận |
| `{player}` | Tên người đặt generator |

### Cách Hoạt Động

1. Player đặt block generator trong đảo của mình
2. Hologram xuất hiện phía trên block, bắt đầu đếm ngược
3. Hết countdown → sinh 1 output item vào kho → **restart countdown ngay lập tức** (lặp vô tận)
4. Player chuột phải vào block → mở GUI, thấy số item đang chờ
5. Click nút **Collect** → nhận tất cả item (countdown không bị ảnh hưởng)
6. Player phá block → hologram xóa, item generator drop lại

### Điều Kiện

- ✅ Cần permission `leafskyblockcore.generator.use` để đặt
- ✅ Cần permission `leafskyblockcore.generator.break` hoặc `generator.admin` để phá
- ✅ Phải đặt trong **đảo của bạn** (SuperiorSkyblock2)
- ✅ Không vượt quá `max-per-island`

### Sau Khi Server Restart

- Generator đang đếm → restore đúng thời gian còn lại (trừ thời gian offline)
- Nếu thời gian offline > thời gian còn lại → sinh item luôn rồi restart countdown

---

## 💬 Messages Config

**File:** `messages.yml` — Hỗ trợ **MiniMessage format**

```yaml
general:
  no-permission: "<red>You don't have permission to use this command!"
  player-not-found: "<red>Player not found!"

crops-tracker:
  your-points: "<green>Your crops points: <yellow>{points}"
  player-points: "<green>{player}'s crops points: <yellow>{points}"

generator:
  not-own-island: "<red>You can only place generators on your own island!"
  give-success: "<green>Gave <yellow>{amount}x Generator</yellow> to <yellow>{player}</yellow>!"
  max-reached: "<red>You have reached the maximum number of generators on this island!"

reload:
  all: "<green>Reloaded all configs!"
  generator: "<green>Reloaded generator config!"
```

[📖 MiniMessage Format Guide](https://docs.advntr.dev/minimessage/format.html)

---

## 🎨 Ví Dụ Sử Dụng

### Cho Players

```bash
/lc cropstracker
/lc cropstracker check Steve
```

### Cho Admins

```bash
/lc cropstracker set Steve 10000
/lc cropstracker add Steve 500
/lc cropstracker reset Steve
/lc generator give Steve 3
/lc reload
/lc reload generator
```

### Tạo Leaderboard Crops

```yaml
lines:
  - "&6&lTOP FARMERS"
  - "&e1. %leafskyblockcore_crops_top_1_name%: &a%leafskyblockcore_crops_top_1_points%"
  - "&e2. %leafskyblockcore_crops_top_2_name%: &a%leafskyblockcore_crops_top_2_points%"
  - "&e3. %leafskyblockcore_crops_top_3_name%: &a%leafskyblockcore_crops_top_3_points%"
  - ""
  - "&7Your rank: &e#%leafskyblockcore_crops_rank%"
  - "&7Your points: &e%leafskyblockcore_crops_points%"
```

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

### Generator không hoạt động?
- ✅ Kiểm tra đã cài DecentHolograms chưa
- ✅ Kiểm tra permission `leafskyblockcore.generator.use`
- ✅ Kiểm tra bạn đang đứng trong đảo của mình

### GUI không mở?
- ✅ Chuột phải vào đúng block generator (không phải block thường)
- ✅ Kiểm tra `gui.size` là bội số của 9 (9, 18, 27, 36, 45, 54)

### Placeholder không hoạt động?
- ✅ Cài đặt PlaceholderAPI
- ✅ Reload PlaceholderAPI: `/papi reload`
- ✅ Test: `/papi parse me %leafskyblockcore_crops_points%`

### Database bị lỗi?
- ✅ Kiểm tra file `data.db` có tồn tại không
- ✅ Kiểm tra quyền write vào folder plugin
- ✅ Backup và xóa `data.db` để tạo mới

---

## 📝 Changelog

### Version 1.1
- ✨ Generator GUI - chuột phải để xem thông tin và nhận vật phẩm
- ✨ Output item - generator tự động sinh vật phẩm sau mỗi countdown
- ✨ Generator tự động restart countdown sau khi sinh vật phẩm
- ✨ GUI live update - đếm ngược cập nhật realtime trong GUI
- ✨ Generator give command
- 🔧 Fix MONITOR priority bug trong BlockPlaceEvent/BlockBreakEvent
- 🔧 Fix hologram trùng tên khi restore sau restart
- 🔧 Fix NPE world unload trong async lambda
- 🔧 Fix secondsLeft stale khi restore
- 🔧 Fix holoName collision giữa các world
- 🔧 Fix hardcode material trong listener

### Version 1.0
- ✨ Crops Tracker system
- ✨ Generator system với DecentHolograms countdown
- ✨ Tích hợp SuperiorSkyblock2
- ✨ PlaceholderAPI support
- ✨ SQLite database với HikariCP
- ✨ MiniMessage format support
- ✨ Config system (messages, permissions, crops, generator)

---

## 📜 License

Copyright © 2024 ipapervn. All rights reserved.

---

## 🙏 Credits

- **Paper Team** - Paper API
- **PlaceholderAPI** - Placeholder support
- **SuperiorSkyblock2** - Island management
- **DecentHolograms** - Hologram support
- **HikariCP** - Database connection pool
- **Kyori Adventure** - MiniMessage format

---

<div align="center">

**Made with ❤️ for Skyblock Community**

⭐ **Nếu thích plugin, hãy cho 1 star!** ⭐

[⬆ Back to top](#-leafskyblockcore)

</div>
