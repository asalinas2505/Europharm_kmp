rootProject.name = "Europharm_Kmp"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google() // Evita el filtrado de contenido específico
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google() // Sin restricciones, incluye todos los grupos
        mavenCentral()
    }
}

include(":composeApp")
