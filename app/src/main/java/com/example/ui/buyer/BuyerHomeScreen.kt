package com.example.ui.buyer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CategoryEntity
import com.example.data.model.ProductEntity
import com.example.data.model.SellerShopEntity
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun BuyerHomeScreen(
    categories: List<CategoryEntity>,
    flashDeals: List<ProductEntity>,
    featuredProducts: List<ProductEntity>,
    allProducts: List<ProductEntity>,
    topShops: List<SellerShopEntity>,
    selectedCity: String,
    onProductClick: (String) -> Unit,
    onCategoryClick: (String) -> Unit,
    onAddToCart: (ProductEntity) -> Unit,
    onViewAllClick: (String) -> Unit,
    onShopClick: (String) -> Unit,
    onOpenSearch: () -> Unit
) {
    // Filter products if city filter is active
    val filteredProducts = remember(allProducts, selectedCity) {
        if (selectedCity == "All Pakistan") {
            allProducts
        } else {
            allProducts.filter { it.cityLocation.equals(selectedCity, ignoreCase = true) }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BichkandBgLight),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // 1. Pakistani Announcement Bar
        item {
            Surface(
                color = BichkandLightBlueAccent,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Campaign,
                        contentDescription = null,
                        tint = BichkandElectricBlue,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "🇵🇰 BICHKAND Mega Deals: Free Delivery across Pakistan on orders above Rs. 2,500!",
                        fontSize = 11.sp,
                        color = BichkandNavyDark,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(10.dp)) }

        // 2. Promotional Hero Banner
        item {
            PromoHeroBanner(
                onShopNowClick = onOpenSearch
            )
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        // 3. Shop by Category (Interactive Horizontal Circles)
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                PaddingHeader(
                    title = "Shop by Category",
                    actionText = "All Categories",
                    onActionClick = { onViewAllClick("categories") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(categories) { cat ->
                        CategoryCircleItem(
                            category = cat,
                            onClick = { onCategoryClick(cat.name) }
                        )
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(20.dp)) }

        // 4. Flash Deals Section (with timer badge)
        if (flashDeals.isNotEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color(0xFFFFECEB), BichkandBgLight)
                            )
                        )
                        .padding(top = 10.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                color = BichkandRose,
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Bolt,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = "FLASH DEALS",
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Ends in 08:45:12",
                                fontSize = 11.sp,
                                color = BichkandTextSecondary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Text(
                            text = "View All",
                            color = BichkandElectricBlue,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { onViewAllClick("flash") }
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(flashDeals) { product ->
                            ProductCard(
                                product = product,
                                onClick = { onProductClick(product.id) },
                                onAddToCart = { onAddToCart(product) },
                                modifier = Modifier.width(170.dp)
                            )
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(20.dp)) }
        }

        // 5. Featured / Best-Selling Products
        if (featuredProducts.isNotEmpty()) {
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    PaddingHeader(
                        title = "Featured & Best-Sellers",
                        actionText = "See More",
                        onActionClick = { onViewAllClick("featured") }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(featuredProducts) { product ->
                            ProductCard(
                                product = product,
                                onClick = { onProductClick(product.id) },
                                onAddToCart = { onAddToCart(product) },
                                modifier = Modifier.width(170.dp)
                            )
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(20.dp)) }
        }

        // 6. Top-Rated Pakistani Shops
        if (topShops.isNotEmpty()) {
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    PaddingHeader(
                        title = "Verified Pakistani Shops",
                        actionText = "Explore",
                        onActionClick = { onViewAllClick("shops") }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(topShops) { shop ->
                            ShopCard(
                                shop = shop,
                                onClick = { onShopClick(shop.id) }
                            )
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(20.dp)) }
        }

        // 7. Recommended Products / Catalog Grid (Two Columns)
        item {
            PaddingHeader(
                title = if (selectedCity == "All Pakistan") "Recommended For You" else "Available in $selectedCity",
                actionText = "${filteredProducts.size} Items",
                onActionClick = {}
            )
        }

        item { Spacer(modifier = Modifier.height(8.dp)) }

        // Grid in column chunks of 2
        val productChunks = filteredProducts.chunked(2)
        items(productChunks) { rowItems ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
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

        item { Spacer(modifier = Modifier.height(24.dp)) }

        // 8. Marketplace Footer
        item {
            MarketplaceFooter()
        }
    }
}

@Composable
fun PaddingHeader(
    title: String,
    actionText: String,
    onActionClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = BichkandNavyDark
        )
        Text(
            text = actionText,
            color = BichkandElectricBlue,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.clickable { onActionClick() }
        )
    }
}

@Composable
fun CategoryCircleItem(
    category: CategoryEntity,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(68.dp)
            .clickable { onClick() }
            .testTag("cat_circle_${category.id}")
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(BichkandLightBlueAccent),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = getCategoryIcon(category.name),
                contentDescription = category.name,
                tint = BichkandElectricBlue,
                modifier = Modifier.size(26.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = category.name,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            color = BichkandTextPrimary,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            lineHeight = 12.sp
        )
    }
}

@Composable
fun ShopCard(
    shop: SellerShopEntity,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = BichkandSurfaceLight),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .width(210.dp)
            .testTag("shop_card_${shop.id}")
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(BichkandNavyDark),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = shop.shopName.take(2).uppercase(),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = shop.shopName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = BichkandNavyDark,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "📍 ${shop.city}",
                        fontSize = 10.sp,
                        color = BichkandTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = shop.description,
                fontSize = 11.sp,
                color = BichkandTextSecondary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 14.sp
            )

            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = BichkandAmber,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "${shop.rating}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = BichkandTextPrimary
                    )
                    Text(
                        text = " (${shop.totalSales} sold)",
                        fontSize = 10.sp,
                        color = BichkandTextSecondary
                    )
                }

                Surface(
                    color = BichkandGreen.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = "VERIFIED",
                        color = BichkandGreen,
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.sp,
                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun MarketplaceFooter() {
    Surface(
        color = BichkandNavyDark,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "BICHKAND",
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    color = BichkandElectricBlue,
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = "PAKISTAN",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.sp,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Pakistan's premier multi-vendor e-commerce marketplace connecting verified sellers in Karachi, Lahore, Islamabad, and nationwide with millions of buyers.",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 11.sp,
                lineHeight = 15.sp
            )

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = Color.White.copy(alpha = 0.15f))
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Customer Care", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Help Center", color = Color.White.copy(alpha = 0.6f), fontSize = 10.sp)
                    Text("How to Buy", color = Color.White.copy(alpha = 0.6f), fontSize = 10.sp)
                    Text("Returns & Refunds", color = Color.White.copy(alpha = 0.6f), fontSize = 10.sp)
                    Text("Contact Us: support@bichkand.pk", color = Color.White.copy(alpha = 0.6f), fontSize = 10.sp)
                }
                Column {
                    Text("Sell on BICHKAND", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Seller Registration", color = Color.White.copy(alpha = 0.6f), fontSize = 10.sp)
                    Text("Seller Center", color = Color.White.copy(alpha = 0.6f), fontSize = 10.sp)
                    Text("5% Low Commission", color = Color.White.copy(alpha = 0.6f), fontSize = 10.sp)
                    Text("Terms & Conditions", color = Color.White.copy(alpha = 0.6f), fontSize = 10.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "© 2026 BICHKAND Marketplace Ltd. All rights reserved. Registered in Pakistan.",
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 10.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
