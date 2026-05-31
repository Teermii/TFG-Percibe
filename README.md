# Percibe

> Alarmas geolocalizadas para Android: crea una zona en el mapa y deja que tu dispositivo te avise cuando llegues a ella. Aunque la aplicación esté cerrada. Aunque la pantalla esté bloqueada.

![Plataforma](https://img.shields.io/badge/Plataforma-Android-3DDC84?logo=android&logoColor=white)
![Lenguaje](https://img.shields.io/badge/Java-100%25-007396?logo=openjdk&logoColor=white)
![Min SDK](https://img.shields.io/badge/Min%20SDK-26%20(Android%208.0)-orange)
![Target SDK](https://img.shields.io/badge/Target%20SDK-36-blue)
![Licencia](https://img.shields.io/badge/Licencia-MIT-green.svg)

## Tabla de contenidos

- [Descripción](#descripción)
- [Características](#características)
- [Arquitectura](#arquitectura)
- [Tecnologías](#tecnologías)
- [Estructura del proyecto](#estructura-del-proyecto)
- [Requisitos](#requisitos)
- [Instalación y ejecución](#instalación-y-ejecución)
- [Permisos](#permisos)
- [Funcionamiento](#funcionamiento)
- [Licencia](#licencia)

## Descripción

**Percibe** es una aplicación móvil nativa para Android que permite crear *alarmas geolocalizadas*: avisos que se activan automáticamente cuando el usuario entra en un radio configurable alrededor de una ubicación previamente definida.

El concepto es directo: el usuario define un área circular sobre un mapa (por ejemplo, alrededor de su parada de tren) y, en cuanto el dispositivo entra dentro de esa zona, la alarma se dispara. El sistema sigue vigilando la zona incluso con la aplicación cerrada o el teléfono bloqueado, apoyándose en la API de *geofencing* de Google Play Services.

Este proyecto se ha desarrollado como Trabajo de Fin de Grado (TFG).

## Características

- **Alarmas por ubicación.** Se define un punto en el mapa y un radio; la alarma se activa al entrar en la zona.
- **Funcionamiento en segundo plano.** Las geocercas las vigila el sistema operativo, no la aplicación, por lo que siguen activas aunque se cierre la app.
- **Aviso sobre la pantalla bloqueada.** La alarma enciende la pantalla y se muestra sobre el lockscreen, ignorando incluso el modo "No molestar".
- **Repetición por días.** Permite configurar en qué días de la semana debe estar activa cada alarma.
- **Categorías con color.** Organiza las alarmas mediante etiquetas personalizables (Trabajo, Casa, Viajes, etc.).
- **Envío automático de SMS.** Notifica a contactos asignados con un mensaje personalizado cuando se activa una alarma.
- **Estadísticas e historial.** Consulta el número de activaciones de cada alarma, la más utilizada y el historial completo.
- **Configuración global.** Ajusta sonido, vibración, volumen y tiempo de parada automática.
- **Persistencia tras reinicio.** Las geocercas se vuelven a registrar automáticamente cuando el dispositivo se reinicia.

## Arquitectura

Percibe sigue el patrón **MVVM (Model–View–ViewModel)** con capas adicionales de **Repository** y **DAO**. Este diseño mantiene la lógica desacoplada de la interfaz, facilita las pruebas y permite cambiar el origen de los datos sin afectar a las pantallas.

```
+------------------------------------------------+
|  VIEW (Activities)                             |
|  Pantallas: unicamente dibujan la interfaz     |
+----------------------+-------------------------+
                       |
+----------------------v-------------------------+
|  VIEWMODEL                                     |
|  Logica y ciclo de vida. AlarmaViewModel       |
|  coordina ademas el GeofenceManager.           |
+----------------------+-------------------------+
                       |
+----------------------v-------------------------+
|  REPOSITORY                                    |
|  Decide el origen de los datos y ejecuta las   |
|  escrituras fuera del hilo principal.          |
+----------------------+-------------------------+
                       |
+----------------------v-------------------------+
|  DAO + ROOM DATABASE (SQLite)                  |
|  Consultas y persistencia local.               |
+------------------------------------------------+

Componentes del sistema (en paralelo):
  - GeofenceBroadcastReceiver -> detecta la entrada en una zona
  - BootReceiver              -> re-registra geocercas al arrancar
  - AlarmaService             -> reproduce sonido y vibracion
```

El flujo de datos hacia la interfaz es reactivo mediante **LiveData**: cuando la base de datos cambia, las pantallas que la observan se actualizan de forma automática, sin intervención manual.

## Tecnologías

| Categoría | Tecnología |
|---|---|
| Lenguaje | Java |
| Patrón de arquitectura | MVVM + Repository |
| Base de datos | [Room](https://developer.android.com/training/data-storage/room) (SQLite) |
| Reactividad | LiveData y ViewModel (Android Architecture Components) |
| Mapas | [Google Maps SDK for Android](https://developers.google.com/maps/documentation/android-sdk) |
| Geolocalización | [Geofencing API](https://developer.android.com/training/location/geofencing) (Google Play Services Location) |
| Interfaz | Material Design Components, RecyclerView |
| Sistema de construcción | Gradle (Kotlin DSL) |

## Estructura del proyecto

```
app/src/main/java/com/example/alarmaapp/
|
|-- model/              Entidades de datos (Alarma, Categoria, Contacto, etc.)
|-- database/           AppDatabase y DAOs (consultas Room)
|-- repository/         Repositorios y GeofenceManager
|-- viewmodel/          ViewModels (logica de presentacion)
|
|-- view/               Adapters (RecyclerView) y BroadcastReceivers
|-- crud/               NuevaAlarmaActivity, EditarAlarmaActivity
|-- navegacion/         Pantallas de listado, mapa, estadisticas y ajustes
|
|-- MainActivity.java          Pantalla principal
|-- MapaActivity.java          Seleccion de ubicacion en el mapa
|-- AlarmaService.java         Foreground Service (sonido y vibracion)
|-- AlarmaActivaActivity.java  Pantalla que aparece al saltar la alarma
```

## Requisitos

- Android Studio (versión reciente recomendada).
- JDK 11 o superior.
- Un dispositivo o emulador con Android 8.0 (API 26) o superior.
- Una clave de API de Google Maps válida con *Maps SDK for Android* habilitado.

## Instalación y ejecución

1. **Clonar el repositorio**

   ```bash
   git clone https://github.com/Teermii/TFG-Percibe.git
   cd TFG-Percibe
   ```

2. **Configurar la clave de Google Maps**

   El proyecto lee la clave desde `local.properties`, un fichero excluido del control de versiones por seguridad. Copia el ejemplo incluido en el repositorio:

   ```bash
   cp local.properties.example local.properties
   ```

   Y añade tu clave en `local.properties`:

   ```properties
   MAPS_API_KEY=TU_CLAVE_DE_API_AQUI
   ```

   La clave puede obtenerse en la [Google Cloud Console](https://console.cloud.google.com/google/maps-apis), habilitando *Maps SDK for Android*.

3. **Abrir el proyecto en Android Studio** y esperar a que Gradle sincronice las dependencias.

4. **Ejecutar la aplicación** en un dispositivo físico (recomendado para probar el geofencing real) o en un emulador con ubicación simulada.

## Permisos

La aplicación solicita los siguientes permisos en tiempo de ejecución:

| Permiso | Uso |
|---|---|
| `ACCESS_FINE_LOCATION` | Ubicación GPS precisa |
| `ACCESS_BACKGROUND_LOCATION` | Detección de zona con la aplicación cerrada |
| `POST_NOTIFICATIONS` | Notificaciones (obligatorio en Android 13 y superior) |
| `SEND_SMS` | Envío de SMS a los contactos asignados |
| `RECEIVE_BOOT_COMPLETED` | Re-registro de geocercas tras reiniciar el dispositivo |
| `VIBRATE`, `WAKE_LOCK`, `USE_FULL_SCREEN_INTENT` | Aviso de alarma sobre la pantalla bloqueada |
| `FOREGROUND_SERVICE` (+ `MEDIA_PLAYBACK`) | Servicio en primer plano para reproducir la alarma |

Para que las alarmas funcionen con la aplicación cerrada es imprescindible conceder el permiso de **ubicación en segundo plano**.

## Funcionamiento

**Al crear una alarma.** El usuario selecciona una ubicación en el mapa, define el radio y guarda. El `AlarmaViewModel` persiste la alarma en Room y registra una geocerca en el sistema utilizando el mismo identificador que la alarma en la base de datos.

**Al entrar en la zona.** Google Play Services detecta la entrada y dispara el `GeofenceBroadcastReceiver`. Este comprueba la repetición por días, registra la activación en el historial, envía los SMS configurados y lanza el `AlarmaService`. El servicio reproduce el sonido y la vibración y muestra `AlarmaActivaActivity` sobre la pantalla de bloqueo hasta que el usuario detiene la alarma o se cumple el tiempo de parada automática.

**Tras un reinicio.** Android elimina todas las geocercas al apagarse. El `BootReceiver` se encarga de volver a registrar las alarmas activas cuando el dispositivo arranca de nuevo.

## Licencia

Este proyecto está licenciado bajo la **Licencia MIT**. Consulta el archivo [LICENSE.txt](LICENSE.txt) para los términos completos.

```
Copyright (c) 2026 Nathan D. Señor Gomez
```

---

Desarrollado como Trabajo de Fin de Grado.
