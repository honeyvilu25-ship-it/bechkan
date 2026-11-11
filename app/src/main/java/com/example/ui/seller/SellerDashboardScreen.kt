package com.example.ui.seller

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.OrderEntity
import com.example.data.model.ProductEntity
import com.example.data.model.SellerShopEntity
import com.example.ui.buyer.formatDate
import com.example.ui.buyer.getStatusColor
import com.example.ui.components.formatPkr
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellerDashboardScreen(
    shop: SellerShopEntity?,
    sellerProducts: List<ProductEntity>,
    incomingOrders: List<OrderEntity>,
    onAddProductClick: () -> Unit,
    onEditProductClick: (ProductEntity) -> Unit,
    onDeleteProductClick: (ProductEntity) -> Unit,
    onUpdateOrderStatus: (String, String) -> Unit,
    onBackToMarketplace: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) } // 0: Overview, 1: Products, 2: Orders

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(shop?.shopName ?: "Seller Center", fontWeight = FontWeight.Bold, fontSize = 17.sp)
                        Text(
                            text = "Status: ${shop?.status ?: "APPROVED"} • ${shop?.city ?: "Pakistan"}",
                            fontSize = 11.sp,
                            color = if (shop?.status == "APPROVED") BichkandGreen else BichkandAmber
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackToMarketplace) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BichkandSurfaceLight)
            )
        },
        floatingActionButton = {
            if (selectedTab == 1) {
                ExtendedFloatingActionButton(
                    onClick = onAddProductClick,
                    containerColor = BichkandElectricBlue,
                    contentColor = Color.White,
                    icon = { Icon(Icons.Default.Add, contentDescription = null) },
                    text = { Text("Add Product", fontWeight = FontWeight.Bold) },
                    modifier = Modifier.testTag("seller_fab_add_product")
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(BichkandBgLight)
        ) {
            // Tabs: Overview, Products, Orders
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = BichkandSurfaceLight,
                contentColor = BichkandElectricBlue
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Overview", fontSize = 13.sp, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Products (${sellerProducts.size})", fontSize = 13.sp, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("Orders (${incomingOrders.size})", fontSize = 13.sp, fontWeight = FontWeight.Bold) }
                )
            }

            when (selectedTab) {
                0 -> SellerOverviewTab(
                    shop = shop,
                    productsCount = sellerProducts.size,
                    ordersCount = incomingOrders.size,
                    onNavigateToProducts = { selectedTab = 1 },
                    onNavigateToOrders = { selectedTab = 2 }
                )
                1 -> SellerProductsTab(
                    products = sellerProducts,
                    onEdit = onEditProductClick,
                    onDelete = onDeleteProductClick
                )
                2 -> SellerOrdersTab(
                    orders = incomingOrders,
                    onUpdateStatus = onUpdateOrderStatus
                )
            }
        }
    }
}

@Composable
fun SellerOverviewTab(
    shop: SellerShopEntity?,
    productsCount: Int,
    ordersCount: Int,
    onNavigateToProducts: () -> Unit,
    onNavigateToOrders: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Earnings Banner
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = BichkandNavyDark),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text("Total Shop Sales & Earnings", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = formatPkr(shop?.earnings ?: 1250000.0),
                        color = Color.White,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Black
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = BichkandGreen.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "Marketplace Commission: ${(shop?.commissionRate ?: 0.05) * 100}%",
                                color = BichkandGreen,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }

        // 3 Key Stat Cards (Sales, Active Products, Rating)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    title = "Orders",
                    value = "$ordersCount",
                    icon = Icons.Default.ReceiptLong,
                    color = BichkandElectricBlue,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Products",
                    value = "$productsCount",
                    icon = Icons.Default.Inventory2,
                    color = BichkandAmber,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Shop Rating",
                    value = "${shop?.rating ?: 4.9} ★",
                    icon = Icons.Default.Star,
                    color = BichkandGreen,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Shop Information
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = BichkandSurfaceLight),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Shop Profile", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Business Address: ${shop?.businessAddress ?: "N/A"}", fontSize = 12.sp)
                    Text("Dispatches from: ${shop?.city ?: "Pakistan"}", fontSize = 12.sp, color = BichkandTextSecondary)
                    Text("Contact Phone: ${shop?.phone ?: "+92 300 0000000"}", fontSize = 12.sp, color = BichkandTextSecondary)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = shop?.description ?: "Verified seller on BICHKAND marketplace.",
                        fontSize = 12.sp,
                        color = BichkandTextPrimary,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        // Quick Actions
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onNavigateToProducts,
                    colors = ButtonDefaults.buttonColors(containerColor = BichkandElectricBlue),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Manage Catalog")
                }

                OutlinedButton(
                    onClick = onNavigateToOrders,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Incoming Orders")
                }
            }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = BichkandSurfaceLight),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(value, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = BichkandNavyDark)
            Text(title, fontSize = 10.sp, color = BichkandTextSecondary)
        }
    }
}

