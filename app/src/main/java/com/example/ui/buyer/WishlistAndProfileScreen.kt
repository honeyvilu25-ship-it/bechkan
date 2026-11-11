package com.example.ui.buyer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AddressEntity
import com.example.data.model.ProductEntity
import com.example.data.model.UserEntity
import com.example.data.model.WishlistItemEntity
import com.example.ui.components.formatPkr
import com.example.ui.components.getCategoryIcon
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WishlistScreen(
    wishlistItems: List<WishlistItemEntity>,
    onProductClick: (String) -> Unit,
    onRemoveFromWishlist: (String) -> Unit,
    onStartShopping: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Wishlist (${wishlistItems.size})", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BichkandSurfaceLight)
            )
        }
    ) { paddingValues ->
        if (wishlistItems.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(Icons.Default.FavoriteBorder, contentDescription = null, tint = BichkandTextSecondary, modifier = Modifier.size(64.dp))
                Spacer(modifier = Modifier.height(16.dp))
                Text("Your wishlist is empty", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = BichkandNavyDark)
                Spacer(modifier = Modifier.height(6.dp))
                Text("Tap the heart icon on any product to save it here for later.", fontSize = 12.sp, color = BichkandTextSecondary, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onStartShopping,
                    colors = ButtonDefaults.buttonColors(containerColor = BichkandElectricBlue)
                ) {
                    Text("Explore Marketplace")
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(BichkandBgLight),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(wishlistItems) { item ->
                    Card(
                        onClick = { onProductClick(item.productId) },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = BichkandSurfaceLight),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("wishlist_item_${item.id}")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(BichkandLightBlueAccent),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = getCategoryIcon(item.category),
                                    contentDescription = null,
                                    tint = BichkandElectricBlue,
                                    modifier = Modifier.size(28.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.title,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 13.sp,
                                    maxLines = 2,
                                    color = BichkandNavyDark
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = formatPkr(item.price),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = BichkandElectricBlue
                                )
                            }

                            IconButton(onClick = { onRemoveFromWishlist(item.productId) }) {
                                Icon(Icons.Default.DeleteOutline, contentDescription = "Remove", tint = BichkandRose)
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    currentUser: UserEntity?,
    addresses: List<AddressEntity>,
    onOpenRoleSwitcher: () -> Unit,
    onOpenSellerRegistration: () -> Unit,
    onOpenSellerDashboard: () -> Unit,
    onOpenAdminDashboard: () -> Unit,
    onOpenOrdersHistory: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Account & Settings", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BichkandSurfaceLight)
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(BichkandBgLight),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // User Card
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = BichkandNavyDark),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .background(BichkandElectricBlue),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = currentUser?.name?.take(1) ?: "U",
                                    color = Color.White,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 24.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = currentUser?.name ?: "Guest User",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                                Text(
                                    text = currentUser?.email ?: "guest@bichkand.pk",
                                    color = Color.White.copy(alpha = 0.7f),
                                    fontSize = 12.sp
                                )
                                Text(
                                    text = currentUser?.phone ?: "+92 300 0000000",
                                    color = BichkandCyan,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider(color = Color.White.copy(alpha = 0.15f))
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Current Role:", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    color = when (currentUser?.role) {
                                        "ADMIN" -> BichkandRose
                                        "SELLER" -> BichkandAmber
                                        else -> BichkandElectricBlue
                                    },
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = currentUser?.role ?: "BUYER",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Button(
                                onClick = onOpenRoleSwitcher,
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.2f)),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.SwitchAccount, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Switch Role", color = Color.White, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }

            // Quick Access Portals (Seller / Admin)
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = BichkandSurfaceLight),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        if (currentUser?.role == "SELLER") {
                            ProfileMenuOption(
                                icon = Icons.Default.Storefront,
                                title = "Open Seller Dashboard",
                                subtitle = "Manage your shop, add products, view earnings",
                                tint = BichkandAmber,
                                onClick = onOpenSellerDashboard
                            )
                        } else {
                            ProfileMenuOption(
                                icon = Icons.Default.AddBusiness,
                                title = "Open a Shop on BICHKAND",
                                subtitle = "Become a verified seller and sell products nationwide",
                                tint = BichkandGreen,
                                onClick = onOpenSellerRegistration
                            )
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                        if (currentUser?.role == "ADMIN") {
                            ProfileMenuOption(
                                icon = Icons.Default.AdminPanelSettings,
                                title = "Open Marketplace Admin Panel",
                                subtitle = "Approve sellers, moderate products, view analytics",
                                tint = BichkandRose,
                                onClick = onOpenAdminDashboard
                            )
                        } else {
                            ProfileMenuOption(
                                icon = Icons.Default.LocalShipping,
                                title = "My Orders & Tracking",
                                subtitle = "View purchase history and live shipping updates",
                                tint = BichkandElectricBlue,
                                onClick = onOpenOrdersHistory
                            )
                        }
                    }
                }
            }

            // Delivery Addresses
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = BichkandSurfaceLight),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Saved Delivery Addresses", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("${addresses.size} Saved", fontSize = 11.sp, color = BichkandTextSecondary)
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        addresses.forEach { addr ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.LocationOn, contentDescription = null, tint = BichkandElectricBlue, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text("${addr.label}: ${addr.recipientName} (${addr.city})", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                    Text(addr.streetAddress, fontSize = 11.sp, color = BichkandTextSecondary)
                                }
                            }
                        }
                    }
                }
            }

            // Architecture Ready for Urdu Support
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = BichkandSurfaceLight),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Language, contentDescription = null, tint = BichkandElectricBlue)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Language / زبان", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("English (Default)", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                Text("Urdu localization architecture enabled", fontSize = 11.sp, color = BichkandTextSecondary)
                            }
                            Surface(
                                color = BichkandGreen.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text("READY", color = BichkandGreen, fontWeight = FontWeight.Bold, fontSize = 10.sp, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileMenuOption(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    tint: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(tint.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = BichkandNavyDark)
            Text(subtitle, fontSize = 11.sp, color = BichkandTextSecondary)
        }
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = BichkandTextSecondary)
    }
}
