package com.example.ui.seller

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CategoryEntity
import com.example.data.model.ProductEntity
import com.example.ui.components.PakistanCities
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditProductScreen(
    existingProduct: ProductEntity?,
    categories: List<CategoryEntity>,
    onSaveProduct: (String, String, String, Double, Double?, Int, List<String>, String, String) -> Unit,
    onBack: () -> Unit
) {
    var title by remember { mutableStateOf(existingProduct?.title ?: "") }
    var category by remember { mutableStateOf(existingProduct?.category ?: (categories.firstOrNull()?.name ?: "Electronics")) }
    var subcategory by remember { mutableStateOf(existingProduct?.subcategory ?: "") }
    var priceText by remember { mutableStateOf(existingProduct?.price?.toInt()?.toString() ?: "") }
    var discountPriceText by remember { mutableStateOf(existingProduct?.discountPrice?.toInt()?.toString() ?: "") }
    var stockText by remember { mutableStateOf(existingProduct?.stock?.toString() ?: "15") }
    var variationsText by remember {
        mutableStateOf(
            existingProduct?.variationsJson?.trim()?.removeSurrounding("[", "]")?.replace("\"", "") ?: "Small, Medium, Large"
        )
    }
    var description by remember { mutableStateOf(existingProduct?.description ?: "") }
    var brand by remember { mutableStateOf(existingProduct?.brand ?: "Generic") }

    var categoryDropdownExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (existingProduct == null) "Add New Product" else "Edit Product", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Product Title") },
                    placeholder = { Text("e.g. Samsung Galaxy S24 Ultra 256GB") },
                    modifier = Modifier.fillMaxWidth().testTag("product_title_input")
                )
            }

            item {
                // Category Dropdown
                ExposedDropdownMenuBox(
                    expanded = categoryDropdownExpanded,
                    onExpandedChange = { categoryDropdownExpanded = !categoryDropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = category,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Category") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryDropdownExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = categoryDropdownExpanded,
                        onDismissRequest = { categoryDropdownExpanded = false }
                    ) {
                        categories.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat.name) },
                                onClick = {
                                    category = cat.name
                                    categoryDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            item {
                OutlinedTextField(
                    value = subcategory,
                    onValueChange = { subcategory = it },
                    label = { Text("Subcategory (Optional)") },
                    placeholder = { Text("e.g. Smartphones, Audio, Ethnic Wear") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = priceText,
                        onValueChange = { priceText = it },
                        label = { Text("Original Price (PKR)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f).testTag("product_price_input")
                    )
                    OutlinedTextField(
                        value = discountPriceText,
                        onValueChange = { discountPriceText = it },
                        label = { Text("Discount Price (PKR)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = stockText,
                        onValueChange = { stockText = it },
                        label = { Text("Available Stock") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = brand,
                        onValueChange = { brand = it },
                        label = { Text("Brand Name") },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                OutlinedTextField(
                    value = variationsText,
                    onValueChange = { variationsText = it },
                    label = { Text("Variations (Comma separated)") },
                    placeholder = { Text("e.g. Black, Silver, Gold or S, M, L") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Product Description & Specifications") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 4
                )
            }

            item {
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = {
                        val price = priceText.toDoubleOrNull() ?: 1000.0
                        val discountPrice = discountPriceText.toDoubleOrNull()
                        val stock = stockText.toIntOrNull() ?: 10
                        val variations = variationsText.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                        onSaveProduct(
                            title,
                            category,
                            subcategory,
                            price,
                            discountPrice,
                            stock,
                            variations,
                            description,
                            brand
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BichkandElectricBlue),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("submit_product_button"),
                    shape = RoundedCornerShape(10.dp),
                    enabled = title.isNotBlank() && priceText.isNotBlank()
                ) {
                    Text(if (existingProduct == null) "Publish Product to Marketplace" else "Save Changes", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellerRegistrationScreen(
    onRegister: (String, String, String, String, String, String, String, String) -> Unit,
    onBack: () -> Unit
) {
    var fullName by remember { mutableStateOf("") }
    var shopName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("+92 3") }
    var password by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var selectedCity by remember { mutableStateOf("Karachi") }
    var cityExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Open a Shop on BICHKAND", fontWeight = FontWeight.Bold, fontSize = 17.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Card(
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = BichkandLightBlueAccent),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("🚀 Sell to Millions of Pakistani Buyers", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = BichkandNavyDark)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "0% registration fee, 5% low marketplace commission, and nationwide logistics pickup from your shop or warehouse.",
                            fontSize = 11.sp,
                            color = BichkandTextPrimary
                        )
                    }
                }
            }

            item {
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Owner Full Name") },
                    placeholder = { Text("e.g. Zain Ul Abideen") },
                    modifier = Modifier.fillMaxWidth().testTag("seller_fullname_input")
                )
            }

            item {
                OutlinedTextField(
                    value = shopName,
                    onValueChange = { shopName = it },
                    label = { Text("Shop / Business Name") },
                    placeholder = { Text("e.g. Lahore Tech Store") },
                    modifier = Modifier.fillMaxWidth().testTag("seller_shopname_input")
                )
            }

            item {
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Business Email") },
                    placeholder = { Text("seller@example.pk") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Contact Phone Number (+92)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Account Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                // City Dropdown
                ExposedDropdownMenuBox(
                    expanded = cityExpanded,
                    onExpandedChange = { cityExpanded = !cityExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedCity,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Operating City") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = cityExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = cityExpanded,
                        onDismissRequest = { cityExpanded = false }
                    ) {
                        PakistanCities.filter { it != "All Pakistan" }.forEach { city ->
                            DropdownMenuItem(
                                text = { Text(city) },
                                onClick = {
                                    selectedCity = city
                                    cityExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            item {
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Physical Business Address / Warehouse") },
                    placeholder = { Text("e.g. Shop #12, Regal Trade Square, Saddar") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Shop Description") },
                    placeholder = { Text("Describe the products you specialize in...") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            }

            item {
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = {
                        onRegister(fullName, shopName, email, phone, password, description, address, selectedCity)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BichkandElectricBlue),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("submit_seller_registration_button"),
                    shape = RoundedCornerShape(10.dp),
                    enabled = fullName.isNotBlank() && shopName.isNotBlank() && email.isNotBlank()
                ) {
                    Text("Submit Application to BICHKAND", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
