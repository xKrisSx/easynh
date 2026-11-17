
plugins {
    id("com.gtnewhorizons.gtnhconvention")
}

tasks.named<org.gradle.api.tasks.bundling.AbstractArchiveTask>("reobfJar") {
    archiveFileName.set("easynh.jar")
}
