package org.example.project

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import org.example.project.commons.platformSpecificModule
import org.example.project.di.sharedModule
import org.example.project.presentation.navigation.NavGraph
import org.koin.compose.KoinApplication
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.koinConfiguration
import org.koin.dsl.module

@Composable
@Preview
fun App(platformModule: Module = module { }) {
    MaterialTheme {
        KoinApplication(
            configuration = koinConfiguration(declaration = {
                modules(
                    sharedModule,
                    platformSpecificModule(),
                    platformModule,
                )
            }),
            content = {
                NavGraph()
            }
        )
    }
}