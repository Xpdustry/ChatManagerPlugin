# ChatManagerPlugin

[![Build status](https://github.com/Xpdustry/ChatManagerPlugin/actions/workflows/commit.yml/badge.svg?branch=master&event=push)](https://github.com/Xpdustry/ChatManagerPlugin/actions/workflows/commit.yml)
[![Mindustry 6.0 | 7.0 ](https://img.shields.io/badge/Mindustry-7.0-ffd37f)](https://github.com/Anuken/Mindustry/releases)

## Description

- Gradle tasks for testing:
    - `./gradlew moveJar`: Move the output jar to your server mod directory.
    - `./gradlew runServer`: Start the server in a new cmd.

## Building

- `./gradlew jar` for a simple jar that contains only the plugin.
- `./gradlew shadowJar` for a fatJar that contains the plugin and its dependencies (use this for your server).

