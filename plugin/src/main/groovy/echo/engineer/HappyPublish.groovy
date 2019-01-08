package echo.engineer

import org.gradle.api.Project
import org.gradle.api.artifacts.maven.MavenDeployment
import org.gradle.api.tasks.bundling.Jar
import org.gradle.api.tasks.javadoc.Javadoc

class HappyPublish {
    static void publish(Project project, HappyMavenExtension config) {
        project.apply plugin: 'maven'
        project.apply plugin: 'signing'

        //SourcesJar
        project.task('androidSourcesJar', type: Jar) {
            classifier = 'sources'
            from project.android.sourceSets.main.java.sourceFiles, project.android.sourceSets.debug.java.sourceFiles
        }
        //DocJar
        project.task('androidJavadocs', type: Javadoc) {
            source = project.android.sourceSets.main.java.srcDirs
            classpath += project.files(project.android.getBootClasspath().join(File.pathSeparator))
            failOnError true
            // Append also the classpath and files for release library variants. This fixes the javadoc warnings.
            // Got it from here - https://github.com/novoda/bintray-release/pull/39/files
            def releaseVariant = project.android.libraryVariants.toList().first()
            classpath += releaseVariant.javaCompile.classpath
            classpath += releaseVariant.javaCompile.outputs.files

            // We don't need javadoc for internals.
            exclude '**/internal/*'

            // Append Java 7, Android references and docs.
            options.links("http://docs.oracle.com/javase/7/docs/api/");
            options.linksOffline "https://developer.android.com/reference", "${project.android.sdkDirectory}/docs/reference"
        }
        project.task('androidJavadocsJar', type: Jar, dependsOn: 'androidJavadocs') {
            classifier = 'javadoc'
            from project.tasks.androidJavadocs.destinationDir
        }
        project.artifacts {
            archives project.tasks.androidSourcesJar
            archives project.tasks.androidJavadocsJar
        }

        //Upload
        project.uploadArchives {
            repositories {
                mavenDeployer {
                    beforeDeployment { MavenDeployment deployment -> project.signing.signPom(deployment) }

                    pom.groupId = config.groupId
                    pom.artifactId = config.artifactId
                    pom.version = config.version

                    repository(url: project.uri(config.releaseRepoUrl)) {
                        if (config.nexusUserName && config.nexusPassword) {
                            authentication(userName: config.nexusUserName, password: config.nexusPassword)
                        }
                    }
                    snapshotRepository(url: project.uri(config.snapshotRepoUrl)) {
                        if (config.nexusUserName && config.nexusPassword) {
                            authentication(userName: config.nexusUserName, password: config.nexusPassword)
                        }
                    }

                    pom.project {
                        name config.pomName
                        packaging config.packaging
                        description config.pomDesc
                        url config.pomUrl

                        scm {
                            url config.scmUrl
                            connection config.scmConnection
                            developerConnection config.scmDevConnection
                        }

                        licenses {
                            license {
                                name config.licenseName
                                url config.licenseUrl
                                distribution config.licenseDist
                            }
                        }

                        developers {
                            developer {
                                id config.developerId
                                name config.developerName
                            }
                        }
                    }
                }
            }
        }

        //签名 sign
        project.signing {
            required {
                config.isReleaseBuild() && project.gradle.taskGraph.hasTask("uploadArchives")
            }
            sign project.configurations.archives
        }
    }
}