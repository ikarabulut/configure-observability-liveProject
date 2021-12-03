plugins {
    id("TestReporter")
}

apply<IntegrationTestsPlugin>()
apply<RestAssuredTestDependenciesPlugin>()

dependencies {
    implementation(project(":service-template-domain"))
    implementation(project(":service-template-util"))


    implementation("org.springframework.boot:spring-boot-starter-web:$springBootVersion")

    testImplementation(project(":service-template-config"))
    testImplementation(project(":service-template-test-data"))
    testImplementation(project(":service-template-test-containers"))


}

tasks.findByName("check")!!.shouldRunAfter(":service-template-domain:check")
