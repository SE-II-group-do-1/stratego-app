plugins {
    alias(libs.plugins.androidApplication)
    id("org.sonarqube") version "4.4.1.3373"
    jacoco
}

android {
    namespace = "com.example.stratego_app"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.stratego_app"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}


tasks.withType<Test>().configureEach{
    useJUnitPlatform()
}

tasks.register<JacocoReport>("jacocoTestReport") {
    dependsOn("testDebugUnitTest")

    reports {
        xml.required = true
        html.required = false
        xml.getOutputLocation().set(file("${project.projectDir}/build/reports/jacoco/jacocoTestReport/jacocoTestReport.xml"))
    }

    val fileFilter = mutableSetOf("**/R.class", "**/R$*.class", "**/BuildConfig.*", "**/Manifest*.*", "**/*Test*.*", "android/**/*.*")
    val debugTree = fileTree("${project.layout.buildDirectory.get().asFile}/intermediates/javac/debug"){
        exclude(fileFilter)
    }
    val mainSrc = "${project.projectDir}/src/main/java"

    sourceDirectories.setFrom(mainSrc)
    classDirectories.setFrom(debugTree)
    executionData.setFrom("${project.layout.buildDirectory.get().asFile}/jacoco/testDebugUnitTest.exec")
}

tasks.named("jacocoTestReport") {
    finalizedBy(this)
}

sonar {
    properties {
        property("sonar.projectKey", "SE-II-group-do-1_stratego-app")
        property("sonar.organization", "se-ii-group-do-1")
        property("sonar.host.url", "https://sonarcloud.io")
        property("sonar.java.coveragePlugin", "jacoco")
        property("sonar.coverage.jacoco.xmlReportPaths", "${project.projectDir}/build/reports/jacoco/jacocoTestReport/jacocoTestReport.xml")
        // Exclude UI tests from SonarQube analysis
        property("sonar.test.exclusions", "**src/main/java/com/example/stratego_app/ui/**")
    }
}




dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation("org.jacoco:org.jacoco.core:0.8.5")
    testImplementation(libs.junit)
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    //Espresso dependencies
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
    androidTestImplementation("androidx.test:runner:1.4.0")
    androidTestImplementation("androidx.test:rules:1.4.0")
    implementation(libs.okhttp)
    implementation("com.github.NaikSoftware:StompProtocolAndroid:1.6.6")
    implementation("com.google.code.gson:gson:2.3.1")
    implementation("io.reactivex.rxjava2:rxjava:2.2.5")
    implementation("io.reactivex.rxjava2:rxandroid:2.1.0")

}