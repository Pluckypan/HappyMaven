package echo.engineer

/**
 *  HappyParser
 *  Info: Parse config
 *  Created by Plucky(plucky@echo.engineer) on 2019/1/3 - 15:04 PM
 *  more about me: http://www.1991th.com
 */
class HappyParser {

/**
 * use global config as default
 * @param global : rootProject config. from `ext.HappyMaven=[]` in `build.gradle`
 * @param config : final config
 */
    static void parseRootConfig(global, HappyMavenExtension config) {
        def gId = global["GROUP_ID"]
        def aId = global["ARTIFACT_ID"]
        def version = global["VERSION"]
        def pack = global["PACKAGING"]
        def dId = global["DEVELOPER_ID"]
        def dName = global["DEVELOPER_NAME"]
        def pName = global["POM_NAME"]
        def lName = global["LICENSE_NAME"]
        def repo = global["RELEASE_REPO_URL"]
        def user = global["NEXUS_USER_NAME"]
        def pwd = global["NEXUS_PASSWORD"]
        //apply root config
        config.groupId = gId
        config.artifactId = aId
        config.version = version

        config.packaging = pack

        config.releaseRepoUrl = repo
        config.snapshotRepoUrl = global["SNAPSHOT_REPO_URL"]
        if (user) {
            config.nexusUserName = user
        }
        if (pwd) {
            config.nexusPassword = pwd
        }

        config.pomName = pName
        config.pomDesc = global["POM_DESC"]
        config.pomUrl = global["POM_URL"]

        config.scmUrl = global["SCM_URL"]
        config.scmConnection = global["SCM_CONNECTION"]
        config.scmDevConnection = global["SCM_DEV_CONNECTION"]

        config.licenseName = lName
        config.licenseUrl = global["LICENSE_URL"]
        config.licenseDist = global["LICENSE_DIST"]

        config.developerId = dId
        config.developerName = dName
    }

    /**
     * override final config by module config if need
     * @param module : module config. from `HappyMaven{}` in `build.gradle`
     * @param config : final config
     */
    static void parseModuleConfig(HappyMavenExtension module, HappyMavenExtension config) {
        // Main
        if (module.groupId) {
            config.groupId = module.groupId
        }
        if (module.artifactId) {
            config.artifactId = module.artifactId
        }
        if (module.version) {
            config.version = module.version
        }

        if (module.packaging) {
            config.packaging = module.packaging
        }
        // REPO
        if (module.releaseRepoUrl) {
            config.releaseRepoUrl = module.releaseRepoUrl
        }
        if (module.snapshotRepoUrl) {
            config.snapshotRepoUrl = module.snapshotRepoUrl
        }
        if (module.nexusUserName) {
            config.nexusUserName = module.nexusUserName
        }
        if (module.nexusPassword) {
            config.nexusPassword = module.nexusPassword
        }
        // POM
        if (module.pomName) {
            config.pomName = module.pomName
        }
        if (module.pomDesc) {
            config.pomDesc = module.pomDesc
        }
        if (module.pomUrl) {
            config.pomUrl = module.pomUrl
        }
        // SCM
        if (module.scmUrl) {
            config.scmUrl = module.scmUrl
        }
        if (module.scmConnection) {
            config.scmConnection = module.scmConnection
        }
        if (module.scmDevConnection) {
            config.scmDevConnection = module.scmDevConnection
        }
        // LICENSE
        if (module.licenseName) {
            config.licenseName = module.licenseName
        }
        if (module.licenseUrl) {
            config.licenseUrl = module.licenseUrl
        }
        if (module.licenseDist) {
            config.licenseDist = module.licenseDist
        }
        // DEVELOPER
        if (module.developerId) {
            config.developerId = module.developerId
        }
        if (module.developerName) {
            config.developerName = module.developerName
        }
    }

    static localProperties(project, key, defaultVal) {
        Properties properties = new Properties()
        if (project.rootProject.file('local.properties').exists()) {
            properties.load(project.rootProject.file('local.properties').newDataInputStream())
            return properties.getProperty(key, defaultVal)
        } else {
            return null
        }
    }

    static getPropertyVal(project, key, defVal = "") {
        def local = localProperties(project, key, defVal)
        if (local != null && local.length() > 0) {
            return local
        } else if (System.getProperty(key)) {
            return System.getProperty(key)
        } else if (project.hasProperty(key)) {
            return project.getProperty(key)
        } else {
            return defVal
        }
    }
}