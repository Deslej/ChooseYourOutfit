package com.example.chooseyouroutfit.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import com.example.chooseyouroutfit.R
import com.example.chooseyouroutfit.data.repository.OutfitRepository
import com.example.chooseyouroutfit.ui.components.ReusableActionButton
import com.example.chooseyouroutfit.ui.components.ReusableBackgroundWardrobe
import com.example.chooseyouroutfit.ui.components.ReusableTextField
import com.example.chooseyouroutfit.ui.theme.ChooseYourOutfitTheme
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class OutfitsActivity : ComponentActivity() {

    private val OODR by inject<OutfitRepository>()

    private val _outfitsNames = MutableLiveData<List<String>>()
    val outfitNames: LiveData<List<String>> = _outfitsNames

    fun searchOutfitsNamesByName(query: String) {
        lifecycleScope.launch {
            val result = OODR.searchOutfitsNames("%$query%")
            _outfitsNames.postValue(result)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChooseYourOutfitTheme {
                MainOutfitsView()
            }
        }
    }

    @Composable
    fun MainOutfitsView() {
        ReusableBackgroundWardrobe()
        OutfitSearch()
        ReturnToMain()
    }

    @Composable
    fun OutfitSearch() {

        val outfits by outfitNames.observeAsState(emptyList())
        val coroutineScope = rememberCoroutineScope()

        var selectedName by remember { mutableStateOf("") }
        var expanded by remember { mutableStateOf(false) }
        var clothesIds by remember { mutableStateOf<List<Long>>(emptyList()) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 70.dp, start = 16.dp, end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)

        ) {
            ReusableTextField(
                value = selectedName,
                onValueChange = { newValue ->
                    selectedName = newValue
                    searchOutfitsNamesByName(newValue)
                    expanded = true
                },
                placeholder = "Type to search outfits"
            )
            DropdownMenu(
                expanded = expanded && outfits.isNotEmpty(),
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth()
            ) {
                outfits.forEach { outfitName ->
                    DropdownMenuItem(
                        text = { Text(outfitName) },
                        onClick = {
                            selectedName = outfitName
                            expanded = false
                        }
                    )
                }
            }

            ReusableActionButton(
                text = stringResource(R.string.search),
                onClick = {
                    coroutineScope.launch {
                        val result = loadFilteredOutfits(selectedName)
                        clothesIds = result
                    }
                },
                isEnabled = true
            )

            // TODO - wyswietlanie listy jak w wardrobe - lista z loadFilteredOutfits
        }
    }

    suspend fun loadFilteredOutfits(selectedName: String): List<Long> {
        val clothesIdsLists = OODR.searchClothesIds(selectedName)
        return clothesIdsLists.flatten().distinct()
    }

    @Composable
    fun ReturnToMain() {
        val context = LocalContext.current
        val intent = Intent(context, MainActivity::class.java)

        Card(
            modifier = Modifier
                .padding(13.dp)
                .clickable {
                    startActivity(intent)
                    finish()
                }, colors = CardDefaults.cardColors(
                containerColor = Color.Transparent, contentColor = Color.White
            )
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                modifier = Modifier.size(40.dp),
                contentDescription = "Return Arrow"
            )
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun PreviewMainBackground() {
        MainOutfitsView()
    }
}