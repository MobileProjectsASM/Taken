package com.asm.taken.ui.page.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.asm.taken.R
import com.asm.taken.ui.puzzleFontFamily

@Composable
fun BackgroundLogin(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        val configuration = LocalConfiguration.current
        val radius = configuration.screenWidthDp
        Box(
            modifier = Modifier
                .height(500.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(bottomStart = (radius * 0.8).dp))
                .background(color = colorResource(id = R.color.purple_200).copy(alpha = 0.4f))
        )
        Box(
            modifier = Modifier
                .height(500.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(bottomStart = radius.dp))
                .background(color = colorResource(id = R.color.purple_200))
        )
        Box(
            modifier = Modifier
                .height(270.dp)
                .fillMaxWidth()
        ) {
            Image(
                modifier = Modifier
                    .size(150.dp)
                    .align(Alignment.Center),
                painter = painterResource(id = R.drawable.puzzle),
                contentDescription = stringResource(
                    id = R.string.txt_cd_icon_app
                )
            )
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter),
                fontSize = dimensionResource(id = R.dimen.app_name_size).value.sp,
                text = stringResource(id = R.string.app_name),
                fontFamily = puzzleFontFamily,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = Color.White
            )
        }
        content()
    }
}