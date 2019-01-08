package echo.engineer

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 *  HappyMavenPlugin
 *  Info: HappyMavenPlugin
 *  Created by Plucky(plucky@echo.engineer) on 2018/12/21 - 6:55 PM
 *  more about me: http://www.1991th.com
 */

class HappyMavenPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        // 0. runtime check
        def hasLib = project.plugins.findPlugin("com.android.library")
        if (!hasLib) {
            throw new IllegalStateException(" 'android-library' plugin required.")
        }

        // 1. create HappyMaven extension
        project.extensions.create("HappyMaven", HappyMavenExtension)
        project.afterEvaluate {
            // 2. read config
            def extension = project.HappyMaven
            def rootExt = project.rootProject.ext
            def global = rootExt.has("HappyMaven") ? rootExt.HappyMaven : null
            if (!extension && !global) {
                throw new IllegalStateException("please config HappyMaven first.")
            }
            // 3. final config
            def config = new HappyMavenExtension()
            println("****************** HappyMaven ******************")
            if (global) {
                HappyParser.parseRootConfig(global, config)
            }
            if (extension) {
                HappyParser.parseModuleConfig(extension, config)
            }
            println("\n---Final Config---")
            println(config)
            if (!config.groupId) {
                throw new IllegalStateException("groupId is nil.")
            }
            if (!config.artifactId) {
                throw new IllegalStateException("artifactId is nil.")
            }
            if (!config.version) {
                throw new IllegalStateException("version is nil.")
            }
            if (!config.packaging) {
                throw new IllegalStateException("packaging is nil.")
            }
            HappyPublish.publish(project,config)
            println("\n****************** HappyMaven ******************")
        }
    }
}