# TemplatePlugin

- Gradle tasks for testing:
    - `./gradlew moveJar`: Move the output jar to your server mod directory.
    - `./gradlew runServer`: Start the server in a new cmd.

## Building

- `./gradlew jar` for a simple jar that contains only the plugin.
- `./gradlew shadowJar` for a fatJar that contains the plugin and its dependencies (use this for your server).

