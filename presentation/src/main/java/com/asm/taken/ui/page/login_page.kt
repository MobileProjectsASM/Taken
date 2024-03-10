package com.asm.taken.ui.page

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.asm.taken.R
import com.asm.taken.ui.AppTitle
import com.asm.taken.ui.DefaultImageButton
import com.asm.taken.ui.PuzzleDefaultButton
import com.asm.taken.ui.PuzzleDefaultOutlinedTrailingIcon
import com.asm.taken.ui.PuzzleDefaultText
import com.asm.taken.ui.PuzzleGeneralTitle

@Composable
fun LoginPage(
    userId: String,
    password: String,
    isPasswordVisible: Boolean,
    onUserIdChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onVisibilityChanged: (Boolean) -> Unit
) {
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
            AppTitle(
                text = stringResource(id = R.string.app_name),
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter),
                textAlign = TextAlign.Center,
                color = Color.White
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            Box(modifier = Modifier.height(250.dp))
            CardLogin(
                userId = userId,
                password = password,
                isPasswordVisible = isPasswordVisible,
                onUserIdChanged = onUserIdChanged,
                onPasswordChanged = onPasswordChanged,
                onVisibilityChanged = onVisibilityChanged
            )
            CardSocialMedia()
            Box(modifier = Modifier.height(250.dp))
        }
    }
}

@Composable
fun CardLogin(
    userId: String,
    password: String,
    isPasswordVisible: Boolean,
    onUserIdChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onVisibilityChanged: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 15.dp, horizontal = 10.dp)
        ) {
            PuzzleGeneralTitle(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.txt_title_login_dialog)
            )
            PuzzleDefaultText(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.txt_inf_login_dialog),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(50.dp))
            PuzzleDefaultOutlinedTrailingIcon(
                value = userId,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                label = R.string.txt_label_user_id_login,
                leadingIcon = Icons.Default.Person,//R.drawable.round_person_24,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                cdLeadingIcon = R.string.txt_cd_icon_user_id,
                cdTrailingIcon = R.string.txt_cd_trailing_icon_info_,
                onValueChanged = onUserIdChanged
            )
            PuzzleDefaultOutlinedTrailingIcon(
                value = password,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                label = R.string.txt_label_password,
                leadingIcon = Icons.Default.Lock,
                cdLeadingIcon = R.string.txt_cd_icon_user_id,
                trailingIcon = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                cdTrailingIcon = R . string . txt_cd_trailing_icon_info_,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                isPasswordVisible = isPasswordVisible,
                onValueChanged = onPasswordChanged,
                onClickTrailingIcon = {
                    onVisibilityChanged.invoke(!isPasswordVisible)
                }
            )
            Spacer(modifier = Modifier.height(50.dp))
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                PuzzleDefaultButton(text = stringResource(id = R.string.txt_btn_login)) {

                }
            }
        }
    }
}

@Composable
fun CardSocialMedia() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 15.dp)
        ) {
            PuzzleGeneralTitle(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.txt_inf_sign_in)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 15.dp, horizontal = 10.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                DefaultImageButton(
                    imageSize = 40.dp,
                    iconButton = R.drawable.facebook,
                    cdIconButton = R.string.txt_cd_icon_button
                )
                DefaultImageButton(
                    imageSize = 40.dp,
                    iconButton = R.drawable.google,
                    cdIconButton = R.string.txt_cd_icon_button
                )
                DefaultImageButton(
                    imageSize = 40.dp,
                    iconButton = R.drawable.phone,
                    cdIconButton = R.string.txt_cd_icon_button
                )
            }
        }
    }
}