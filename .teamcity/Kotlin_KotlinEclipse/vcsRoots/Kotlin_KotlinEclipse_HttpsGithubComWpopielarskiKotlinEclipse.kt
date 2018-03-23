package Kotlin_KotlinEclipse.vcsRoots

import jetbrains.buildServer.configs.kotlin.v2017_2.*
import jetbrains.buildServer.configs.kotlin.v2017_2.vcs.GitVcsRoot

object Kotlin_KotlinEclipse_HttpsGithubComWpopielarskiKotlinEclipse : GitVcsRoot({
    uuid = "e85cd456-6de2-4b0d-aac5-54db1f3076f9"
    id = "Kotlin_KotlinEclipse_HttpsGithubComWpopielarskiKotlinEclipse"
    name = "https://github.com/wpopielarski/kotlin-eclipse"
    url = "https://github.com/wpopielarski/kotlin-eclipse"
    branch = "refs/heads/bump-to-1.2.30"
    authMethod = password {
        userName = "wpopielarski"
        password = "credentialsJSON:0fefd9bb-7e7f-42d6-a91b-d41d653cc676"
    }
})
