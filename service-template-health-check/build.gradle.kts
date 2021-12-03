plugins {
    id("TestReporter")
}

apply<IntegrationTestsPlugin>()
apply<RestAssuredTestDependenciesPlugin>()

dependencies {
    implementation(project(":service-template-domain"))
    implementation(project(":service-template-util"))

    implementation("org.springframework.boot:spring-boot-starter-web:$springBootVersion")

    testImplementation(project(":service-template-persistence"))
    testImplementation(project(":service-template-test-data"))
    testImplementation(project(":service-template-test-containers"))
    testImplementation(project(":service-template-test-util"))

    runtimeOnly("mysql:mysql-connector-java:8.0.21")

}

tasks.findByName("check")!!.shouldRunAfter(":service-template-domain:check")
