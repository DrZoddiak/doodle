plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
}

val kotlinVersion = "1.6.10" //: String by System.getProperties()

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
    implementation(gradleApi())
}