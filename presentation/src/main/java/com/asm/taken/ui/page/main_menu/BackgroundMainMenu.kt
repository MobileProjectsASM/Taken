package com.asm.taken.ui.page.main_menu

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import com.asm.taken.R

@Composable
fun BackgroundMainMenu(content: @Composable () -> Unit) {
    val gradientColors = listOf(
        colorResource(id = R.color.purple_200).copy(alpha = 0.6f),
        colorResource(id = R.color.purple_500).copy(alpha = 0.6f)
    )
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = gradientColors
                    )
                )
        )
        content()
    }
}

@Preview(showSystemUi = true)
@Composable
fun PreviewBackgroundMainMenu() {
    BackgroundMainMenu {

    }
}