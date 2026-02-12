# TreasureLife

A lives system mod designed for Minecraft NeoForge servers./一个为 Minecraft NeoForge 服务器设计的命系统模组。

<details>
<summary>English</summary>

## Features

- **Initial Lives**: Each player starts with 10 lives (configurable)
- **Daily Claim**: Players receive 1 life when logging in daily (configurable)
- **Heart Item**: Use heart items to gain lives
- **Death Penalty**: Lose 1 life on death
- **Spectator Mode**: Players with 0 lives enter spectator mode, return to survival when gaining lives
- **Commands**: Full command support for management

### Commands

| Command | Permission | Description |
|---------|------------|-------------|
| `/lives` | All | View your lives |
| `/treasurelife lives` | OP | View own or others' lives |
| `/treasurelife set <player> <amount>` | OP | Set player's lives |
| `/treasurelife add <player> <amount>` | OP | Add lives to player |
| `/treasurelife remove <player> <amount>` | OP | Remove lives from player |
| `/treasurelife give <player> <amount>` | OP | Give heart items to player |
| `/tl` | OP | Shortcut for `/treasurelife` |

### Configuration

Located in `config/treasurelife-common.toml`:

```toml
initialLives=10
maxLives=20
enableDailyClaim=true
```

</details>

## 功能特性

- **初始命数**: 每个玩家初始拥有 10 条命（可配置）
- **每日领取**: 每天首次登录可获得一条命（可配置）
- **Heart 物品**: 使用心形物品可获得一条命
- **死亡惩罚**: 死亡时扣除一条命
- **旁观模式**: 命耗尽后死亡会进入旁观者模式，获得命后自动转为生存模式
- **指令支持**: 完整的指令系统方便管理

### 指令

| 指令 | 权限 | 说明 |
|------|------|------|
| `/lives` | 所有人 | 查看自己的命数 |
| `/treasurelife lives` | OP | 查看自己或他人命数 |
| `/treasurelife set <玩家> <数量>` | OP | 设置玩家命数 |
| `/treasurelife add <玩家> <数量>` | OP | 增加玩家命数 |
| `/treasurelife remove <玩家> <数量>` | OP | 减少玩家命数 |
| `/treasurelife give <玩家> <数量>` | OP | 给予玩家心物品 |
| `/tl` | OP | `/treasurelife` 的简写 |

### 配置文件

位于 `config/treasurelife-common.toml`:

```toml
initialLives=10
maxLives=20
enableDailyClaim=true
```
---

MIT License
