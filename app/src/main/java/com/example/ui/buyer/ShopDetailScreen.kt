package com.example.ui.buyer

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ProductEntity
import com.example.data.model.SellerShopEntity
import com.example.ui.components.ProductCard
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShopDetailScreen(
    shop: SellerShopEntity?,
    shopProducts: List<ProductEntity>,
    onProductClick: (String) -> Unit,
    onAddToCart: (ProductEntity) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var showContactDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(shop?.shopName ?: "Shop Profile", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BichkandSurfaceLight)
            )
        }
    ) { paddingValues ->
        if (shop == null) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = BichkandElectricBlue)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(BichkandBgLight),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Shop Header Card
                item {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = BichkandNavyDark),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(54.dp)
                                        .clip(CircleShape)
                                        .background(BichkandElectricBlue),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = shop.shopName.take(2).uppercase(),
                                        color = Color.White,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 20.sp
                                    )
                                }
                                Spacer(modifier = Modifier.width(14.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(shop.shopName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                                    Text("📍 ${shop.city}, Pakistan", color = BichkandCyan, fontSize = 12.sp)
                                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 2.dp)) {
                                        Icon(Icons.Default.Star, contentDescription = null, tint = BichkandAmber, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text("${shop.rating} Rating", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        Text(" • ${shop.totalSales} items sold", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            Text(shop.description, color = Color.White.copy(alpha = 0.85f), fontSize = 12.sp, lineHeight = 16.sp)

                            Spacer(modifier = Modifier.height(14.dp))
                            HorizontalDivider(color = Color.White.copy(alpha = 0.15f))
                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Address: ${shop.businessAddress}", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp, modifier = Modifier.weight(1f))
                                Button(
                                    onClick = { showContactDialog = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = BichkandElectricBlue),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                                ) {
                                    Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Contact", fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }

                item {
                    Text(
                        text = "Shop Products (${shopProducts.size})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = BichkandNavyDark
                    )
                }

                val chunks = shopProducts.chunked(2)
                items(chunks) { rowItems ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        for (product in rowItems) {
                            Box(modifier = Modifier.weight(1f)) {
                                ProductCard(
                                    product = product,
                                    onClick = { onProductClick(product.id) },
                                    onAddToCart = { onAddToCart(product) }
                                )
                            }
                        }
                        if (rowItems.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }

    if (showContactDialog && shop != null) {
        AlertDialog(
            onDismissRequest = { showContactDialog = false },
            title = { Text("Contact ${shop.shopName}") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Official Phone: ${shop.phone}")
                    Text("City: ${shop.city}, Pakistan")
                    Text("Business Address: ${shop.businessAddress}")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("You can dial the seller directly or contact BICHKAND customer helpline at support@bichkand.pk.", fontSize = 11.sp, color = BichkandTextSecondary)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val dialIntent = Intent(Intent.ACTION_DIAL).apply {
                            data = Uri.parse("tel:${shop.phone}")
                        }
                        context.startActivity(dialIntent)
                        showContactDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BichkandElectricBlue)
                ) {
                    Text("Dial Seller")
                }
            },
            dismissButton = {
                TextButton(onClick = { showContactDialog = false }) { Text("Close") }
            }
        )
    }
}
