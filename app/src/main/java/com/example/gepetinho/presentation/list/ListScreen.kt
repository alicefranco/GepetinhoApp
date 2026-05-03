package com.example.gepetinho.presentation.list

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.gepetinho.R
import com.example.gepetinho.domain.model.Pokemon
import com.example.gepetinho.ui.theme.GepetinhoTheme

@Composable
fun ListRoute(
    viewModel: ListViewModel,
    onPokemonClick: (Int) -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ListScreen(
        uiState = uiState,
        onSearchQueryChanged = viewModel::onSearchQueryChanged,
        onFavoritesOnlyChanged = viewModel::onFavoritesOnlyChanged,
        onPokemonClick = onPokemonClick,
        onFavoriteClick = viewModel::toggleFavorite,
        onRetryClick = viewModel::refreshPokemon,
        onDismissError = viewModel::clearError,
        onLogout = onLogout,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListScreen(
    uiState: ListUiState,
    onSearchQueryChanged: (String) -> Unit,
    onFavoritesOnlyChanged: (Boolean) -> Unit,
    onPokemonClick: (Int) -> Unit,
    onFavoriteClick: (Int) -> Unit,
    onRetryClick: () -> Unit,
    onDismissError: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Pokemon",
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Text(
                        text = "${uiState.items.size} shown",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                TextButton(onClick = onLogout) {
                    Text(text = "Logout")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = onSearchQueryChanged,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Search") },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilterChip(
                    selected = uiState.showFavoritesOnly,
                    onClick = {
                        onFavoritesOnlyChanged(!uiState.showFavoritesOnly)
                    },
                    label = { Text("Favorites") }
                )

                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(28.dp),
                        strokeWidth = 2.dp
                    )
                }
            }

            uiState.errorMessage?.let { message ->
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = message,
                        modifier = Modifier.weight(1f),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    TextButton(onClick = onDismissError) {
                        Text(text = "Dismiss")
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            when {
                uiState.items.isEmpty() && uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                uiState.items.isEmpty() -> {
                    EmptyListState(
                        showFavoritesOnly = uiState.showFavoritesOnly,
                        searchQuery = uiState.searchQuery,
                        onRetryClick = onRetryClick,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(
                            items = uiState.items,
                            key = { it.id }
                        ) { pokemon ->
                            PokemonRow(
                                pokemon = pokemon,
                                onClick = { onPokemonClick(pokemon.id) },
                                onFavoriteClick = { onFavoriteClick(pokemon.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PokemonRow(
    pokemon: Pokemon,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        tonalElevation = 1.dp,
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = pokemon.imageUrl,
                    contentDescription = "${pokemon.name} image",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(4.dp),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = pokemon.name.replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "No. ${pokemon.id}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            IconButton(onClick = onFavoriteClick) {
                Image(
                    painter = painterResource(
                        id = if (pokemon.isFavorite) {
                            R.drawable.heart
                        } else {
                            R.drawable.heart_outline
                        }
                    ),
                    contentDescription = if (pokemon.isFavorite) {
                        "Remove favorite"
                    } else {
                        "Add favorite"
                    },
                    modifier = Modifier.size(26.dp)
                )
            }
        }
    }
}

@Composable
private fun EmptyListState(
    showFavoritesOnly: Boolean,
    searchQuery: String,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val message = when {
                showFavoritesOnly -> "No favorite Pokemon found."
                searchQuery.isNotBlank() -> "No Pokemon match your search."
                else -> "No Pokemon loaded yet."
            }

            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge
            )
            Button(onClick = onRetryClick) {
                Text(text = "Retry")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ListScreenPreview() {
    GepetinhoTheme {
        ListScreen(
            uiState = ListUiState(
                items = listOf(
                    Pokemon(
                        id = 1,
                        name = "bulbasaur",
                        imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/1.png",
                        isFavorite = true
                    ),
                    Pokemon(
                        id = 4,
                        name = "charmander",
                        imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/4.png",
                        isFavorite = false
                    )
                )
            ),
            onSearchQueryChanged = {},
            onFavoritesOnlyChanged = {},
            onPokemonClick = {},
            onFavoriteClick = {},
            onRetryClick = {},
            onDismissError = {},
            onLogout = {}
        )
    }
}
