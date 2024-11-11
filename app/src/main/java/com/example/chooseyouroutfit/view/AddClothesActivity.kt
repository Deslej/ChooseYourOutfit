package com.example.chooseyouroutfit.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.example.chooseyouroutfit.R
import com.example.chooseyouroutfit.data.entities.Category
import com.example.chooseyouroutfit.data.repository.CategoryRepository
import com.example.chooseyouroutfit.model.ClothesHolder
import com.example.chooseyouroutfit.ui.theme.ChooseYourOutfitTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class AddClothesActivity : ComponentActivity() {
    private val CAODR by inject<CategoryRepository>()
    private var categoryItems = mutableListOf<Category>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChooseYourOutfitTheme {
                BackgroundView()
            }
        }
    }


    @Composable
    fun BackgroundView() {
        var clothesHolder by remember { mutableStateOf<ClothesHolder?>(null)}
        val context = LocalContext.current
        val intentCameraXActivity = Intent(context, CameraActivity::class.java)
        Image(
            painter = painterResource(R.drawable.addingclothesbackground),
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize(),
            contentDescription = "Tło dodawania zdjec"
        )
        Column(
            modifier = Modifier.fillMaxSize()
                .padding(vertical = 50.dp, horizontal = 15.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.weight(0.3f))
            val name = textPlace(R.string.name)
            Spacer(modifier = Modifier.height(20.dp))
            val color = textPlace(R.string.color)
            Spacer(modifier = Modifier.height(20.dp))
            val season = textPlace(R.string.season)
            Spacer(modifier = Modifier.height(20.dp))
            val material = textPlace(R.string.material)
            Spacer(modifier = Modifier.height(20.dp))
            val categoryId = chooseTypeClothe()
            Spacer(modifier = Modifier.weight(0.7f))

            categoryId?.let{
                clothesHolder = ClothesHolder(
                    name = name,
                    color = color,
                    season = season,
                    material = material,
                    categoryId = categoryId
                )
            }

            Button(
                onClick = {
                    intentCameraXActivity.putExtra("objectClothes", clothesHolder)
                    startActivity(intentCameraXActivity)
                    finish()
                   },
                colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray),
                border = BorderStroke(1.dp, Color.Black),
                enabled = name.isNotEmpty() && color.isNotEmpty() && season.isNotEmpty()
                        && material.isNotEmpty() && categoryId != null)
                 {
                            Text(
                text = stringResource(R.string.photo),
                fontSize = 25.sp,
                color = Color.DarkGray
            )
            }

        }
        ReturnToMain()
    }
    @Composable
    fun textPlace(@StringRes labelText: Int):String {
        var text by remember { mutableStateOf("") }
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = text,
            onValueChange = { text = it },
            label = { Text(
                text = stringResource(id = labelText),
                color = Color.Gray,
                style = TextStyle(fontSize = 24.sp,
                    fontWeight = FontWeight.SemiBold,
                    shadow = Shadow(
                        color = Color.Black,
                        offset = Offset(2f,2f),
                        blurRadius = 1f
                    )
                    )

                ) },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                cursorColor = Color.Gray,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedBorderColor = Color.LightGray,
                unfocusedBorderColor = Color.Gray,
            ),
            textStyle = TextStyle(
                color = Color.Black,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                shadow = Shadow(color = Color.White,
                    offset = Offset(2f,2f),
                    blurRadius = 1f
                )
            ),
        )
        return text
    }

    @Composable
    fun chooseTypeClothe():Long?{
        var currentCategory by remember { mutableStateOf<Long?>(null) }
        val resourceMap = mapOf(
            "Shirt" to R.string.shirt,
            "Trousers" to R.string.trousers
        )
        loadCategory()
        LazyRow(modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp)) {
            items(items=categoryItems){category->
                val resourceId = resourceMap[category.name]
                Button(
                    onClick = {currentCategory=category.categoryId},
                    colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray),
                    border = BorderStroke(2.dp,if (category.categoryId==currentCategory) Color.Green else Color.Black)
                ){
                    Text(
                        text = if (resourceId != null) stringResource(resourceId) else "",
                        fontSize = 25.sp,
                        color = Color.DarkGray
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))

            }
        }
        return currentCategory
    }

    private fun loadCategory(){
        lifecycleScope.launch(Dispatchers.IO){categoryItems = CAODR.getAllCategories().toMutableList()}
    }

    @Composable
    fun ReturnToMain() {
        val context = LocalContext.current
        val intent = Intent(context, MainActivity::class.java)

        Card(
            modifier = Modifier
                .padding(10.dp)
                .clickable {
                    startActivity(intent)
                    finish()
                }, colors = CardDefaults.cardColors(
                containerColor = Color.Transparent, contentColor = Color.White
            )
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                modifier = Modifier.size(35.dp),
                contentDescription = "Strzałka powrotu"
            )
        }
    }
}
