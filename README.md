# 🍃 LeafSkyblockCore

> **Plugin core cho server Skyblock với hệ thống tracking nông sản, generator, MobCoins, khung giờ đặc biệt và khai thác tùy chỉnh**

---

## 📋 Tổng Quan

LeafSkyblockCore là plugin core được thiết kế đặc biệt cho server Skyblock, cung cấp hệ thống theo dõi điểm nông sản tự động, hệ thống mùa màng, generator block tự động sinh vật phẩm, khung giờ đặc biệt và hệ thống khai thác quặng tùy chỉnh theo phong cách Hypixel.

### ✨ Tính Năng Chính

- 🌾 **Crops Tracker** - Theo dõi điểm khi thu hoạch nông sản
- 🍂 **Season Farming** - Hệ thống mùa màng, chỉ tính điểm nông sản đúng mùa
- ⚙️ **Generator** - Block tự động sinh vật phẩm theo chu kỳ, hologram đếm ngược
- 🖥️ **Generator GUI** - Chuột phải vào generator để xem thông tin và nhận vật phẩm
- 📡 **Custom MOTD** - Tùy chỉnh MOTD server list, hỗ trợ MiniMessage gradient, hex color
- 🪙 **MobCoins** - Hệ thống tiền tệ, quản lý qua command, tên hiển thị config được
- ⏰ **TimeFrame** - Khung giờ đặc biệt theo giờ Việt Nam, broadcast tự động
- ⛏️ **Custom Mining** - Hệ thống khai thác quặng Nexo với Breaking Power, Mining Speed, Fortune
- 🏝️ **Tích hợp SuperiorSkyblock2** - Chỉ tính điểm và đặt generator trong đảo của bạn
- 📊 **PlaceholderAPI Support** - Hiển thị điểm, mùa, mobcoins, khung giờ trên scoreboard
- 💾 **SQLite Database** - Lưu trữ dữ liệu an toàn với HikariCP
- 🎨 **MiniMessage Format** - Hỗ trợ màu sắc, gradient, hover, click events

---

## 📦 Yêu Cầu

| Plugin | Bắt buộc | Phiên bản |
|--------|----------|-----------|
| **Paper** | ✅ | 1.21.x |
| **Java** | ✅ | 21+ |
| **SuperiorSkyblock2** | ⚠️ | Latest (Softdepend) |
| **DecentHolograms** | ⚠️ | Latest (Softdepend) |
| **WorldGuard** | ⚠️ | Latest (Softdepend) |
| **Nexo** | ⚠️ | 1.22+ (Softdepend) |
| **MMOItems** | ⚠️ | 6.10+ (Softdepend) |
| **PlaceholderAPI** | ❌ | Latest (Khuyến nghị) |

---

## 🚀 Cài Đặt

1. **Download** plugin từ releases
2. **Đặt** file `.jar` vào folder `plugins/`
3. **Cài đặt** các plugin yêu cầu
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

### 🪙 MobCoins

| Command | Mô tả | Permission |
|---------|-------|------------|
| `/mcoins` | Xem số coins của bạn | - |
| `/mcoins give <player> <amount>` | Cho coins | `leafskyblockcore.mobcoins.give` |
| `/mcoins take <player> <amount>` | Lấy coins | `leafskyblockcore.mobcoins.take` |
| `/mcoins add <player> <amount>` | Thêm coins (alias give) | `leafskyblockcore.mobcoins.give` |
| `/mcoins reset <player>` | Reset coins | `leafskyblockcore.mobcoins.reset` |

**Aliases:** `/mcoins`, `/mobcoins`

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

### MobCoins

