# HappyMaven
> Provide an easy way to publish your library(AARS or JARS) to Maven repositories

### I.features
1. jcenter()       [bintray](https://bintray.com/)
   - **not support yet.coming soon**
   - search `https://bintray.com/search?query=happymaven`
2. mavenCentral()  [maven](https://issues.sonatype.org)
   - release url `https://oss.sonatype.org/service/local/staging/deploy/maven2/`
   - snapshot url `https://oss.sonatype.org/content/repositories/snapshots/`
   - search your library `https://search.maven.org/`
   - more help -> [发布Maven构件到中央仓库](https://my.oschina.net/songxinqiang/blog/313226) &  [sonatype issue](http://www.echo.engineer/c/sonatype.html)

### II.usage
#### you can define your personal secret key In your `~/.gradle/gradle.properties` like

```
NEXUS_USER_NAME = yourUsername
NEXUS_PASSWORD = yourPassword
signing.keyId=gpg secret key id like B1855705
signing.password=gpg key password
signing.secretKeyRingFile=/Users/your name/.gnupg/secring.gpg
```

#### gen gpg secret key (Mac)
- brew install gnupg 
- or download dmg form http://www.gpgtools.org/
- test gpg `gpg --help`
- gen key `gpg --full-generate-key`
- output secret key: `gpg -o .gnupg/secring.gpg  --export-secret-keys`
- send publish key `gpg --keyserver  http://pool.sks-keyservers.net:11371/ --send-keys B1855705`
  or `gpg --keyserver http://keyserver.ubuntu.com:11371/ --send-keys B1855705`


### III.full module config in library `build.gradle`

```
apply plugin: 'happy-maven'

HappyMaven {
    //Main
    groupId = "engineer.echo"
    artifactId = "happymaven"
    version = "0.0.1"

    packaging = "aar"
    //Repo
    releaseRepoUrl = "https://oss.sonatype.org/service/local/staging/deploy/maven2/"
    snapshotRepoUrl = "https://oss.sonatype.org/content/repositories/snapshots/"
    nexusUserName = "plucky"
    nexusPassword = "xxxxxx"
    //Pom
    pomName = "HappyMaven"
    pomDesc = "Easy to publish android library."
    pomUrl = "https://github.com/Pluckypan/HappyMaven"
    //Scm
    scmUrl = "https://github.com/Pluckypan/HappyMaven.git"
    scmConnection = "scm:git@github.com:Pluckypan/HappyMaven.git"
    scmDevConnection = "scm:git@github.com:Pluckypan/HappyMaven.git"
    //License
    licenseName = "The Apache Software License, Version 2.0"
    licenseUrl = "http://www.apache.org/licenses/LICENSE-2.0.txt"
    licenseDist = "repo"
    //Developer
    developerId = "pluckypan"
    developerName = "Plucky Pan"
}
```

### IIII.full global config in rootProject `build.gradle`

```
ext.HappyMaven = [
        "GROUP_ID": "engineer.echo",
        "ARTIFACT_ID": "happymaven",
        "VERSION": "0.0.1",

        "PACKAGING": "aar",

        "RELEASE_REPO_URL": "https://oss.sonatype.org/service/local/staging/deploy/maven2/",
        "SNAPSHOT_REPO_URL": "https://oss.sonatype.org/content/repositories/snapshots/",
        "NEXUS_USER_NAME": "plucky",
        "NEXUS_PASSWORD": "xxxxxx",

        "POM_NAME": "HappyMaven",
        "POM_DESC": "Easy to publish android library.",
        "POM_URL": "https://github.com/Pluckypan/HappyMaven",

        "SCM_URL": "https://github.com/Pluckypan/HappyMaven.git",
        "SCM_CONNECTION": "scm:git@github.com:Pluckypan/HappyMaven.git",
        "SCM_DEV_CONNECTION": "scm:git@github.com:Pluckypan/HappyMaven.git",

        "LICENSE_NAME": "The Apache Software License, Version 2.0",
        "LICENSE_URL": "http://www.apache.org/licenses/LICENSE-2.0.txt",
        "LICENSE_DIST": "repo",

        "DEVELOPER_ID": "pluckypan",
        "DEVELOPER_NAME": "Plucky Pan"
]
```

### X.thanks