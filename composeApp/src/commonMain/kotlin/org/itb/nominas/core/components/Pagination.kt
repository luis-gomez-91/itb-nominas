package org.itb.nominas.core.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import compose.icons.EvaIcons
import compose.icons.evaicons.Outline
import compose.icons.evaicons.outline.ChevronLeft
import compose.icons.evaicons.outline.ChevronRight
import org.itb.nominas.core.data.response.PagingResponse


@Composable
fun Pagination(
    isLoading: Boolean,
    paging: PagingResponse,
    onBack: () -> Unit,
    onNext: () -> Unit,
    actualPage: Int
) {
    Row (
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .background(color = MaterialTheme.colorScheme.surfaceContainer),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ){
        IconButton(
            onClick = {
                onBack()
            },
            enabled = actualPage > 1  && !isLoading
        ) {
            Icon(
                imageVector = EvaIcons.Outline.ChevronLeft,
                contentDescription = "Back",
                tint = if (actualPage > 1  && !isLoading) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.outlineVariant
            )
        }

        Text(
            text = "${actualPage}/${paging.lastPage}",
            style = MaterialTheme.typography.bodyLarge,
            color = if (!isLoading) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.outlineVariant
        )

        IconButton(
            onClick = {
                onNext()
            },
            enabled = actualPage < (paging.lastPage) && !isLoading
        ) {
            Icon(
                imageVector = EvaIcons.Outline.ChevronRight,
                contentDescription = "Next",
                tint = if (actualPage < (paging.lastPage)  && !isLoading) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.outlineVariant
            )
        }
    }
}