```yaml
leafskyblockcore.mobcoins.give         # Cho/thêm coins (Admin)
leafskyblockcore.mobcoins.take         # Lấy coins (Admin)
leafskyblockcore.mobcoins.reset        # Reset coins (Admin)
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
| `%leafskyblockcore_crops_points_formatted%` | Điểm nông sản (1K, 1M, 1B) |
| `%leafskyblockcore_crops_rank%` | Hạng của bạn |
| `%leafskyblockcore_crops_top_X_name%` | Tên người chơi top X (1-10) |
| `%leafskyblockcore_crops_top_X_points%` | Điểm người chơi top X (1-10) |
| `%leafskyblockcore_crops_top_X_points_formatted%` | Điểm top X (1K, 1M, 1B) |
| `%leafskyblockcore_season%` | Tên hiển thị mùa hiện tại (hỗ trợ MiniMessage) |
| `%leafskyblockcore_mobcoins%` | Số MobCoins của bạn |
| `%leafskyblockcore_mobcoins_formatted%` | Số MobCoins (1K, 1M, 1B) |
| `%leafskyblockcore_timeframe_active%` | `true` nếu đang trong khung giờ đặc biệt |
| `%leafskyblockcore_timeframe_status%` | Text trạng thái khung giờ (config được) |

---

## 📁 Cấu Trúc Files

```
plugins/LeafSkyblockCore/
├── data.db
├── messages.yml
├── permissions.yml
├── motd.yml
├── mobcoins.yml
├── timeframe.yml
├── seasons-state.yml
├── crops-tracker/
│   └── config.yml
├── generator/
│   └── config.yml
└── mining/
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
- Trạng thái mùa được lưu vào `seasons-state.yml` — **restart server không mất mùa**

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

---

## 🪙 MobCoins

### Config (`mobcoins.yml`)

```yaml
display-name: "MobCoins"
```

---

## 📡 Custom MOTD

### Config (`motd.yml`)

```yaml
enabled: true
line1: "<gradient:green:aqua><bold>LeafSkyblock</bold></gradient> <gray>| 1.21"
line2: "<yellow>✦ <white>Chào mừng bạn đến với server! <yellow>✦"
```

- `enabled: false` để tắt, server dùng MOTD mặc định từ `server.properties`
- `/lc reload` reload MOTD ngay lập tức

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
```

### Cách Hoạt Động

1. Player đặt block generator trong đảo
2. Hologram đếm ngược xuất hiện phía trên
3. Hết countdown → sinh output item → **restart countdown ngay lập tức**
4. Chuột phải vào block → mở GUI, thấy số item và thời gian còn lại
5. Click **Collect** → nhận tất cả item
6. Phá block → hologram xóa, item generator drop lại
7. Sau restart server → restore đúng thời gian còn lại

---

## ⏰ TimeFrame (Khung Giờ Đặc Biệt)

### Config (`timeframe.yml`)

```yaml
enabled: true

# Định dạng: "HH:mm-HH:mm" theo giờ Việt Nam (UTC+7)
# Hỗ trợ overnight: "22:00-02:00"
slots:
  - "20:00-22:00"
  - "08:00-10:00"

broadcast-message: "<gold>⏰ <yellow>Khung giờ đặc biệt đã bắt đầu!"
broadcast-end-message: "<gray>⏰ <yellow>Khung giờ đặc biệt đã kết thúc!"

placeholder-active: "<green>Đang diễn ra"
placeholder-inactive: "<red>Không hoạt động"
```

### Cách Hoạt Động

- Tự động broadcast lên toàn server khi **bắt đầu** và **kết thúc** khung giờ
- Hỗ trợ nhiều khung giờ trong ngày
- Hỗ trợ khung giờ qua đêm (ví dụ `22:00-02:00`)
- Tất cả thời gian theo **giờ Việt Nam (UTC+7)**
- `/lc reload` reload config ngay lập tức

### Placeholders

| Placeholder | Giá trị |
|-------------|---------|
| `%leafskyblockcore_timeframe_active%` | `true` / `false` |
| `%leafskyblockcore_timeframe_status%` | Text từ `placeholder-active` hoặc `placeholder-inactive` |

---

## ⛏️ Custom Mining

### Yêu Cầu

- **WorldGuard** — tạo region khai thác
- **Nexo** — custom ore block
- **MMOItems** — pickaxe với stat `breaking_power`

### Config (`mining/config.yml`)

```yaml
# Tên các WorldGuard region cho phép khai thác
regions:
  - "mining_zone_1"

# Nexo block ID → cấu hình quặng
ores:
  CUSTOM_COAL:
    breaking-power: 1    # Breaking Power tối thiểu để đào
    mining-time: 3       # Thời gian đào (giây), giảm theo Mining Speed
    respawn-time: 30     # Thời gian hồi sinh quặng (giây)
  CUSTOM_DIAMOND:
    breaking-power: 3
    mining-time: 8
    respawn-time: 60
