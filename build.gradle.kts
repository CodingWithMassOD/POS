

plugins {
    kotlin("jvm") version "1.4.21"
}

repositories{
	mavenCentral()
}


dependencies{

    implementation("org.json:json:20201115")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-scalars:2.9.0")
    
}
