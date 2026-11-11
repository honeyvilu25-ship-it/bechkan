package com.example.ui.admin

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.buyer.formatDate
import com.example.ui.buyer.getStatusColor
import com.example.ui.components.formatPkr
import com.example.ui.components.getCategoryIcon
import com.example.ui.seller.StatCard
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    totalUsers: Int,
    totalShops: Int,
    totalProducts: Int,
    totalOrders: Int,
    totalRevenue: Double?,
    allShops: List<SellerShopEntity>,
    allProducts: List<ProductEntity>,
    allOrders: List<OrderEntity>,
    categories: List<CategoryEntity>,
    complaints: List<ComplaintEntity>,
    platformSettings: PlatformSettingsEntity?,
    onApproveShop: (String) -> Unit,
    onRejectShop: (String) -> Unit,
    onSetProductStatus: (ProductEntity, String) -> Unit,
    onAddCategory: (String, String) -> Unit,
    onDeleteCategory: (CategoryEntity) -> Unit,
    onUpdateComplaint: (String, String, String) -> Unit,
    onUpdatePlatformSettings: (Double, Double, String) -> Unit,
    onBack: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) } // 0: Analytics, 1: Sellers, 2: Products, 3: Categories, 4: Complaints, 5: Settings
    var showAddCategoryDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("BICHKAND Admin Panel", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BichkandSurfaceLight)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(BichkandBgLight)
        ) {
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor = BichkandSurfaceLight,
                contentColor = BichkandRose,
                edgePadding = 12.dp
            ) {
                Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("Overview") })
                Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("Sellers (${allShops.size})") })
                Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }, text = { Text("Products (${allProducts.size})") })
                Tab(selected = selectedTab == 3, onClick = { selectedTab = 3 }, text = { Text("Categories (${categories.size})") })
                Tab(selected = selectedTab == 4, onClick = { selectedTab = 4 }, text = { Text("Disputes (${complaints.size})") })
                Tab(selected = selectedTab == 5, onClick = { selectedTab = 5 }, text = { Text("Settings") })
            }

            when (selectedTab) {
                0 -> AdminOverviewTab(
                    totalUsers = totalUsers,
                    totalShops = totalShops,
                    totalProducts = totalProducts,
                    totalOrders = totalOrders,
                    totalRevenue = totalRevenue ?: 0.0,
                    recentOrders = allOrders.take(5)
                )
                1 -> AdminSellersTab(
                    shops = allShops,
                    onApprove = onApproveShop,
                    onReject = onRejectShop
                )
                2 -> AdminProductsTab(
                    products = allProducts,
                    onSetStatus = onSetProductStatus
                )
                3 -> AdminCategoriesTab(
                    categories = categories,
                    onAddCategoryClick = { showAddCategoryDialog = true },
                    onDeleteCategory = onDeleteCategory
                )
                4 -> AdminDisputesTab(
                    complaints = complaints,
                    onResolve = { id -> onUpdateComplaint(id, "RESOLVED", "Dispute addressed and refund/replacement verified by Admin.") }
                )
                5 -> AdminSettingsTab(
                    settings = platformSettings,
                    onSave = onUpdatePlatformSettings
                )
            }
        }
    }

    if (showAddCategoryDialog) {
        var catName by remember { mutableStateOf("") }
        var iconName by remember { mutableStateOf("Category") }

        AlertDialog(
            onDismissRequest = { showAddCategoryDialog = false },
            title = { Text("Add New Product Category") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = catName,
                        onValueChange = { catName = it },
                        label = { Text("Category Name") },
                        placeholder = { Text("e.g. Baby Care, Hardware, Musical Instruments") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (catName.isNotBlank()) {
                            onAddCategory(catName, iconName)
                            showAddCategoryDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BichkandElectricBlue)
                ) {
                    Text("Add Category")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddCategoryDialog = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
fun AdminOverviewTab(
    totalUsers: Int,
    totalShops: Int,
    totalProducts: Int,
    totalOrders: Int,
    totalRevenue: Double,
    recentOrders: List<OrderEntity>
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Platform Revenue Card
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = BichkandNavyDark),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text("Total Marketplace Gross Merchandise Value", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = formatPkr(totalRevenue),
                        color = Color.White,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Black
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = BichkandGreen.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "Platform Commission Earned: ${formatPkr(totalRevenue * 0.05)}",
                                color = BichkandGreen,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }

        // Stats 2x2 Grid
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatCard("Total Users", "$totalUsers", Icons.Default.People, BichkandElectricBlue, Modifier.weight(1f))
                StatCard("Registered Sellers", "$totalShops", Icons.Default.Storefront, BichkandAmber, Modifier.weight(1f))
            }
        }
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatCard("Catalog Products", "$totalProducts", Icons.Default.Inventory2, BichkandCyan, Modifier.weight(1f))
                StatCard("Total Orders", "$totalOrders", Icons.Default.ReceiptLong, BichkandRose, Modifier.weight(1f))
            }
        }

        // Recent Orders
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = BichkandSurfaceLight),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Recent Orders", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    recentOrders.forEach { ord ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("#${ord.orderNumber} • ${ord.city}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                Text(formatDate(ord.createdAt), fontSize = 10.sp, color = BichkandTextSecondary)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(formatPkr(ord.totalAmount), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Text(ord.orderStatus, fontSize = 10.sp, color = getStatusColor(ord.orderStatus), fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminSellersTab(
    shops: List<SellerShopEntity>,
    onApprove: (String) -> Unit,
    onReject: (String) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(shops) { shop ->
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
                        Text(shop.shopName, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = BichkandNavyDark)
                        Surface(
                            color = when (shop.status) {
                                "APPROVED" -> BichkandGreen
                                "PENDING" -> BichkandAmber
                                else -> BichkandRose
                            },
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                shop.status,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text("City: ${shop.city} • Phone: ${shop.phone}", fontSize = 11.sp, color = BichkandTextSecondary)
                    Text("Address: ${shop.businessAddress}", fontSize = 11.sp, color = BichkandTextSecondary)
                    Text("Description: ${shop.description}", fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (shop.status != "APPROVED") {
                            Button(
                                onClick = { onApprove(shop.id) },
                                colors = ButtonDefaults.buttonColors(containerColor = BichkandGreen),
                                shape = RoundedCornerShape(6.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                            ) {
                                Text("Approve Shop", fontSize = 11.sp)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        if (shop.status != "REJECTED") {
                            OutlinedButton(
                                onClick = { onReject(shop.id) },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = BichkandRose),
                                shape = RoundedCornerShape(6.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                            ) {
                                Text("Reject / Suspend", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminProductsTab(
    products: List<ProductEntity>,
    onSetStatus: (ProductEntity, String) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(products) { prod ->
            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = BichkandSurfaceLight),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(prod.title, fontWeight = FontWeight.Bold, fontSize = 13.sp, modifier = Modifier.weight(1f))
                        Surface(
                            color = when (prod.status) {
                                "APPROVED" -> BichkandGreen
                                "PENDING" -> BichkandAmber
                                else -> BichkandRose
                            },
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(prod.status, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 9.sp, modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Shop: ${prod.shopName} • Category: ${prod.category} • Price: ${formatPkr(prod.displayPrice)}", fontSize = 11.sp, color = BichkandTextSecondary)

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        if (prod.status != "APPROVED") {
                            Button(
                                onClick = { onSetStatus(prod, "APPROVED") },
                                colors = ButtonDefaults.buttonColors(containerColor = BichkandGreen),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text("Approve", fontSize = 10.sp)
                            }
                        }
                        if (prod.status != "SUSPENDED") {
                            OutlinedButton(
                                onClick = { onSetStatus(prod, "SUSPENDED") },
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text("Suspend", fontSize = 10.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminCategoriesTab(
    categories: List<CategoryEntity>,
    onAddCategoryClick: () -> Unit,
    onDeleteCategory: (CategoryEntity) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Button(
                onClick = onAddCategoryClick,
                colors = ButtonDefaults.buttonColors(containerColor = BichkandElectricBlue),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Add New Category")
            }
        }

        items(categories) { cat ->
            Card(
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = BichkandSurfaceLight),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(getCategoryIcon(cat.name), contentDescription = null, tint = BichkandElectricBlue)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(cat.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    IconButton(onClick = { onDeleteCategory(cat) }) {
                        Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = BichkandRose)
                    }
                }
            }
        }
    }
}

@Composable
fun AdminDisputesTab(
    complaints: List<ComplaintEntity>,
    onResolve: (String) -> Unit
) {
    if (complaints.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No customer disputes or complaints recorded.", color = BichkandTextSecondary, fontSize = 13.sp)
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(complaints) { comp ->
                Card(
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = BichkandSurfaceLight),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(comp.subject, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(comp.status, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = if (comp.status == "RESOLVED") BichkandGreen else BichkandAmber)
                        }
                        Text("Customer: ${comp.userName}", fontSize = 11.sp, color = BichkandTextSecondary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(comp.description, fontSize = 12.sp)

                        if (comp.status != "RESOLVED") {
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = { onResolve(comp.id) },
                                colors = ButtonDefaults.buttonColors(containerColor = BichkandGreen)
                            ) {
                                Text("Mark as Resolved")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminSettingsTab(
    settings: PlatformSettingsEntity?,
    onSave: (Double, Double, String) -> Unit
) {
    var commission by remember { mutableStateOf((settings?.commissionPercent ?: 5.0).toString()) }
    var deliveryFee by remember { mutableStateOf((settings?.baseDeliveryFee ?: 199.0).toInt().toString()) }
    var announcement by remember { mutableStateOf(settings?.announcementText ?: "Welcome to BICHKAND!") }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = BichkandSurfaceLight),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Platform Configuration", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = commission,
                        onValueChange = { commission = it },
                        label = { Text("Marketplace Commission (%)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = deliveryFee,
                        onValueChange = { deliveryFee = it },
                        label = { Text("Base Delivery Fee in Pakistan (PKR)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = announcement,
                        onValueChange = { announcement = it },
                        label = { Text("Nationwide Announcement Banner") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            val commVal = commission.toDoubleOrNull() ?: 5.0
                            val feeVal = deliveryFee.toDoubleOrNull() ?: 199.0
                            onSave(commVal, feeVal, announcement)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BichkandElectricBlue),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Save Marketplace Settings")
                    }
                }
            }
        }
    }
}
