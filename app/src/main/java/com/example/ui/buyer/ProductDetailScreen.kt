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
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ProductEntity
import com.example.data.model.ReviewEntity
import com.example.ui.components.ProductCard
import com.example.ui.components.formatPkr
import com.example.ui.components.getCategoryIcon
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    product: ProductEntity,
    reviews: List<ReviewEntity>,
    relatedProducts: List<ProductEntity>,
    isInWishlist: Boolean,
    onAddToCart: (Int, String) -> Unit,
    onBuyNow: (Int, String) -> Unit,
    onToggleWishlist: () -> Unit,
    onAddReview: (Int, String) -> Unit,
    onShopClick: (String) -> Unit,
    onRelatedProductClick: (String) -> Unit,
    onBack: () -> Unit
) {
    var quantity by remember { mutableStateOf(1) }
    var selectedVariationIndex by remember { mutableStateOf(0) }
    var showReviewDialog by remember { mutableStateOf(false) }
    var newReviewRating by remember { mutableStateOf(5) }
    var newReviewComment by remember { mutableStateOf("") }

    // Parse variations JSON
    val variations = remember(product.variationsJson) {
        val cleaned = product.variationsJson.trim().removeSurrounding("[", "]")
        if (cleaned.isNotBlank()) {
            cleaned.split(",").map { it.trim().removeSurrounding("\"") }
        } else {
            listOf("Standard")
        }
    }
    val currentVariation = variations.getOrElse(selectedVariationIndex) { "Standard" }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(product.title, maxLines = 1, fontSize = 16.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = onToggleWishlist,
                        modifier = Modifier.testTag("detail_wishlist_button")
                    ) {
                        Icon(
                            imageVector = if (isInWishlist) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Wishlist",
                            tint = if (isInWishlist) BichkandRose else BichkandNavyDark
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BichkandSurfaceLight)
            )
        },
        bottomBar = {
            // Sticky Add to Cart & Buy Now Bar
            Surface(
                color = BichkandSurfaceLight,
                tonalElevation = 12.dp,
                modifier = Modifier.navigationBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Quantity selector
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .background(Color(0xFFF1F5F9), RoundedCornerShape(8.dp))
                            .padding(4.dp)
                    ) {
                        IconButton(
                            onClick = { if (quantity > 1) quantity-- },
                            modifier = Modifier.size(30.dp)
                        ) {
                            Icon(Icons.Default.Remove, contentDescription = "Decrease", modifier = Modifier.size(16.dp))
                        }
                        Text(
                            text = "$quantity",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                        IconButton(
                            onClick = { if (quantity < product.stock) quantity++ },
                            modifier = Modifier.size(30.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Increase", modifier = Modifier.size(16.dp))
                        }
                    }

                    // Add to Cart
                    OutlinedButton(
                        onClick = { onAddToCart(quantity, currentVariation) },
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("detail_add_to_cart_button"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = BichkandElectricBlue)
                    ) {
                        Icon(Icons.Default.AddShoppingCart, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Add to Cart", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    // Buy Now
                    Button(
                        onClick = { onBuyNow(quantity, currentVariation) },
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("detail_buy_now_button"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BichkandElectricBlue)
                    ) {
                        Text("Buy Now", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(BichkandBgLight),
            contentPadding = PaddingValues(bottom = 30.dp)
        ) {
            // 1. Hero Image Stage with Visual Icon
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFFE2E8F0), Color(0xFFCBD5E1))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    val icon = getCategoryIcon(product.category)
                    Icon(
                        imageVector = icon,
                        contentDescription = product.title,
                        tint = BichkandNavyDark.copy(alpha = 0.5f),
                        modifier = Modifier.size(96.dp)
                    )

                    // Badges
                    if (product.discountPercentage > 0) {
                        Surface(
                            color = BichkandRose,
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "-${product.discountPercentage}% OFF",
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    // City Location Badge
                    Surface(
                        color = Color.Black.copy(alpha = 0.7f),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = BichkandAmber, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Dispatches from ${product.cityLocation}", color = Color.White, fontSize = 11.sp)
                        }
                    }
                }
            }

            // 2. Price and Title Card
            item {
                Card(
                    shape = RoundedCornerShape(0.dp),
                    colors = CardDefaults.cardColors(containerColor = BichkandSurfaceLight),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.Bottom,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column {
                                Text(
                                    text = formatPkr(product.displayPrice),
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.Black,
                                    color = BichkandElectricBlue
                                )
                                if (product.discountPrice != null && product.discountPrice < product.price) {
                                    Text(
                                        text = formatPkr(product.price),
                                        fontSize = 14.sp,
                                        color = BichkandTextSecondary,
                                        textDecoration = TextDecoration.LineThrough
                                    )
                                }
                            }

                            // Stock status
                            Surface(
                                color = if (product.stock > 0) BichkandGreen.copy(alpha = 0.15f) else BichkandRose.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = if (product.stock > 0) "In Stock (${product.stock})" else "Out of Stock",
                                    color = if (product.stock > 0) BichkandGreen else BichkandRose,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = product.title,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = BichkandNavyDark,
                            lineHeight = 22.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Rating & Reviews row
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Star, contentDescription = null, tint = BichkandAmber, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${product.rating} / 5.0",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = BichkandTextPrimary
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "(${product.reviewCount} customer reviews)",
                                    fontSize = 12.sp,
                                    color = BichkandTextSecondary
                                )
                            }

                            Text(
                                text = "Brand: ${product.brand}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = BichkandElectricBlue
                            )
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(8.dp)) }

            // 3. Variations Selector (if available)
            if (variations.isNotEmpty()) {
                item {
                    Card(
                        shape = RoundedCornerShape(0.dp),
                        colors = CardDefaults.cardColors(containerColor = BichkandSurfaceLight),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Select Variation: $currentVariation",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = BichkandNavyDark
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(variations.indices.toList()) { index ->
                                    val isSelected = selectedVariationIndex == index
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = { selectedVariationIndex = index },
                                        label = { Text(variations[index], fontSize = 12.sp) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = BichkandElectricBlue,
                                            selectedLabelColor = Color.White
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
                item { Spacer(modifier = Modifier.height(8.dp)) }
            }

            // 4. Delivery & Guarantees
            item {
                Card(
                    shape = RoundedCornerShape(0.dp),
                    colors = CardDefaults.cardColors(containerColor = BichkandSurfaceLight),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocalShipping, contentDescription = null, tint = BichkandElectricBlue, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("Standard Delivery (2-4 Working Days)", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                Text("Rs. 199 across Pakistan (Free over Rs. 2,500)", color = BichkandTextSecondary, fontSize = 11.sp)
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = BichkandGreen, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("100% Authentic Guaranteed", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                Text("7-Day Easy Return & Warranty Policy", color = BichkandTextSecondary, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(8.dp)) }

            // 5. Seller / Shop Card
            item {
                Card(
                    shape = RoundedCornerShape(0.dp),
                    colors = CardDefaults.cardColors(containerColor = BichkandSurfaceLight),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(BichkandNavyDark),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Storefront, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(product.shopName, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = BichkandNavyDark)
                                Text("Verified Pakistani Seller", fontSize = 11.sp, color = BichkandGreen)
                            }
                        }

                        OutlinedButton(
                            onClick = { onShopClick(product.shopId) },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Text("Visit Shop", fontSize = 11.sp)
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(8.dp)) }

            // 6. Product Description & Specifications
            item {
                Card(
                    shape = RoundedCornerShape(0.dp),
                    colors = CardDefaults.cardColors(containerColor = BichkandSurfaceLight),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Product Description & Specs", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = BichkandNavyDark)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = product.description,
                            fontSize = 13.sp,
                            color = BichkandTextPrimary,
                            lineHeight = 18.sp
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Text("Category: ", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text(product.category, fontSize = 12.sp, color = BichkandTextSecondary)
                        }
                        if (product.subcategory.isNotBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(modifier = Modifier.fillMaxWidth()) {
                                Text("Subcategory: ", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text(product.subcategory, fontSize = 12.sp, color = BichkandTextSecondary)
                            }
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(8.dp)) }

            // 7. Customer Ratings & Reviews
            item {
                Card(
                    shape = RoundedCornerShape(0.dp),
                    colors = CardDefaults.cardColors(containerColor = BichkandSurfaceLight),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Customer Reviews (${reviews.size})", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            TextButton(onClick = { showReviewDialog = true }) {
                                Icon(Icons.Default.RateReview, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Write Review", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        if (reviews.isEmpty()) {
                            Text(
                                text = "No reviews yet. Be the first to review this product!",
                                fontSize = 12.sp,
                                color = BichkandTextSecondary,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        } else {
                            reviews.forEach { rev ->
                                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(rev.userName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Row {
                                            repeat(rev.rating) {
                                                Icon(Icons.Default.Star, contentDescription = null, tint = BichkandAmber, modifier = Modifier.size(14.dp))
                                            }
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(rev.comment, fontSize = 12.sp, color = BichkandTextPrimary)
                                    HorizontalDivider(modifier = Modifier.padding(top = 8.dp), color = Color(0xFFF1F5F9))
                                }
                            }
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }

            // 8. Related Products
            if (relatedProducts.isNotEmpty()) {
                item {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Related Products",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = BichkandNavyDark,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(relatedProducts) { rel ->
                                ProductCard(
                                    product = rel,
                                    onClick = { onRelatedProductClick(rel.id) },
                                    onAddToCart = { /* Handled in parent */ },
                                    modifier = Modifier.width(170.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Write Review Dialog
    if (showReviewDialog) {
        AlertDialog(
            onDismissRequest = { showReviewDialog = false },
            title = { Text("Rate & Review Product") },
            text = {
                Column {
                    Text("Select Rating:")
                    Spacer(modifier = Modifier.height(6.dp))
                    Row {
                        (1..5).forEach { star ->
                            IconButton(onClick = { newReviewRating = star }) {
                                Icon(
                                    imageVector = if (star <= newReviewRating) Icons.Default.Star else Icons.Outlined.StarOutline,
                                    contentDescription = "$star Stars",
                                    tint = BichkandAmber
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newReviewComment,
                        onValueChange = { newReviewComment = it },
                        placeholder = { Text("Write your honest feedback on product quality, packaging, and delivery...") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newReviewComment.isNotBlank()) {
                            onAddReview(newReviewRating, newReviewComment)
                            showReviewDialog = false
                            newReviewComment = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BichkandElectricBlue)
                ) {
                    Text("Submit Review")
                }
            },
            dismissButton = {
                TextButton(onClick = { showReviewDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
