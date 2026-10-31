package com.example.module2task10

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlin.random.Random

private const val FREE_DELIVERY_THRESHOLD = 500

data class Cart(
    val products: List<Product>,
) {
    // Сумма вычисляется из списка, а не накапливается в поле Activity
    val totalSum: Int get() = products.sumOf { it.price }
}

data class Product(
    val id: Int,
    val name: String,
    val price: Int,
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ShoppingCartScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun ShoppingCartScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    // Единственный источник правды — состояние корзины
    var cart by remember {
        mutableStateOf(
            Cart(
                listOf(
                    Product(0, "Товар #1", 100),
                    Product(1, "Товар #2", 150),
                    Product(2, "Товар #3", 56),
                )
            )
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        for (product in cart.products) {
            Text(text = "${product.name} - ${product.price} рублей")
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(text = "Товаров на сумму: ${cart.totalSum} рублей")

        AddProductSection(
            onAdd = {
                val products = cart.products
                val newProduct = Product(
                    id = (products.maxOfOrNull { it.id } ?: -1) + 1,
                    name = "Товар #${products.size + 1}",
                    price = Random.nextInt(0, 100),
                )
                cart = cart.copy(products = products + newProduct)

                // Toast — побочный эффект, показываем его в обработчике нажатия,
                // а не в теле composable-функции
                if (cart.totalSum > FREE_DELIVERY_THRESHOLD) {
                    Toast.makeText(context, "Доставка бесплатная!", Toast.LENGTH_SHORT).show()
                }
            },
        )

        RemoveProductSection(
            isVisible = cart.products.isNotEmpty(),
            onRemove = { cart = cart.copy(products = cart.products.dropLast(1)) },
        )
    }
}

@Composable
fun AddProductSection(onAdd: () -> Unit) {
    Button(onClick = onAdd) {
        Text(text = "Добавить товар")
    }
}

@Composable
fun RemoveProductSection(
    isVisible: Boolean,
    onRemove: () -> Unit,
) {
    if (isVisible) {
        Button(onClick = onRemove) {
            Text(text = "Удалить товар")
        }
    }
}