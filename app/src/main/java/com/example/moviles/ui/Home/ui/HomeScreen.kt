package com.example.moviles.ui.Home.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

@Composable
fun HomeScreen(navController: NavHostController) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = { navController.navigate("add_product_screen") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(30.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFA020F0),
                    contentColor = Color.White
                ),
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Agregar Chunchesito",
                )
                Spacer(Modifier.padding(4.dp))
                Text(
                    text = "Agregar chunchesito",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }

            Button(
                onClick = { navController.navigate("list_products_screen") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(30.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFA020F0),
                    contentColor = Color.White
                ),
            ) {
                Icon(
                    imageVector = Icons.Filled.List,
                    contentDescription = "Listar Chunches",
                )
                Spacer(Modifier.padding(4.dp))
                Text(
                    text = "Ver chunchesitos",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }

            Button(
                onClick = { navController.navigate("edit_delete_product_screen") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(30.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFA020F0),
                    contentColor = Color.White
                ),
            ) {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = "Modificar Chunchesito",
                )
                Spacer(Modifier.padding(4.dp))
                Text(
                    text = "Modificar chunchesito",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }

            Button(
                onClick = {
                    navController.navigate("login_screen") {
                        popUpTo("login_screen") {
                            inclusive = true
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(30.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF800080),
                    contentColor = Color.White
                ),
            ) {
                Icon(
                    imageVector = Icons.Filled.ExitToApp,
                    contentDescription = "Salir",
                )
                Spacer(Modifier.padding(4.dp))
                Text(
                    text = "Salir",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewHomeScreen() {
    val navController = rememberNavController()
    HomeScreen(navController = navController)
}