# 🍃 LeafSkyblockCore

> **Plugin core cho server Skyblock với hệ thống tracking nông sản, generator và custom MOTD**

---

## 📋 Tổng Quan

LeafSkyblockCore là plugin core được thiết kế đặc biệt cho server Skyblock, cung cấp hệ thống theo dõi điểm nông sản tự động, hệ thống mùa màng, generator block tự động sinh vật phẩm với hologram đếm ngược, GUI quản lý và custom MOTD.

### ✨ Tính Năng Chính

- 🌾 **Crops Tracker** - Theo dõi điểm khi thu hoạch nông sản
- 🍂 **Season Farming** - Hệ thống mùa màng, chỉ tính điểm nông sản đúng mùa
- ⚙️ **Generator** - Block tự động sinh vật phẩm theo chu kỳ, hologram đếm ngược
- 🖥️ **Generator GUI** - Chuột phải vào generator để xem thông tin và nhận vật phẩm
- 📡 **Custom MOTD** - Tùy chỉnh MOTD server list, hỗ trợ MiniMessage gradient, hex color
- 🏝️ **Tích hợp SuperiorSkyblock2** - Chỉ tính điểm và đặt generator trong đảo của bạn
- 📊 **PlaceholderAPI Support** - Hiển thị điểm, mùa trên scoreboard, tab, chat
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
| `%leafskyblockcore_season%` | Tên hiển thị mùa hiện tại (hỗ trợ MiniMessage) |

---

## 📁 Cấu Trúc Files

```
plugins/LeafSkyblockCore/
├── data.db
├── messages.yml
├── permissions.yml
├── motd.yml
├── seasons-state.yml
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

seasons:
  enabled: true
  duration: "30m"
  order: [SPRING, SUMMER, AUTUMN, WINTER]
  SPRING:
    display: "<green>Mùa Xuân"
    crops: [WHEAT, CARROTS, POTATOES]
  SUMMER:
    display: "<yellow>Mùa Hè"
    crops: [MELON, PUMPKIN, SWEET_BERRY_BUSH]
  AUTUMN:
    display: "<gold>Mùa Thu"
    crops: [BEETROOTS, NETHER_WART, COCOA]
  WINTER:
    display: "<aqua>Mùa Đông"
    crops: []
```

### Cách Hoạt Động

- Chỉ tính khi nông sản **chín hoàn toàn**
- Chỉ tính khi đứng trong **đảo của bạn**
- Không tính fortune drops (1 lần mỗi block)
- Chỉ tính điểm nông sản **đúng mùa** — sai mùa thu hoạch bình thường nhưng không được điểm

---

## 🍂 Season Farming

### Cách Hoạt Động

- Mùa tự động đổi sau mỗi `duration` (mặc định 30 phút)
- Mỗi mùa chỉ tính điểm cho các nông sản được cấu hình trong `seasons.<TÊN_MÙA>.crops`
- `WINTER` mặc định không có crop nào → không tính điểm mùa đông
- Trạng thái mùa được lưu vào `seasons-state.yml` — **restart server không mất mùa**, thời gian tiếp tục đếm

### Duration Format

| Format | Ví dụ | Kết quả |
|--------|-------|---------|
| Giây | `30s` | 30 giây |
| Phút | `5m` | 5 phút |
| Giờ | `1h` | 1 giờ |

### Tắt Season Farming

```yaml
seasons:
  enabled: false
```

Khi `enabled: false`, tất cả nông sản trong `crops` đều được tính điểm bình thường.

---

## 📡 Custom MOTD

### Config (`motd.yml`)

```yaml
enabled: true
line1: "<gradient:green:aqua><bold>LeafSkyblock</bold></gradient> <gray>| 1.21"
line2: "<yellow>✦ <white>Chào mừng bạn đến với server! <yellow>✦"
```

### Cách Hoạt Động

- Hỗ trợ đầy đủ **MiniMessage format** — gradient, hex color (`<#ff6600>`), bold, italic, v.v.
- 2 dòng MOTD độc lập, mỗi dòng config riêng
- `enabled: false` để tắt, server dùng MOTD mặc định từ `server.properties`
- `/lc reload` reload MOTD ngay lập tức, không cần restart

---

## ⚙️ Generator

### Config (`generator/config.yml`)

```yaml
countdown: "15s"
max-per-island: 1

hologram:
  lines:
    counting: "<yellow>⏳ <gold>{seconds}s"
    stored: "<gray>Stored: <yellow>{stored}"

item:
  material: END_PORTAL_FRAME
  name: "<gold>⚙ Generator"
  lore:
    - "<gray>Đặt xuống để kích hoạt"
    - "<gray>Đếm ngược: <yellow>{countdown}s"
  custom-model-data: 0

output:
  material: PAPER
  amount: 1
  name: ""
  lore: []

gui:
  title: "<dark_gray>⚙ Generator"
  size: 27
  status-counting: "<yellow>⏳ Counting down"
  background:
    material: GRAY_STAINED_GLASS_PANE
    name: " "
  slots:
    info:
      slot: 11
      material: END_PORTAL_FRAME
      name: "<gold>⚙ Generator"
      lore:
        - "<gray>Status: {status}"
        - "<gray>Time left: <yellow>{seconds}s"
        - "<gray>Placed by: <yellow>{player}"
    collect:
      slot: 15
      material: CHEST
      name: "<green>Collect Items"
      lore:
        - "<gray>Stored: <yellow>{stored} items"
        - "<gray>Click to collect!"
      action: collect
```

> **Lưu ý:** Config dùng **MiniMessage format**. Hologram lines dùng **legacy `&` format** (DecentHolograms).

### Countdown Format

Giống Season duration: `15s`, `5m`, `1h`.

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
  collected: "<green>Collected <yellow>{amount}</yellow> items from the generator!"

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
- Kiểm tra nông sản có trong danh sách mùa hiện tại không (`%leafskyblockcore_season%`)

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
- Test mùa: `/papi parse me %leafskyblockcore_season%`

---

## 📝 Changelog

### Version 1.3
- ✨ Custom MOTD — hỗ trợ MiniMessage gradient, hex color, 2 dòng config riêng
- ✨ Tên hiển thị mùa có màu sắc (`seasons.<MÙA>.display`)

### Version 1.2
- ✨ Season Farming — hệ thống mùa màng, mỗi mùa chỉ tính điểm nông sản được cấu hình
- ✨ Placeholder `%leafskyblockcore_season%`
- ✨ Persist trạng thái mùa qua restart (`seasons-state.yml`)
- ✨ Duration format `15s`, `5m`, `1h` cho cả season và generator countdown

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
