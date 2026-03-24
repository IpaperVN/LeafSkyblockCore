# 🍃 LeafSkyblockCore

> **Plugin core cho server Skyblock với hệ thống tracking nông sản và generator**

---

## 📋 Tổng Quan

LeafSkyblockCore là plugin core được thiết kế đặc biệt cho server Skyblock, cung cấp hệ thống theo dõi điểm nông sản tự động, generator block tự động sinh vật phẩm với hologram đếm ngược và GUI quản lý.

### ✨ Tính Năng Chính

- 🌾 **Crops Tracker** - Theo dõi điểm khi thu hoạch nông sản
- ⚙️ **Generator** - Block tự động sinh vật phẩm theo chu kỳ, hologram đếm ngược
- 🖥️ **Generator GUI** - Chuột phải vào generator để xem thông tin và nhận vật phẩm
- 🏝️ **Tích hợp SuperiorSkyblock2** - Chỉ tính điểm và đặt generator trong đảo của bạn
- 📊 **PlaceholderAPI Support** - Hiển thị điểm trên scoreboard, tab, chat
- 💾 **SQLite Database** - Lưu trữ dữ liệu an toàn với HikariCP
- 🎨 **MiniMessage Format** - Hỗ trợ màu sắc, gradient, hover, click events

---

## 📦 Yêu Cầu

| Plugin | Bắt buộc | Phiên bản |
|--------|----------|-----------|
| **Paper** | ✅ | 1.20.x - 1.21.x |
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
| `/lc cropstracker set <player> <points>` | Set điểm | `leafskyblockcore.cropstracker.set` |
| `/lc cropstracker add <player> <points>` | Thêm điểm | `leafskyblockcore.cropstracker.add` |
| `/lc cropstracker reset <player>` | Reset điểm | `leafskyblockcore.cropstracker.reset` |

### ⚙️ Generator

| Command | Mô tả | Permission |
|---------|-------|------------|
| `/lc generator give <player> [amount]` | Cho player item generator | `leafskyblockcore.generator.give` |

### 🔄 Reload

| Command | Mô tả | Permission |
|---------|-------|------------|
| `/lc reload` | Reload tất cả configs | `leafskyblockcore.reload` |
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
```

### Generator

```yaml
leafskyblockcore.generator.use         # Đặt generator block
leafskyblockcore.generator.break       # Phá generator block
leafskyblockcore.generator.admin       # Bypass mọi check
leafskyblockcore.generator.give        # Cho item generator (Admin)
```

### Reload

```yaml
leafskyblockcore.reload                # Reload configs
```

---

## 📊 PlaceholderAPI

| Placeholder | Mô tả |
|-------------|-------|
| `%leafskyblockcore_crops_points%` | Điểm nông sản của bạn |
| `%leafskyblockcore_crops_rank%` | Hạng của bạn |
| `%leafskyblockcore_crops_top_X_name%` | Tên người chơi top X (1-10) |
| `%leafskyblockcore_crops_top_X_points%` | Điểm người chơi top X (1-10) |

---

## 📁 Cấu Trúc Files

```
plugins/LeafSkyblockCore/
├── data.db
├── messages.yml
├── permissions.yml
├── crops-tracker/
│   └── config.yml
└── generator/
    └── config.yml
```

---

## 🌾 Crops Tracker

### Config (`crops-tracker/config.yml`)

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

- Chỉ tính khi nông sản **chín hoàn toàn**
- Chỉ tính khi đứng trong **đảo của bạn**
- Không tính fortune drops (1 lần mỗi block)

---

## ⚙️ Generator

### Config (`generator/config.yml`)

```yaml
countdown: "15s"
max-per-island: 1

hologram:
  lines:
    counting: "&e⏳ &6{seconds}s"

item:
  material: END_PORTAL_FRAME
  name: "&6⚙ Generator"
  lore:
    - "&7Đặt xuống để kích hoạt"
    - "&7Đếm ngược: &e{countdown}s"
  custom-model-data: 0

output:
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
| `{stored}` | Số vật phẩm đang chờ nhận |
| `{player}` | Tên người đặt generator |

### Cách Hoạt Động

1. Player đặt block generator trong đảo
2. Hologram đếm ngược xuất hiện phía trên
3. Hết countdown → sinh output item vào kho → **restart countdown ngay lập tức**
4. Chuột phải vào block → mở GUI, thấy số item đang chờ và thời gian còn lại (live update)
5. Click **Collect** → nhận tất cả item, countdown tiếp tục không bị ảnh hưởng
6. Phá block → hologram xóa, item generator drop lại

### Sau Khi Server Restart

- Restore đúng thời gian còn lại (trừ thời gian offline)
- Nếu thời gian offline > thời gian còn lại → sinh item luôn rồi restart

---

## 💬 Messages (`messages.yml`)

Hỗ trợ **MiniMessage format** — [📖 Xem hướng dẫn](https://docs.advntr.dev/minimessage/format.html)

```yaml
general:
  no-permission: "<red>You don't have permission!"
  player-not-found: "<red>Player not found!"
  invalid-number: "<red>Invalid number!"

crops-tracker:
  your-points: "<green>Your crops points: <yellow>{points}"
  player-points: "<green>{player}'s crops points: <yellow>{points}"

generator:
  not-own-island: "<red>You can only place generators on your own island!"
  give-success: "<green>Gave <yellow>{amount}x Generator</yellow> to <yellow>{player}</yellow>!"
  max-reached: "<red>You have reached the maximum number of generators!"

reload:
  all: "<green>Reloaded all configs!"
  generator: "<green>Reloaded generator config!"
```

---

## 🔧 Troubleshooting

**Crops Tracker không tính điểm?**
- Kiểm tra nông sản đã chín chưa
- Kiểm tra đang đứng trong đảo của mình
- Kiểm tra SuperiorSkyblock2 đã cài chưa

**Generator không hoạt động?**
- Kiểm tra DecentHolograms đã cài chưa
- Kiểm tra permission `leafskyblockcore.generator.use`
- Kiểm tra đang đứng trong đảo của mình

**GUI không mở?**
- Chuột phải vào đúng block generator
- Kiểm tra `gui.size` là bội số của 9

**Placeholder không hoạt động?**
- Cài PlaceholderAPI
- Test: `/papi parse me %leafskyblockcore_crops_points%`

---

## 📝 Changelog

### Version 1.1
- ✨ Generator GUI với live update
- ✨ Output item sinh ra sau mỗi countdown
- ✨ Countdown tự restart sau khi sinh item
- ✨ Command `/lc generator give`
- 🔧 Nhiều bug fixes (MONITOR priority, hologram collision, NPE, stale timer)

### Version 1.0
- ✨ Crops Tracker
- ✨ Generator với hologram đếm ngược
- ✨ SuperiorSkyblock2 integration
- ✨ PlaceholderAPI support
- ✨ SQLite + HikariCP
- ✨ MiniMessage format

---

## 📜 License

Copyright © 2024 ipapervn. All rights reserved.

---

<div align="center">

**Made with ❤️ for Skyblock Community**

[⬆ Back to top](#-leafskyblockcore)

</div>
