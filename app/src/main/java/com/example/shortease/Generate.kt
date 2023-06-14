package com.example.shortease

import android.graphics.ImageDecoder
import android.graphics.Insets.add
import android.graphics.drawable.AnimatedImageDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.util.Size
import android.widget.ImageView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.shortease.ui.theme.ShortEaseTheme
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shortease.ui.theme.colorPalette
import java.nio.file.Files.size
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape


@Composable
fun Generate( navController: NavController) {

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Surface(modifier = Modifier.fillMaxSize(), color = lightColorScheme().background) {
            ShortEaseTheme {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 245.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                     Image(
                        painter = painterResource(R.drawable.spinner_loading_1),
                        contentDescription = "Loading Icon",
                        modifier = Modifier
                            .size(190.dp)
                            .padding(vertical = 16.dp)
                    )
                    Text(message(navController), color = colorPalette.ShortEaseRed,
                        fontSize = 22.sp)
                }
            }
        }
    }

}
@Composable
@Preview
fun GeneratorPreview(){
    Generate(navController = rememberNavController())
}

 fun message( navController: NavController): String {
    return if (navController.currentDestination?.route == Screen.MyVideos.route){
        "Generating..."
    }
    else {
         "Generating Preview..."
    }
}
