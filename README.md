# PartySystem

Ein ressourcenschonendes Partysystem-Plugin für Paper 1.20+.

## Anforderungen
- Paper 1.20+ (mindestens 1.20.1)
- Java 17

## Befehle
- `/party invite <Spieler>` – Lädt einen Spieler in deine Party ein (erstellt automatisch eine Party, falls noch keine vorhanden ist).
- `/party accept` – Nimmt eine offene Einladung an.
- `/party leave` – Verlässt die aktuelle Party (oder löst sie auf, falls du Leader bist).
- `/party info` – Zeigt den Leader und die Mitglieder der aktuellen Party an.

## Build-Anleitung
1. Repository klonen oder herunterladen.
2. `mvn clean package` ausführen.
3. Die erstellte `.jar`-Datei aus dem `target/`-Ordner in den `plugins/`-Ordner deines Minecraft-Servers kopieren.
