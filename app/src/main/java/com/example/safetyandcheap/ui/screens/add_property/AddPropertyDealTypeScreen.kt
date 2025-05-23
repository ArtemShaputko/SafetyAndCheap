package com.example.safetyandcheap.ui.screens.add_property

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.safetyandcheap.R
import com.example.safetyandcheap.service.DealType
import com.example.safetyandcheap.ui.theme.SafetyAndCheapTheme
import com.example.safetyandcheap.ui.util.AddPropertyCard
import com.example.safetyandcheap.viewmodel.AddAnnouncementViewModel

@Composable
fun AddPropertyDealTypeScreen(
    onSaleTypePressed: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AddAnnouncementViewModel = hiltViewModel()
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 10.dp, start = 16.dp, end = 16.dp)
    ) {
        AddPropertyCard(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                viewModel.selectDealType(DealType.Sale)
                onSaleTypePressed()
            }
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    modifier = Modifier.fillMaxHeight().offset(y = (-3).dp),
                    painter = painterResource(R.drawable.rent),
                    contentDescription = "Sale",
                    contentScale = ContentScale.FillHeight
                )
                Spacer(Modifier.width(15.dp))
                Text(
                    text = "Sale\nproperty",
                    style = TextStyle(
                        fontSize = 20.sp,
                        lineHeight = 22.sp,
                        fontWeight = FontWeight(400),
                        color = MaterialTheme.colorScheme.tertiary,
                    )
                )
            }
        }
    }
}

@Composable
@Preview
fun PropertyAddDealTypeScreenPreview() {
    SafetyAndCheapTheme {
        AddPropertyDealTypeScreen({})
    }
}