```

### Cách Hoạt Động

1. Admin tạo WorldGuard region, đặt Nexo ore block vào trong
2. Config `breaking-power` cho từng loại quặng trong `mining/config.yml`
3. Tạo pickaxe MMOItems với stat `breaking_power` tương ứng
4. Player vào region, dùng pickaxe đào quặng:
   - Không đủ Breaking Power → **không thể đào**, không có animation
   - Đủ Breaking Power → animation nứt block xuất hiện, đào theo thời gian thực
   - Thời gian đào giảm theo stat `Mining Speed` của pickaxe
5. Đào xong → item rơi thẳng vào **inventory** (không rơi ra đất), inventory đầy mới rơi ra đất
6. Block biến thành **Bedrock** trong thời gian hồi sinh
7. Hết thời gian → quặng hồi sinh tự động

### Fortune

Pickaxe có stat `Fortune` từ MMOItems sẽ nhân số lượng item drop:

| Fortune | Kết quả |
|---------|---------|
| 100 | x1 guaranteed + 0% bonus |
| 200 | x2 guaranteed |
| 250 | x2 guaranteed + 50% chance x3 |
| 300 | x3 guaranteed |

### Silk Touch

Pickaxe có Silk Touch → drop chính block Nexo thay vì loot table thông thường.

### MMOItems Stats Cần Thiết

| Stat ID | Mô tả |
|---------|-------|
| `breaking_power` | Sức mạnh phá block (custom stat) |
| `MINING_SPEED` | Tốc độ đào (stat mặc định MMOItems) |
| `FORTUNE` | Nhân drop (stat mặc định MMOItems) |
| `SILK_TOUCH` | Silk touch (stat mặc định MMOItems) |

---

## 💬 Messages (`messages.yml`)

Hỗ trợ **MiniMessage format** — [📖 Xem hướng dẫn](https://docs.advntr.dev/minimessage/format.html)

```yaml
mining:
  not-enough-power: "<red>Bạn cần Breaking Power <yellow>{required} <red>để đào block này! <gray>(Hiện tại: {current})"
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

**TimeFrame không broadcast?**
- Kiểm tra `enabled: true` trong `timeframe.yml`
- Kiểm tra định dạng slot `HH:mm-HH:mm`
- `/lc reload` sau khi sửa config

**Không đào được quặng?**
- Kiểm tra đang đứng trong WorldGuard region đã config
- Kiểm tra Nexo block ID đúng với config `mining/config.yml`
- Kiểm tra pickaxe có stat `breaking_power` đủ
- Kiểm tra WorldGuard, Nexo, MMOItems đã cài chưa

**Placeholder không hoạt động?**
- Cài PlaceholderAPI
- Test: `/papi parse me %leafskyblockcore_crops_points%`
- Test khung giờ: `/papi parse me %leafskyblockcore_timeframe_active%`

---

## 📝 Changelog

### Version 1.6
- ✨ Custom Mining System — khai thác quặng Nexo với Breaking Power, Mining Speed, Fortune
- ✨ Hỗ trợ WorldGuard region cho khu vực khai thác
- ✨ Block hồi sinh sau khi bị đào (Bedrock placeholder)
- ✨ Item drop thẳng vào inventory player

### Version 1.5
- ✨ TimeFrame — khung giờ đặc biệt theo giờ Việt Nam
- ✨ Broadcast tự động khi bắt đầu và kết thúc khung giờ
- ✨ Placeholder `%leafskyblockcore_timeframe_active%` và `%leafskyblockcore_timeframe_status%`
- ✨ Hỗ trợ nhiều khung giờ, overnight range

### Version 1.4
- ✨ MobCoins — hệ thống tiền tệ, command `/mcoins`, tên hiển thị config được
- ✨ Placeholder `%leafskyblockcore_mobcoins%` và `%leafskyblockcore_mobcoins_formatted%`
- ✨ Placeholder formatted cho crops points và top points

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
- 🔧 Nhiều bug fixes

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
