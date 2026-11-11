package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.CategoryEntity
import com.example.data.model.ProductEntity
import com.example.data.model.UserEntity
import com.example.ui.theme.*
import java.text.NumberFormat
import java.util.Locale

fun formatPkr(amount: Double): String {
    val formatter = NumberFormat.getNumberInstance(Locale.US)
    return "Rs. " + formatter.format(amount.toInt())
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BichkandTopBar(
    currentUser: UserEntity?,
    selectedCity: String,
    cartCount: Int,
    onCityClick: () -> Unit,
    onSearchClick: () -> Unit,
    onCartClick: () -> Unit,
    onWishlistClick: () -> Unit,
    onRoleBadgeClick: () -> Unit
) {
    Surface(
        color = BichkandNavyDark,
        tonalElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Row 1: Brand Logo, City Selector, Role Chip, Cart
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Brand Name & Tag
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { /* Brand click */ }
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(BichkandElectricBlue),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.bichkand_logo),
                            contentDescription = "BICHKAND Logo",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "BICHKAND",
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "PAKISTAN'S MARKETPLACE",
                            color = BichkandCyan,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    }
                }

                // Actions: City, Role Chip, Wishlist, Cart
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Location Button
                    Surface(
                        onClick = onCityClick,
                        shape = RoundedCornerShape(20.dp),
                        color = Color.White.copy(alpha = 0.12f),
                        modifier = Modifier.testTag("location_selector")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = "Location",
                                tint = BichkandAmber,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = selectedCity,
                                color = Color.White,
                                fontSize = 11.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Role Switcher Badge
                    val roleLabel = when (currentUser?.role) {
                        "ADMIN" -> "ADMIN"
                        "SELLER" -> "SELLER"
                        else -> "BUYER"
                    }
                    val roleColor = when (currentUser?.role) {
                        "ADMIN" -> BichkandRose
                        "SELLER" -> BichkandAmber
                        else -> BichkandElectricBlue
                    }
                    Surface(
                        onClick = onRoleBadgeClick,
                        shape = RoundedCornerShape(14.dp),
                        color = roleColor,
                        modifier = Modifier.testTag("role_switcher_chip")
                    ) {
                        Text(
                            text = roleLabel,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Wishlist Icon
                    IconButton(
                        onClick = onWishlistClick,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("top_bar_wishlist")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.FavoriteBorder,
                            contentDescription = "Wishlist",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Cart Icon with Badge
                    BadgedBox(
                        badge = {
                            if (cartCount > 0) {
                                Badge(
                                    containerColor = BichkandRose,
                                    contentColor = Color.White
                                ) {
                                    Text(text = cartCount.toString(), fontSize = 10.sp)
                                }
                            }
                        }
                    ) {
                        IconButton(
                            onClick = onCartClick,
                            modifier = Modifier
                                .size(36.dp)
                                .testTag("top_bar_cart")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = "Shopping Cart",
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Search Bar Button (Opens Search Screen)
            Surface(
                onClick = onSearchClick,
                shape = RoundedCornerShape(12.dp),
                color = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .testTag("home_search_bar")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = BichkandTextSecondary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Search in BICHKAND (Mobiles, Lawn, Electronics...)",
                        color = BichkandTextSecondary,
                        fontSize = 13.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Surface(
                        color = BichkandElectricBlue,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "Search",
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BichkandBottomBar(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    userRole: String? = "BUYER"
) {
    NavigationBar(
        containerColor = BichkandSurfaceLight,
        tonalElevation = 8.dp,
        modifier = Modifier.navigationBarsPadding()
    ) {
        NavigationBarItem(
            selected = currentRoute == "home",
            onClick = { onNavigate("home") },
            icon = {
                Icon(
                    imageVector = if (currentRoute == "home") Icons.Filled.Home else Icons.Outlined.Home,
                    contentDescription = "Home"
                )
            },
            label = { Text("Home", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = BichkandElectricBlue,
                selectedTextColor = BichkandElectricBlue,
                indicatorColor = BichkandLightBlueAccent
            )
        )

        NavigationBarItem(
            selected = currentRoute == "categories",
            onClick = { onNavigate("categories") },
            icon = {
                Icon(
                    imageVector = if (currentRoute == "categories") Icons.Filled.Category else Icons.Outlined.Category,
                    contentDescription = "Categories"
                )
            },
            label = { Text("Categories", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = BichkandElectricBlue,
                selectedTextColor = BichkandElectricBlue,
                indicatorColor = BichkandLightBlueAccent
            )
        )

        NavigationBarItem(
            selected = currentRoute == "orders",
            onClick = { onNavigate("orders") },
            icon = {
                Icon(
                    imageVector = if (currentRoute == "orders") Icons.Filled.LocalShipping else Icons.Outlined.LocalShipping,
                    contentDescription = "Orders"
                )
            },
            label = { Text("Orders", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = BichkandElectricBlue,
                selectedTextColor = BichkandElectricBlue,
                indicatorColor = BichkandLightBlueAccent
            )
        )

        NavigationBarItem(
            selected = currentRoute == "cart",
            onClick = { onNavigate("cart") },
            icon = {
                Icon(
                    imageVector = if (currentRoute == "cart") Icons.Filled.ShoppingCart else Icons.Outlined.ShoppingCart,
                    contentDescription = "Cart"
                )
            },
            label = { Text("Cart", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = BichkandElectricBlue,
                selectedTextColor = BichkandElectricBlue,
                indicatorColor = BichkandLightBlueAccent
            )
        )

        val profileLabel = when (userRole) {
            "ADMIN" -> "Admin"
            "SELLER" -> "Seller"
            else -> "Account"
        }
        val profileIcon = when (userRole) {
            "ADMIN" -> Icons.Default.AdminPanelSettings
            "SELLER" -> Icons.Default.Storefront
            else -> Icons.Default.Person
        }

        NavigationBarItem(
            selected = currentRoute == "profile",
            onClick = { onNavigate("profile") },
            icon = {
                Icon(
                    imageVector = profileIcon,
                    contentDescription = profileLabel
                )
            },
            label = { Text(profileLabel, fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = BichkandElectricBlue,
                selectedTextColor = BichkandElectricBlue,
                indicatorColor = BichkandLightBlueAccent
            )
        )
    }
}

@Composable
fun ProductCard(
    product: ProductEntity,
    onClick: () -> Unit,
    onAddToCart: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = BichkandSurfaceLight),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("product_card_${product.id}")
    ) {
        Column {
            // Image Box with Flash Deal / Discount Badge
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color(0xFFE2E8F0), Color(0xFFF1F5F9))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                // Product category visual icon
                val icon = getCategoryIcon(product.category)
                Icon(
                    imageVector = icon,
                    contentDescription = product.title,
                    tint = BichkandNavyDark.copy(alpha = 0.4f),
                    modifier = Modifier.size(54.dp)
                )

                // Discount Badge (Top Left)
                if (product.discountPercentage > 0) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(6.dp)
                            .background(BichkandRose, RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "-${product.discountPercentage}%",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // City badge (Bottom Right)
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(6.dp)
                        .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 5.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = product.cityLocation,
                        color = Color.White,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Content
            Column(modifier = Modifier.padding(10.dp)) {
                // Brand & Shop Name
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = product.brand,
                        color = BichkandElectricBlue,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                // Title
                Text(
                    text = product.title,
                    fontWeight = FontWeight.Medium,
                    fontSize = 13.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 16.sp,
                    color = BichkandTextPrimary
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Price Section
                Row(
                    verticalAlignment = Alignment.Bottom,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = formatPkr(product.displayPrice),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = BichkandNavyDark
                    )
                    if (product.discountPrice != null && product.discountPrice < product.price) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = formatPkr(product.price),
                            fontSize = 10.sp,
                            color = BichkandTextSecondary,
                            textDecoration = TextDecoration.LineThrough
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Rating & Add button
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Rating",
                            tint = BichkandAmber,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "${product.rating}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = BichkandTextPrimary
                        )
                        Text(
                            text = " (${product.reviewCount})",
                            fontSize = 10.sp,
                            color = BichkandTextSecondary
                        )
                    }

                    // Quick Add to Cart
                    Surface(
                        onClick = onAddToCart,
                        shape = CircleShape,
                        color = BichkandElectricBlue,
                        modifier = Modifier
                            .size(28.dp)
                            .testTag("quick_add_${product.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddShoppingCart,
                            contentDescription = "Add to Cart",
                            tint = Color.White,
                            modifier = Modifier
                                .padding(5.dp)
                                .size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

fun getCategoryIcon(categoryName: String): ImageVector {
    return when {
        categoryName.contains("Mobile", ignoreCase = true) -> Icons.Default.PhoneAndroid
        categoryName.contains("Electronic", ignoreCase = true) -> Icons.Default.Devices
        categoryName.contains("Fashion", ignoreCase = true) || categoryName.contains("Clothing", ignoreCase = true) -> Icons.Default.Checkroom
        categoryName.contains("Shoe", ignoreCase = true) -> Icons.Default.Hiking
        categoryName.contains("Beauty", ignoreCase = true) -> Icons.Default.Spa
        categoryName.contains("Home", ignoreCase = true) -> Icons.Default.Home
        categoryName.contains("Furniture", ignoreCase = true) -> Icons.Default.Chair
        categoryName.contains("Grocery", ignoreCase = true) -> Icons.Default.ShoppingBasket
        categoryName.contains("Sport", ignoreCase = true) -> Icons.Default.SportsCricket
        categoryName.contains("Book", ignoreCase = true) -> Icons.Default.MenuBook
        categoryName.contains("Toy", ignoreCase = true) -> Icons.Default.Gamepad
        categoryName.contains("Auto", ignoreCase = true) -> Icons.Default.DirectionsCar
        else -> Icons.Default.ShoppingBag
    }
}

@Composable
fun PromoHeroBanner(
    onShopNowClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .testTag("hero_banner_card"),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
        ) {
            // Generated Hero Banner image
            Image(
                painter = painterResource(id = R.drawable.bichkand_banner),
                contentDescription = "BICHKAND Mega Sale",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Subtle gradient overlay at bottom
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.75f)),
                            startY = 60f
                        )
                    )
            )

            // Content inside banner
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp),
                verticalArrangement = Arrangement.Bottom
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Surface(
                            color = BichkandAmber,
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "FLAT 70% OFF MEGA SALE",
                                color = BichkandNavyDark,
                                fontWeight = FontWeight.Black,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Free Delivery on orders above Rs. 2,500",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Button(
                        onClick = onShopNowClick,
                        colors = ButtonDefaults.buttonColors(containerColor = BichkandElectricBlue),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("hero_shop_now_button")
                    ) {
                        Text("Shop Now", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryChipsRow(
    categories: List<CategoryEntity>,
    selectedCategory: String?,
    onCategorySelected: (String?) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        item {
            FilterChip(
                selected = selectedCategory == null,
                onClick = { onCategorySelected(null) },
                label = { Text("All Categories", fontSize = 12.sp) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.AllInclusive,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = BichkandElectricBlue,
                    selectedLabelColor = Color.White,
                    selectedLeadingIconColor = Color.White
                )
            )
        }

        items(categories) { cat ->
            val isSelected = selectedCategory == cat.name
            FilterChip(
                selected = isSelected,
                onClick = { onCategorySelected(if (isSelected) null else cat.name) },
                label = { Text(cat.name, fontSize = 12.sp) },
                leadingIcon = {
                    Icon(
                        imageVector = getCategoryIcon(cat.name),
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = BichkandElectricBlue,
                    selectedLabelColor = Color.White,
                    selectedLeadingIconColor = Color.White
                )
            )
        }
    }
}
