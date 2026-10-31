# Epic Fight - Extended Datapacks (EDP)

<div align="center">

![Extended Datapacks](https://img.shields.io/badge/Mod-Extended%20Datapacks-blueviolet)
![Minecraft](https://img.shields.io/badge/Minecraft-1.20.1%20%7C%201.21.1-green)
![Forge](https://img.shields.io/badge/Forge-47.4.0-orange)
![NeoForge](https://img.shields.io/badge/NeoForge-21.1.220-red)
![License](https://img.shields.io/badge/License-ARR-critical)

**Extended Datapacks** is an extension for Epic **Fight Mod**. It empowers datapack, resourcepack & registry (Data-Driven) for creators to implement combat mechanics, skills, animations, capability, custom visual effects, logic systems via JSON configurations & more!.

>This project will act as a translation layer, any addon or mod you design using it will be compatible anywhere this exists.

[Curseforge](https://www.curseforge.com/minecraft/mc-mods/epic-fight-edp) • [Wiki](https://github.com/Sleys-g/ExtendedDatapacks/wiki)

</div>

## 📌 Requirements

| Dependency             | Versions Compatibility   | Notes                                                     |
|------------------------|--------------------------|-----------------------------------------------------------|
| **Epic Fight Mod**     | Required (All versions)  |                                                           |
| **Lazy Utilities**     | Required **(Pre v2.0)**  | Legacy library.                                           |
| **SL Library / SLM**   | Required **(Post v2.0)** | New libraries (Epic Fight & Shaders modules).             |
| **Weapons Of Miracle** | Optional                 | Unlocks Passive Skills & Innate Skills Utilities for WoM. |
| **Combat Evolution**   | Optional                 | Unlocks Json Execution Animation Registry.                |

## 🧭 Project Roadmap

Take a look at what is currently implemented and what is coming next!

- [x] **Advanced Animation JSON Registry v2.3** ✔️
- [x] **Weapons Passive Particles v2.4** ✔️
- [x] **Innate Skills II - Payloads v2.5** ✔️
  - [x] **Data Packets** ✔️
    - [x] **Write Packets** ✔️
      - [x] **Arithmetic Instruction** ✔️
      - [x] **Logical Instruction** ✔️
      - [x] **String Instruction** ✔️
    - [x] **Read Packets** ✔️
      - [x] **Numeric Comparator** ✔️
      - [x] **String Comparator** ✔️
  - [x] **Additional Innate Skills** ✔️
    - [x] **Conditional Data Innate Skill** ✔️
    - [x] **Sequential Innate Skill** ✔️
    - [x] **Per Combo Innate Skill** ✔️
    - [x] **Combo Innate Skill** ✔️
    - [x] **Conditional Holdable Innate Skill** ✔️
  - [x] **More Animation Events** ✔️
    - [x] **Whiter Ghost Entity Event** ✔️
    - [x] **Laser Damage Event** ✔️
      - [x] **Laser Target Event** ✔️
      - [x] **Laser Vertical Target Event** ✔️
      - [x] **Laser Joint Target Event** ✔️
      - [x] **Laser Vertical Event** ✔️
      - [x] **Laser Joint Vertical Event** ✔️
      - [x] **Laser Line World Event** ✔️
      - [x] **Laser Floor World Event** ✔️
    - [x] **Summon Entity Event** ✔️
    - [x] **Write Data** ✔️
      - [x] **Synced Data Write Event** ✔️
      - [x] **Data Write Event** ✔️
    - [x] **Read Data** ✔️
        - [x] **Synced Direct Read Write Event** ✔️
        - [x] **Synced Branched Read Write Event** ✔️
        - [x] **Direct Read Write Event** ✔️
        - [x] **Branched Read Write Event** ✔️
    - [x] **Entity Pairing Event** ✔️
      - [x] **Flash White Pair Event** ✔️
      - [x] **Scape Emergence Pair Event** ✔️
      - [x] **Entity Pairing Event** ✔️
    - [x] **Item Data Read Event** ✔️
      - [x] **Item String Data Read Event** ✔️
      - [x] **Item Number Data Read Event** ✔️
  - [x] **More Animation Static Property** ✔️
    - [x] **Animation Process Events** ✔️
      - [x] **On Tick Events** ✔️
      - [x] **On Begin Events** ✔️
      - [x] **On End Events** ✔️

### 🏗️ Settling debts
- [x] **Taskable Animation Event** ✔️
    - [x] **Taskable Laser Line World Damage** ✔️
    - [x] **Taskable Laser Floor World Damage** ✔️
    - [x] **Taskable Laser Shape World Damage** ✔️
    - [x] **Taskable Entity Clone** ✔️
    - [x] **Taskable Entity Animated** ✔️
- [x] **Timed Innate Skill** ✔️
    - [x] **Timed Innate Skill** ✔️
    - [x] **Timed Advanced Innate Skill** ✔️
    - [x] **Controlable Events** ✔️
        - [x] **Combat** ✔️
            - [x] **Animation Phase** ✔️
            - [x] **Anticipation Animation** ✔️
            - [x] **Attack Animation** ✔️
            - [x] **Recovery Animation** ✔️
            - [x] **Attack Entity** ✔️
            - [x] **Attack Speed** ✔️
            - [x] **Deal Pre Damage** ✔️
            - [x] **Deal Post Damage** ✔️
            - [x] **Deal Pre Player Damage** ✔️
            - [x] **Deal Post Player Damage** ✔️
            - [x] **Take Pre Damage** ✔️
            - [x] **Take Post Damage** ✔️
            - [x] **Living Death** ✔️
            - [x] **Living Kill** ✔️
            - [x] **Living Incoming Deal Damage** ✔️
            - [x] **Living Incoming Player Deal Damage** ✔️
            - [x] **Living Incoming Take Damage** ✔️
            - [x] **Living Knockback** ✔️
            - [x] **Living Shield Block** ✔️
            - [x] **Living Shield Blocked** ✔️
            - [x] **Living Use Totem** ✔️
            - [x] **Living Trigger Totem** ✔️
        - [x] **IA** ✔️
            - [x] **Target Entity** ✔️
            - [x] **Client Target Entity** ✔️
            - [x] **Server Target Entity** ✔️
        - [x] **Loot** ✔️
            - [x] **Living Drops** ✔️
            - [x] **Living Experience Drop** ✔️
        - [x] **Movement** ✔️
            - [x] **Living Breath** ✔️
            - [x] **Living Fall** ✔️
            - [x] **Movement Input** ✔️
            - [x] **Player Use Input** ✔️
            - [x] **Player Client Use Input** ✔️
            - [x] **Player Server Use Input** ✔️
        - [x] **Status** ✔️
            - [x] **Added Mob Effect** ✔️
            - [x] **Applicable Mob Effect** ✔️
            - [x] **Expired Mob Effect** ✔️
            - [x] **Remove Mob Effect** ✔️
            - [x] **Living Heal** ✔️
        - [x] **Ticks**
            - [x] **Tick** ✔️
            - [x] **Client Tick** ✔️
            - [x] **Server Tick** ✔️
          
> Major updates/content releases will be temporarily paused; minor fixes will be prioritized. The update pool is mostly complete. Once "Innate Skills II & Payloads" is finished, updates will be frozen.

## 🔭 Next Updates
| Update                                    | Priority    | version |
|-------------------------------------------|-------------|---------|
| Camera Packet on Animations               | ⭕ Very Low  | v2.6    |
| Weapons Passive Skill                     | 🔺 High     | v2.7    |
| Skill Books (Dodge / Guard / Passive)     | 🔶 Medium   | v2.8    |
| Custom Skill Slots, Slots, Modified Slots | 🔻 Low      | v2.9    |

> The release disposition can vary based on priority, if the entire table has a low priority and then one goes to medium or high, it will become the next update

## 📌 Project Status

As version **3.0** approaches, the focus will be on consolidating, stabilizing, and freezing the system to ensure stability before beginning UI development and facilitating the design and creation of datapacks, assetpacks, and add-ons.
Once version **3.0** is reached, the project will be renamed from **"Epic Fight: Extended Datapacks"** to **"Epic Fight: Extended Development Platform"**.
