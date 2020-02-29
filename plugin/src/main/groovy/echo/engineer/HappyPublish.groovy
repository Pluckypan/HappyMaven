package echo.engineer

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.artifacts.maven.MavenDeployment
import org.gradle.api.tasks.bundling.Jar
import org.gradle.api.tasks.javadoc.Javadoc

class HappyPublish {

    static void androidConfig(Project project, HappyMavenExtension config) {
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

            def variant = project.android.libraryVariants.toList().first()
            Task javaCompileTask
            if (variant.hasProperty('javaCompileProvider')) {
                // Android 3.3.0+
                javaCompileTask = variant.javaCompileProvider.get()
            } else {
                // Older Android
                javaCompileTask = variant.javaCompile
            }
            if (javaCompileTask != null) {
                classpath += javaCompileTask.classpath
                classpath += javaCompileTask.outputs.files
            }

            // We don't need javadoc for internals.
            exclude '**/internal/*'
            exclude '**/BuildConfig.java'
            exclude '**/R.java'
            exclude '**/R.html', '**/R.*.html', '**/index.html', '**/*.kt'

            // Append Java 7, Android references and docs.
            options.version(true)
            options.author(true)
            options.charSet("UTF-8")
            options.docEncoding("UTF-8")
            options.links("http://docs.oracle.com/javase/7/docs/api/")
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
    }

    static void javaConfig(Project project, HappyMavenExtension config) {

        project.task('sourcesJar', type: Jar, dependsOn: 'classes') {
            classifier = 'sources'
            from project.sourceSets.main.allSource
        }

        project.task('javadocJar', type: Jar, dependsOn: 'javadoc') {
            classifier = 'sources'
            from project.tasks.javadoc.destinationDir
        }

        project.artifacts {
            archives project.tasks.sourcesJar
            archives project.tasks.javadocJar
        }
    }

    static void publish(Project project, HappyMavenExtension config, boolean androidLib) {
        project.apply plugin: 'maven'
        project.apply plugin: 'signing'

        if (androidLib) {
            androidConfig(project, config)
        } else {
            javaConfig(project, config)
        }

        // gen JavaDoc chinese word error
        project.tasks.withType(Javadoc) {
            options.addStringOption('Xdoclint:none', '-quiet')
            options.addStringOption('encoding', 'UTF-8')
        }

        //Upload
        project.uploadArchives {
            repositories {
                mavenDeployer {
                    beforeDeployment { MavenDeployment deployment ->
                        if (!config.isReleaseBuild()) return
                        def _id = HappyParser.getPropertyVal(project, "signing.keyId")
                        def _pwd = HappyParser.getPropertyVal(project, "signing.password")
                        def _ring = HappyParser.getPropertyVal(project, "signing.secretKeyRingFile")
                        if (!_id) {
                            project.logger.error("****************** signing.keyId is nil ******************")
                            return
                        }
                        if (!_pwd) {
                            project.logger.error("****************** signing.password is nil ******************")
                            return
                        }
                        if (!_ring || !new File(_ring).exists()) {
                            project.logger.error("****************** signing.secretKeyRingFile not exists ******************")
                            return
                        }
                        project.signing.signPom(deployment)
                    }

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