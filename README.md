# 🛡️ ChunkProtect — Plugin para Minecraft 1.20.x

Plugin de protección de chunks con sistema de roles, /home y /spawn para servidores Aternos.

---

## 📁 Estructura del proyecto

```
ChunkProtect/
├── pom.xml
└── src/main/
    ├── java/com/chunkprotect/
    │   ├── ChunkProtectPlugin.java
    │   ├── model/
    │   │   ├── ClaimRole.java
    │   │   └── ClaimedChunk.java
    │   ├── managers/
    │   │   ├── ClaimManager.java
    │   │   ├── HomeManager.java
    │   │   └── SpawnManager.java
    │   ├── commands/
    │   │   ├── ClaimCommand.java
    │   │   ├── UnclaimCommand.java
    │   │   ├── ClaimListCommand.java
    │   │   ├── ClaimInfoCommand.java
    │   │   ├── TrustCommand.java
    │   │   ├── UntrustCommand.java
    │   │   ├── HomeCommand.java
    │   │   ├── SetHomeCommand.java
    │   │   ├── DelHomeCommand.java
    │   │   ├── HomesCommand.java
    │   │   ├── SpawnCommand.java
    │   │   └── SetSpawnCommand.java
    │   └── listeners/
    │       ├── ProtectionListener.java
    │       └── PlayerJoinListener.java
    └── resources/
        ├── plugin.yml
        └── config.yml
```

---

## 🔨 Cómo compilar

### Requisitos
- Java JDK 17+
- Maven
- IntelliJ IDEA (recomendado) o cualquier IDE

### Pasos

1. **Abre IntelliJ IDEA** → `Open` → selecciona la carpeta `ChunkProtect`
2. Maven detectará el `pom.xml` automáticamente
3. En el panel derecho de Maven haz doble click en: `Lifecycle → package`
4. El `.jar` se generará en: `target/ChunkProtect-1.0.0.jar`

### Con consola (si tienes Maven instalado)
```bash
cd ChunkProtect
mvn clean package
```

---

## 🚀 Instalar en Aternos

1. Ve a tu panel de **Aternos** → Sección **Plugins**
2. Haz click en **Subir** (arriba a la derecha)
3. Sube el archivo `ChunkProtect-1.0.0.jar`
4. **Reinicia el servidor**
5. Ve al spawn y usa `/setspawn`

---

## 📋 Comandos

### Protecciones
| Comando | Descripción |
|---------|-------------|
| `/claim <radio>` | Reclama chunks. Radio 0=1chunk, 1=3x3, 2=5x5... |
| `/unclaim` | Libera el chunk donde estás |
| `/claimlist` | Lista tus territorios |
| `/claiminfo` | Info del chunk actual |
| `/trust <jugador> <ROL>` | Añade un jugador (ADMIN, MEMBER o VISITOR) |
| `/untrust <jugador>` | Quita a un jugador de tu territorio |

### Homes
| Comando | Descripción |
|---------|-------------|
| `/home [nombre]` | Ir a tu home (por defecto "home") |
| `/sethome [nombre]` | Guardar posición como home |
| `/delhome [nombre]` | Eliminar un home |
| `/homes` | Ver todos tus homes |

### Spawn
| Comando | Descripción | Permiso |
|---------|-------------|---------|
| `/spawn` | Ir al spawn | Todos |
| `/setspawn` | Establecer el spawn | OP |

---

## 👥 Sistema de Roles

| Rol | Romper/Colocar | Cofres | Entidades | Puertas | Gestionar |
|-----|:--------------:|:------:|:---------:|:-------:|:---------:|
| **OWNER** | ✅ | ✅ | ✅ | ✅ | ✅ |
| **ADMIN** | ✅ | ✅ | ✅ | ✅ | ✅ (solo MEMBER/VISITOR) |
| **MEMBER** | ❌ | ✅ | ✅ | ✅ | ❌ |
| **VISITOR** | ❌ | ❌ | ❌ | ✅ | ❌ |
| **Sin rol** | ❌ | ❌ | ❌ | ❌ | ❌ |

> Los jugadores sin rol pueden pasar por el territorio pero no interactuar con nada.

---

## ⚙️ Configuración (config.yml)

```yaml
max-claims-per-player: 25    # Chunks máximos por jugador
max-claim-radius: 5          # Radio máximo en /claim
max-homes-per-player: 5      # Homes máximos por jugador
teleport-delay: 3            # Segundos de espera antes de tp
```

---

## 🔑 Permisos

| Permiso | Descripción | Default |
|---------|-------------|---------|
| `chunkprotect.claim` | Usar /claim y gestionar territorios | Todos |
| `chunkprotect.home` | Usar /home y /sethome | Todos |
| `chunkprotect.spawn` | Usar /spawn | Todos |
| `chunkprotect.setspawn` | Usar /setspawn | OP |
| `chunkprotect.bypass` | Ignorar todas las protecciones | OP |

---

## 📝 Notas

- Las protecciones se guardan automáticamente en `plugins/ChunkProtect/`
- Los datos persisten entre reinicios del servidor
- Las explosiones (TNT, creepers) no pueden dañar chunks protegidos
- El PvP entre jugadores está permitido incluso en chunks protegidos
