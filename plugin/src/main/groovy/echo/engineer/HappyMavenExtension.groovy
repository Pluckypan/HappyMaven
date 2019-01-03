package echo.engineer
/**
 *  HappyMavenExtension
 *  Info: HappyMaven扩展
 *  Created by Plucky(plucky@echo.engineer) on 2018/12/21 - 6:55 PM
 *  more about me: http://www.1991th.com
 */
class HappyMavenExtension {
    //Main
    String groupId
    String artifactId
    String version

    String packaging
    //Repo
    String releaseRepoUrl
    String snapshotRepoUrl
    String nexusUserName
    String nexusPassword
    //Sign
    String signKeyId
    String signPassword
    String signFile
    //Pom
    String pomName
    String pomDesc
    String pomUrl
    //Scm
    String scmUrl
    String scmConnection
    String scmDevConnection
    //License
    String licenseName
    String licenseUrl
    String licenseDist
    //Developer
    String developerId
    String developerName

    boolean isReleaseBuild() {
        return version && !version.contains("SNAPSHOT")
    }

    @Override
    String toString() {
        return String.format("[groupId=%s,artifactId=%s,version=%s,packaging=%s,release=%s]", groupId, artifactId, version, packaging, isReleaseBuild())
    }
}