@Composable
fun SellerProductsTab(
    products: List<ProductEntity>,
    onEdit: (ProductEntity) -> Unit,
    onDelete: (ProductEntity) -> Unit
) {
    if (products.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No products in your catalog. Tap + to add your first product!", fontSize = 13.sp, color = BichkandTextSecondary)
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(products) { prod ->
                Card(
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = BichkandSurfaceLight),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(prod.title, fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text("Category: ${prod.category} • Stock: ${prod.stock}", fontSize = 11.sp, color = BichkandTextSecondary)
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(formatPkr(prod.displayPrice), fontWeight = FontWeight.Bold, fontSize = 14.sp, color = BichkandElectricBlue)
                                Spacer(modifier = Modifier.width(8.dp))
                                Surface(
                                    color = if (prod.status == "APPROVED") BichkandGreen.copy(alpha = 0.15f) else BichkandAmber.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        prod.status,
                                        color = if (prod.status == "APPROVED") BichkandGreen else BichkandAmber,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        IconButton(onClick = { onEdit(prod) }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit", tint = BichkandElectricBlue)
                        }
                        IconButton(onClick = { onDelete(prod) }) {
                            Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = BichkandRose)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SellerOrdersTab(
    orders: List<OrderEntity>,
    onUpdateStatus: (String, String) -> Unit
) {
    if (orders.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No incoming orders yet. Share your shop link to start selling!", fontSize = 13.sp, color = BichkandTextSecondary)
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(orders) { order ->
                var expandedStatusDropdown by remember { mutableStateOf(false) }
                val availableStatuses = listOf("PENDING", "CONFIRMED", "PROCESSING", "SHIPPED", "OUT_FOR_DELIVERY", "DELIVERED", "CANCELLED")

                Card(
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = BichkandSurfaceLight),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Order #${order.orderNumber}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Surface(
                                color = getStatusColor(order.orderStatus),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    order.orderStatus,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Customer: ${order.buyerName} • ${order.buyerPhone}", fontSize = 12.sp)
                        Text("Delivery City: ${order.city}", fontSize = 12.sp, color = BichkandTextSecondary)
                        Text("Amount: ${formatPkr(order.totalAmount)} (${order.paymentMethod})", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = BichkandNavyDark)

                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(8.dp))

                        // Status Updater Dropdown
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Update Status:", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            Box {
                                OutlinedButton(
                                    onClick = { expandedStatusDropdown = true },
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text(order.orderStatus, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                }

                                DropdownMenu(
                                    expanded = expandedStatusDropdown,
                                    onDismissRequest = { expandedStatusDropdown = false }
                                ) {
                                    availableStatuses.forEach { st ->
                                        DropdownMenuItem(
                                            text = { Text(st, fontSize = 12.sp) },
                                            onClick = {
                                                onUpdateStatus(order.id, st)
                                                expandedStatusDropdown = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
