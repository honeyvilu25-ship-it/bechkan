package com.example.ui.buyer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CategoryEntity
import com.example.data.model.ProductEntity
import com.example.ui.components.CategoryChipsRow
import com.example.ui.components.ProductCard
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    initialQuery: String,
    initialCategory: String,
    allProducts: List<ProductEntity>,
    categories: List<CategoryEntity>,
    onProductClick: (String) -> Unit,
    onAddToCart: (ProductEntity) -> Unit,
    onBack: () -> Unit
) {
    var searchQuery by remember { mutableStateOf(initialQuery) }
    var selectedCategory by remember { mutableStateOf(if (initialCategory.isNotEmpty()) initialCategory else null) }
    var selectedPriceRange by remember { mutableStateOf("All") }
    var minRating by remember { mutableStateOf(0f) }
    var selectedSort by remember { mutableStateOf("Recommended") }
    var showFilterSheet by remember { mutableStateOf(false) }

    // Popular Pakistani search suggestions
    val popularTags = listOf("Galaxy S24", "Stitched Lawn", "Airbuds", "Cricket Bat", "Infinix", "Kurta", "Servis Shoes", "Shan Masala")

    // Filter products
    val filteredProducts = remember(allProducts, searchQuery, selectedCategory, selectedPriceRange, minRating, selectedSort) {
        var list = allProducts.filter { product ->
            val matchesQuery = searchQuery.isBlank() ||
                    product.title.contains(searchQuery, ignoreCase = true) ||
                    product.description.contains(searchQuery, ignoreCase = true) ||
                    product.category.contains(searchQuery, ignoreCase = true) ||
                    product.brand.contains(searchQuery, ignoreCase = true) ||
                    product.cityLocation.contains(searchQuery, ignoreCase = true)

            val matchesCategory = selectedCategory == null || product.category.equals(selectedCategory, ignoreCase = true)

            val price = product.displayPrice
            val matchesPrice = when (selectedPriceRange) {
                "Under 5k" -> price < 5000
                "5k - 20k" -> price in 5000.0..20000.0
                "20k - 100k" -> price in 20000.0..100000.0
                "Above 100k" -> price > 100000
                else -> true
            }

            val matchesRating = product.rating >= minRating

            matchesQuery && matchesCategory && matchesPrice && matchesRating
        }

        list = when (selectedSort) {
            "Price: Low to High" -> list.sortedBy { it.displayPrice }
            "Price: High to Low" -> list.sortedByDescending { it.displayPrice }
            "Top Rated" -> list.sortedByDescending { it.rating }
            else -> list.sortedByDescending { it.createdAt }
        }

        list
    }

    Scaffold(
        topBar = {
            Surface(
                color = BichkandNavyDark,
                tonalElevation = 4.dp
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }

                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Search products, brands, shops...", fontSize = 13.sp) },
                            singleLine = true,
                            leadingIcon = {
                                Icon(Icons.Default.Search, contentDescription = null, tint = BichkandElectricBlue)
                            },
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { searchQuery = "" }) {
                                        Icon(Icons.Default.Close, contentDescription = "Clear", tint = Color.Gray)
                                    }
                                }
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedBorderColor = BichkandElectricBlue,
                                unfocusedBorderColor = Color.Transparent
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                                .testTag("search_input_field")
                        )

                        Spacer(modifier = Modifier.width(6.dp))

                        IconButton(
                            onClick = { showFilterSheet = true },
                            modifier = Modifier.testTag("open_filters_button")
                        ) {
                            Icon(
                                Icons.Default.FilterList,
                                contentDescription = "Filters",
                                tint = if (selectedPriceRange != "All" || minRating > 0f) BichkandAmber else Color.White
                            )
                        }
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
            contentPadding = PaddingValues(bottom = 60.dp)
        ) {
            // Popular Pakistani Search Tags
            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(popularTags) { tag ->
                        Surface(
                            onClick = { searchQuery = tag },
                            shape = RoundedCornerShape(16.dp),
                            color = if (searchQuery.equals(tag, ignoreCase = true)) BichkandElectricBlue else Color.White,
                            border = androidx.compose.foundation.BorderStroke(1.dp, BichkandBorder)
                        ) {
                            Text(
                                text = tag,
                                fontSize = 11.sp,
                                color = if (searchQuery.equals(tag, ignoreCase = true)) Color.White else BichkandTextPrimary,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }
                }
            }

            // Categories horizontal bar
            item {
                CategoryChipsRow(
                    categories = categories,
                    selectedCategory = selectedCategory,
                    onCategorySelected = { selectedCategory = it }
                )
            }

            item { Spacer(modifier = Modifier.height(10.dp)) }

            // Active filters & Result count
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${filteredProducts.size} Products found",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = BichkandNavyDark
                    )

                    // Sort dropdown button
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable {
                            selectedSort = when (selectedSort) {
                                "Recommended" -> "Price: Low to High"
                                "Price: Low to High" -> "Price: High to Low"
                                "Price: High to Low" -> "Top Rated"
                                else -> "Recommended"
                            }
                        }
                    ) {
                        Text(
                            text = "Sort: $selectedSort",
                            fontSize = 11.sp,
                            color = BichkandElectricBlue,
                            fontWeight = FontWeight.SemiBold
                        )
                        Icon(
                            imageVector = Icons.Default.SwapVert,
                            contentDescription = null,
                            tint = BichkandElectricBlue,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // Empty State
            if (filteredProducts.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.SearchOff,
                            contentDescription = null,
                            tint = BichkandTextSecondary,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No products found",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = BichkandNavyDark
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Try adjusting your search terms or clearing your filters.",
                            fontSize = 12.sp,
                            color = BichkandTextSecondary,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                searchQuery = ""
                                selectedCategory = null
                                selectedPriceRange = "All"
                                minRating = 0f
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = BichkandElectricBlue)
                        ) {
                            Text("Reset All Filters")
                        }
                    }
                }
            } else {
                // Two column grid
                val chunks = filteredProducts.chunked(2)
                items(chunks) { row ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        for (prod in row) {
                            Box(modifier = Modifier.weight(1f)) {
                                ProductCard(
                                    product = prod,
                                    onClick = { onProductClick(prod.id) },
                                    onAddToCart = { onAddToCart(prod) }
                                )
                            }
                        }
                        if (row.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }

    // Filter Bottom Sheet
    if (showFilterSheet) {
        ModalBottomSheet(
            onDismissRequest = { showFilterSheet = false },
            containerColor = BichkandSurfaceLight
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .navigationBarsPadding()
            ) {
                Text(
                    text = "Filter Products",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = BichkandNavyDark
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Price Range
                Text("Price in PKR", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(8.dp))
                val ranges = listOf("All", "Under 5k", "5k - 20k", "20k - 100k", "Above 100k")
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(ranges) { r ->
                        FilterChip(
                            selected = selectedPriceRange == r,
                            onClick = { selectedPriceRange = r },
                            label = { Text(r, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = BichkandElectricBlue,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Rating
                Text("Customer Rating", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = minRating == 0f,
                        onClick = { minRating = 0f },
                        label = { Text("All", fontSize = 11.sp) }
                    )
                    FilterChip(
                        selected = minRating == 4f,
                        onClick = { minRating = 4f },
                        label = { Text("4★ & above", fontSize = 11.sp) },
                        leadingIcon = { Icon(Icons.Default.Star, contentDescription = null, tint = BichkandAmber, modifier = Modifier.size(14.dp)) }
                    )
                    FilterChip(
                        selected = minRating == 4.5f,
                        onClick = { minRating = 4.5f },
                        label = { Text("4.5★ & above", fontSize = 11.sp) },
                        leadingIcon = { Icon(Icons.Default.Star, contentDescription = null, tint = BichkandAmber, modifier = Modifier.size(14.dp)) }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { showFilterSheet = false },
                    colors = ButtonDefaults.buttonColors(containerColor = BichkandElectricBlue),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Apply Filters", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
